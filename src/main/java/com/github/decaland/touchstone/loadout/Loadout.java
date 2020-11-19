package com.github.decaland.touchstone.loadout;

import com.github.decaland.touchstone.loadout.layers.Layer;
import org.gradle.api.GradleException;
import org.gradle.api.Project;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public class Loadout {

    private static final String MSG_FAILED_TO_INSTANTIATE_LAYER
            = "Touchstone plugin failed to instantiate loadout layer class '%s'";

    private final Project project;
    private final Collection<Layer> layers;

    public Loadout(Project project) {
        this.project = project;
        this.layers = new ArrayList<>();
    }

    public void putOn() {
        if (layers.isEmpty()) return;
        boolean anyApplied;
        do {
            anyApplied = false;
            for (Layer layer : layers) {
                if (!layer.isApplied() && layer.readyForApplication(layers)) {
                    layer.applyLayer();
                    layer.markApplied();
                    anyApplied = true;
                }
            }
        } while (anyApplied);
        for (Layer layer : layers) {
            if (layer.isApplied()) {
                layer.configureLayer();
            }
        }
    }

    public <T extends Layer> void addLayer(Class<T> clazz) {
        addLayer(clazz, layer -> {
        });
    }

    public <T extends Layer> void addLayer(Class<T> clazz, Consumer<T> configurer) {
        T layer = createLayer(clazz);
        configurer.accept(layer);
        layers.add(layer);
    }

    private <T extends Layer> T createLayer(Class<T> clazz) {
        try {
            return clazz
                    .getDeclaredConstructor(Project.class, Collection.class)
                    .newInstance(this.project, this.layers);
        } catch (ReflectiveOperationException e) {
            throw new GradleException(String.format(MSG_FAILED_TO_INSTANTIATE_LAYER, clazz.getSimpleName()));
        }
    }
}
