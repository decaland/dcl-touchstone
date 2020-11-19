package com.github.decaland.touchstone.plugins.builds;

import com.github.decaland.touchstone.loadout.Loadout;
import com.github.decaland.touchstone.loadout.layers.flavors.SpringBootLayer;
import com.github.decaland.touchstone.plugins.DecalandPlugin;
import org.jetbrains.annotations.NotNull;

public class DecalandSpringBootLibraryJavaPlugin extends DecalandLibraryJavaPlugin {

    @Override
    protected void configurePluginLoadout(Loadout pluginLoadout) {
        super.configurePluginLoadout(pluginLoadout);
        pluginLoadout.addLayer(SpringBootLayer.class);
    }

    @NotNull
    @Override
    protected Class<? extends DecalandPlugin> getPluginType() {
        return DecalandSpringBootLibraryJavaPlugin.class;
    }
}
