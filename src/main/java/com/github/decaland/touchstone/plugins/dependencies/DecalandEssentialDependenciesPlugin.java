package com.github.decaland.touchstone.plugins.dependencies;

import com.github.decaland.touchstone.loadout.Loadout;
import com.github.decaland.touchstone.loadout.layers.dependencies.EssentialDependenciesLayer;
import com.github.decaland.touchstone.plugins.DecalandDependenciesPlugin;
import com.github.decaland.touchstone.plugins.DecalandPlugin;
import org.jetbrains.annotations.NotNull;

public class DecalandEssentialDependenciesPlugin extends DecalandDependenciesPlugin {

    @Override
    protected void configurePluginLoadout(Loadout pluginLoadout) {
        pluginLoadout.addLayer(EssentialDependenciesLayer.class);
    }

    @NotNull
    @Override
    protected Class<? extends DecalandPlugin> getPluginType() {
        return DecalandEssentialDependenciesPlugin.class;
    }
}
