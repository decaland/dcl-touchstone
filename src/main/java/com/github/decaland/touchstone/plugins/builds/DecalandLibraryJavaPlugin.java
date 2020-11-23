package com.github.decaland.touchstone.plugins.builds;

import com.github.decaland.touchstone.loadout.Loadout;
import com.github.decaland.touchstone.loadout.layers.configs.DependencyManagementLayer;
import com.github.decaland.touchstone.loadout.layers.configs.MavenPublishLayer;
import com.github.decaland.touchstone.loadout.layers.configs.RepositoryConfigurationLayer;
import com.github.decaland.touchstone.loadout.layers.flavors.JavaLibraryLayer;
import com.github.decaland.touchstone.plugins.DecalandBuildConfigPlugin;
import com.github.decaland.touchstone.plugins.DecalandPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Arranges for the consuming project to be built as a non-executable,
 * consumable library written in Java.
 */
public class DecalandLibraryJavaPlugin extends DecalandBuildConfigPlugin {

    @Override
    protected void configurePluginLoadout(Loadout pluginLoadout) {
        pluginLoadout.addLayer(RepositoryConfigurationLayer.class);
        pluginLoadout.addLayer(DependencyManagementLayer.class);
        pluginLoadout.addLayer(MavenPublishLayer.class);
        pluginLoadout.addLayer(JavaLibraryLayer.class);
    }

    @NotNull
    @Override
    protected Class<? extends DecalandPlugin> getPluginType() {
        return DecalandLibraryJavaPlugin.class;
    }
}
