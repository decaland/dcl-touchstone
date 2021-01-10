package com.github.decaland.touchstone.plugins.builds;

import com.github.decaland.touchstone.plugins.DecalandPluginTest;
import org.jetbrains.annotations.NotNull;

public class DecalandSpringBootApplicationJavaKotlinPluginTest extends DecalandPluginTest<DecalandSpringBootApplicationJavaKotlinPlugin> {

    @NotNull
    @Override
    public Class<DecalandSpringBootApplicationJavaKotlinPlugin> supplyPluginClass() {
        return DecalandSpringBootApplicationJavaKotlinPlugin.class;
    }

    @NotNull
    @Override
    public DecalandSpringBootApplicationJavaKotlinPlugin supplyDefaultPluginInstance() {
        return new DecalandSpringBootApplicationJavaKotlinPlugin();
    }
}
