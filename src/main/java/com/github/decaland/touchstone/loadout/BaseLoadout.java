package com.github.decaland.touchstone.loadout;

import com.github.decaland.touchstone.loadout.layers.Layer;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class BaseLoadout implements Loadout {

    private static final String MSG_FAILED_TO_APPLY_ALL_LAYERS
            = "Touchstone plugin '%s' failed to apply loadout layers: %s";

    private final Layers layers;

    private BaseLoadout(Layers layers) {
        this.layers = layers;
    }

    @NotNull
    public static BaseBuilder builder() {
        return new BaseBuilder();
    }

    @Override
    public void apply(Project project, String pluginId) {
        LinkedHashSet<Layer> appliedLayers = applyLayers(project);
        ensureAllLayersApplied(appliedLayers, pluginId);
        configureAppliedLayers(appliedLayers, project);
    }

    @Override
    public Layers layers() {
        return layers;
    }

    @NotNull
    private LinkedHashSet<Layer> applyLayers(Project project) {
        boolean anyApplied;
        List<Layer> layerList = layers.stream().collect(Collectors.toList());
        LinkedHashSet<Layer> appliedLayers = new LinkedHashSet<>();
        do {
            anyApplied = false;
            for (Layer layer : layerList) {
                if (!appliedLayers.contains(layer) && layer.isReady(project, layers)) {
                    project.getLogger().info(String.format(
                            LIFECYCLE_LOG_APPLY_LAYER,
                            layer.getClass().getSimpleName()
                    ));
                    layer.apply(project, layers);
                    anyApplied = true;
                    appliedLayers.add(layer);
                }
            }
        } while (anyApplied);
        return appliedLayers;
    }

    private void ensureAllLayersApplied(@NotNull Set<Layer> appliedLayers, String pluginId) {
        Set<Layer> unappliedLayers = layers.stream()
                .filter(layer -> !appliedLayers.contains(layer))
                .collect(Collectors.toSet());
        if (!unappliedLayers.isEmpty()) {
            throw new GradleException(String.format(
                    MSG_FAILED_TO_APPLY_ALL_LAYERS,
                    pluginId,
                    unappliedLayers.stream()
                            .map(l -> l.getClass().getSimpleName())
                            .collect(joining("', '", "'", "'"))
            ));
        }
    }

    private void configureAppliedLayers(@NotNull LinkedHashSet<Layer> appliedLayers, Project project) {
        for (Layer appliedLayer : appliedLayers) {
            project.getLogger().info(String.format(
                    LIFECYCLE_LOG_CONFIGURE_LAYER,
                    appliedLayer.getClass().getSimpleName()
            ));
            appliedLayer.configure(project, layers);
        }
    }

    public static class BaseBuilder implements Builder {

        private final Collection<Layer> innerLayers;
        private final Layers layers;

        private BaseBuilder() {
            this.innerLayers = new LinkedList<>();
            this.layers = new BaseLayers(innerLayers);
        }

        @Override
        public Builder add(Layer layer) {
            innerLayers.add(layer);
            return this;
        }

        @Override
        @NotNull
        public final BaseLoadout build() {
            return new BaseLoadout(layers);
        }
    }

    private static class BaseLayers implements Layers {

        private final Collection<Layer> layers;

        private BaseLayers(Collection<Layer> layers) {
            this.layers = layers;
        }

        @Override
        public Stream<Layer> stream() {
            return layers.stream();
        }
    }
}
