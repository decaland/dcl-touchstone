package com.github.decaland.touchstone.plugins.dependencies;

import com.github.decaland.touchstone.plugins.DecalandPluginTest;
import org.jetbrains.annotations.NotNull;

public class DecalandEssentialDependenciesPluginTest extends DecalandPluginTest<DecalandEssentialDependenciesPlugin> {

    @NotNull
    @Override
    public Class<DecalandEssentialDependenciesPlugin> supplyPluginClass() {
        return DecalandEssentialDependenciesPlugin.class;
    }

    @NotNull
    @Override
    public DecalandEssentialDependenciesPlugin supplyDefaultPluginInstance() {
        return new DecalandEssentialDependenciesPlugin();
    }
}
