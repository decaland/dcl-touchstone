package com.github.decaland.touchstone.plugins.dependencies;

import com.github.decaland.touchstone.loadout.Loadout;
import com.github.decaland.touchstone.loadout.layers.dependencies.EssentialDependenciesLayer;
import com.github.decaland.touchstone.plugins.DecalandBuildConfigPlugin;
import com.github.decaland.touchstone.plugins.DecalandDependenciesPlugin;
import com.github.decaland.touchstone.plugins.DecalandPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Introduces a modest set of ‘essential’ JVM dependencies to the consuming
 * project that is already configured with one of the
 * {@link DecalandBuildConfigPlugin} type plugins.
 */
public class DecalandEssentialDependenciesPlugin extends DecalandDependenciesPlugin {

    @NotNull
    @Override
    public Loadout supplyLoadout() {
        return Loadout.builder()
                .add(new EssentialDependenciesLayer())
                .build();
    }

    @NotNull
    @Override
    protected Class<? extends DecalandPlugin> getPluginType() {
        return DecalandEssentialDependenciesPlugin.class;
    }
}
