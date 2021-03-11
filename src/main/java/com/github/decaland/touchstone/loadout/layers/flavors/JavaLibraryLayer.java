package com.github.decaland.touchstone.loadout.layers.flavors;

import com.github.decaland.touchstone.loadout.Loadout;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaLibraryDistributionPlugin;

public class JavaLibraryLayer extends JavaLayer {

    public JavaLibraryLayer() {
    }

    @Override
    public void apply(Project project, Loadout.Layers layers) {
        project.getPluginManager().apply(JavaLibraryDistributionPlugin.class);
    }
}
