package com.github.decaland.touchstone.plugins.builds;

import com.github.decaland.touchstone.loadout.Loadout;
import com.github.decaland.touchstone.loadout.layers.configs.DependencyManagementLayer;
import com.github.decaland.touchstone.loadout.layers.configs.MavenPublishLayer;
import com.github.decaland.touchstone.loadout.layers.configs.RepositoryConfigurationLayer;
import com.github.decaland.touchstone.loadout.layers.flavors.JavaLayer;
import com.github.decaland.touchstone.loadout.layers.flavors.SpringBootLayer;
import com.github.decaland.touchstone.plugins.DecalandBuildConfigPlugin;
import com.github.decaland.touchstone.plugins.DecalandPlugin;
import org.jetbrains.annotations.NotNull;

public class DecalandSpringBootApplicationJavaPlugin extends DecalandBuildConfigPlugin {

    @Override
    protected void configurePluginLoadout(Loadout pluginLoadout) {
        pluginLoadout.addLayer(RepositoryConfigurationLayer.class);
        pluginLoadout.addLayer(DependencyManagementLayer.class);
        pluginLoadout.addLayer(MavenPublishLayer.class);
        pluginLoadout.addLayer(JavaLayer.class);
        pluginLoadout.addLayer(SpringBootLayer.class, SpringBootLayer::markApplication);
    }

    @NotNull
    @Override
    protected Class<? extends DecalandPlugin> getPluginType() {
        return DecalandSpringBootApplicationJavaPlugin.class;
    }
}
