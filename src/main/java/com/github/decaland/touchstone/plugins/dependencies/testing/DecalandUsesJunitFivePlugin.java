package com.github.decaland.touchstone.plugins.dependencies.testing;

import com.github.decaland.touchstone.loadout.Loadout;
import com.github.decaland.touchstone.loadout.layers.dependencies.testing.UsesJunitFiveLayer;
import com.github.decaland.touchstone.plugins.DecalandDependenciesPlugin;
import com.github.decaland.touchstone.plugins.DecalandPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Introduces dependencies for testing with jUnit5.
 */
public class DecalandUsesJunitFivePlugin extends DecalandDependenciesPlugin {

    @NotNull
    @Override
    public Loadout supplyLoadout() {
        return Loadout.builder()
                .add(new UsesJunitFiveLayer())
                .build();
    }

    @NotNull
    @Override
    protected Class<? extends DecalandPlugin> getPluginType() {
        return DecalandUsesJunitFivePlugin.class;
    }
}
