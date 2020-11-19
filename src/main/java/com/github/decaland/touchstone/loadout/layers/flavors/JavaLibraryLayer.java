package com.github.decaland.touchstone.loadout.layers.flavors;

import com.github.decaland.touchstone.loadout.layers.Layer;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaLibraryPlugin;

import java.util.Collection;

public class JavaLibraryLayer extends JavaLayer {

    public JavaLibraryLayer(Project project, Collection<Layer> layers) {
        super(project, layers);
    }

    @Override
    public void applyLayer() {
        pluginManager.apply(JavaLibraryPlugin.class);
    }
}
