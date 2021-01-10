package com.github.decaland.touchstone.plugins.builds;

import com.github.decaland.touchstone.plugins.DecalandPluginTest;
import org.jetbrains.annotations.NotNull;

public class DecalandSpringBootLibraryJavaKotlinPluginTest extends DecalandPluginTest<DecalandSpringBootLibraryJavaKotlinPlugin> {

    @NotNull
    @Override
    public Class<DecalandSpringBootLibraryJavaKotlinPlugin> supplyPluginClass() {
        return DecalandSpringBootLibraryJavaKotlinPlugin.class;
    }

    @NotNull
    @Override
    public DecalandSpringBootLibraryJavaKotlinPlugin supplyDefaultPluginInstance() {
        return new DecalandSpringBootLibraryJavaKotlinPlugin();
    }
}
