package com.github.decaland.touchstone.plugins.builds;

import com.github.decaland.touchstone.loadout.Loadout;
import com.github.decaland.touchstone.loadout.layers.configs.DependencyManagementLayer;
import com.github.decaland.touchstone.loadout.layers.configs.MavenPublishLayer;
import com.github.decaland.touchstone.loadout.layers.configs.RepositoryConfigurationLayer;
import com.github.decaland.touchstone.loadout.layers.flavors.JavaLibraryLayer;
import com.github.decaland.touchstone.plugins.DecalandBuildConfigPlugin;
import com.github.decaland.touchstone.plugins.DecalandPlugin;
import org.jetbrains.annotations.NotNull;

public class DecalandLibraryJavaPlugin extends DecalandBuildConfigPlugin {

    @Override
    protected Loadout.Builder configurePluginLoadout(Loadout.Builder loadoutBuilder) {
        return loadoutBuilder
                .addLayer(RepositoryConfigurationLayer.class)
                .addLayer(DependencyManagementLayer.class)
                .addLayer(MavenPublishLayer.class)
                .addLayer(JavaLibraryLayer.class);
    }

    @NotNull
    @Override
    protected Class<? extends DecalandPlugin> getPluginType() {
        return DecalandLibraryJavaPlugin.class;
    }
}
