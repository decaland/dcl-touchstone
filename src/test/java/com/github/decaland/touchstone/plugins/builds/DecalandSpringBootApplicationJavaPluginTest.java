package com.github.decaland.touchstone.plugins.builds;

import com.github.decaland.touchstone.plugins.DecalandPluginTest;
import org.jetbrains.annotations.NotNull;

public class DecalandSpringBootApplicationJavaPluginTest extends DecalandPluginTest<DecalandSpringBootApplicationJavaPlugin> {

    @NotNull
    @Override
    public Class<DecalandSpringBootApplicationJavaPlugin> supplyPluginClass() {
        return DecalandSpringBootApplicationJavaPlugin.class;
    }

    @NotNull
    @Override
    public DecalandSpringBootApplicationJavaPlugin supplyDefaultPluginInstance() {
        return new DecalandSpringBootApplicationJavaPlugin();
    }
}
