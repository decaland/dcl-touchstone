package com.github.decaland.touchstone.loadout;

import com.github.decaland.touchstone.loadout.layers.FinalizedLayers;
import com.github.decaland.touchstone.loadout.layers.Layer;
import com.github.decaland.touchstone.loadout.layers.LayerAccumulator;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

public final class Loadout {

    private static final String MSG_FAILED_TO_APPLY_ALL_LAYERS
            = "Touchstone plugin '%s' failed to apply loadout layers: %s";
    private static final String MSG_FAILED_TO_INSTANTIATE_LAYER
            = "Touchstone plugin '%s' failed to instantiate loadout layer class '%s'";

    private final Project project;
    private final String pluginId;
    private final FinalizedLayers layers;
    private boolean isPutOn = false;

    private Loadout(Project project, String pluginId, FinalizedLayers layers) {
        this.project = project;
        this.pluginId = pluginId;
        this.layers = layers;
    }

    @NotNull
    public static Loadout.Builder builder(Project project, String pluginId) {
        return new Builder(project, pluginId);
    }

    public final void putOn() {
        if (isPutOn) return;
        Set<Layer> appliedLayers = applyLayers();
        ensureAllLayersApplied(appliedLayers);
        for (Layer appliedLayer : appliedLayers) {
            project.getLogger().lifecycle(
                    String.format("Configure Touchstone layer '%s'", appliedLayer.getClass().getSimpleName())
            );
            appliedLayer.configureLayer();
        }
        isPutOn = true;
    }

    @NotNull
    private Set<Layer> applyLayers() {
        boolean anyApplied;
        Set<Layer> appliedLayers = new LinkedHashSet<>();
        do {
            anyApplied = false;
            for (Layer layer : layers.asUnmodifiableLinkedSet()) {
                if (!layer.isApplied() && layer.readyForApplication()) {
                    project.getLogger().lifecycle(
                            String.format("Apply Touchstone layer     '%s'", layer.getClass().getSimpleName())
                    );
                    layer.applyLayer();
                    layer.markAsApplied();
                    anyApplied = true;
                    appliedLayers.add(layer);
                }
            }
        } while (anyApplied);
        return appliedLayers;
    }

    private void ensureAllLayersApplied(Set<Layer> appliedLayers) {
        Set<Layer> unappliedLayers = layers.asUnmodifiableLinkedSet().stream()
                .filter(layer -> !appliedLayers.contains(layer))
                .collect(Collectors.toSet());
        if (unappliedLayers.isEmpty()) return;
        throw new GradleException(String.format(
                MSG_FAILED_TO_APPLY_ALL_LAYERS,
                pluginId,
                unappliedLayers.stream()
                        .map(l -> l.getClass().getSimpleName())
                        .collect(joining("', '", "'", "'"))
        ));
    }

    public static final class Builder {

        private final Project project;
        private final String pluginId;
        private final LayerAccumulator layers;

        private Builder(Project project, String pluginId) {
            this.project = project;
            this.pluginId = pluginId;
            this.layers = new LayerAccumulator();
        }

        public final <T extends Layer> Builder addLayer(Class<T> clazz) {
            return addLayer(clazz, layer -> {
            });
        }

        public final <T extends Layer> Builder addLayer(Class<T> clazz, @NotNull Consumer<T> configurer) {
            T layer = createLayer(clazz);
            configurer.accept(layer);
            layers.add(layer);
            return this;
        }

        public final <T extends Layer> Builder removeLayer(Class<T> clazz) {
            layers.removeMatching(clazz);
            return this;
        }

        public final <O extends Layer, I extends Layer> Builder swapLayer(
                Class<O> outgoing, Class<I> incoming
        ) {
            return swapLayer(outgoing, incoming, layer -> {
            });
        }

        public final <O extends Layer, I extends Layer> Builder swapLayer(
                Class<O> outgoing, Class<I> incoming, @NotNull Consumer<I> configurer
        ) {
            I replacement = createLayer(incoming);
            configurer.accept(replacement);
            layers.swapMatching(outgoing, replacement);
            return this;
        }

        public final <O extends Layer, I extends Layer> Builder swapOrAddLayer(
                Class<O> outgoing, Class<I> incoming
        ) {
            return swapOrAddLayer(outgoing, incoming, layer -> {
            });
        }

        public final <O extends Layer, I extends Layer> Builder swapOrAddLayer(
                Class<O> outgoing, Class<I> incoming, @NotNull Consumer<I> configurer
        ) {
            I replacement = createLayer(incoming);
            configurer.accept(replacement);
            layers.swapMatchingOrAdd(outgoing, replacement);
            return this;
        }

        @NotNull
        public final Loadout build() {
            return new Loadout(this.project, this.pluginId, layers.finalized());
        }

        @NotNull
        private <T extends Layer> T createLayer(@NotNull Class<T> clazz) {
            try {
                return clazz
                        .getDeclaredConstructor(Project.class, FinalizedLayers.class)
                        .newInstance(project, layers.pending());
            } catch (ReflectiveOperationException e) {
                throw new GradleException(String.format(
                        MSG_FAILED_TO_INSTANTIATE_LAYER,
                        pluginId,
                        clazz.getSimpleName()
                ));
            }
        }
    }
}
