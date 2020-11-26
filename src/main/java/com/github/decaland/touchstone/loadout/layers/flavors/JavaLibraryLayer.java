package com.github.decaland.touchstone.loadout.layers.flavors;

import com.github.decaland.touchstone.loadout.layers.LayerAccumulator;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaLibraryPlugin;

public class JavaLibraryLayer extends JavaLayer {

    public JavaLibraryLayer(Project project, LayerAccumulator.Finalized layers) {
        super(project, layers);
    }

    @Override
    public void applyLayer() {
        pluginManager.apply(JavaLibraryPlugin.class);
    }
}
