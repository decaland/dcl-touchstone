package com.github.decaland.touchstone.plugins.builds;

import com.github.decaland.touchstone.plugins.DecalandPluginTest;
import org.jetbrains.annotations.NotNull;

public class DecalandSpringBootLibraryJavaPluginTest extends DecalandPluginTest<DecalandSpringBootLibraryJavaPlugin> {

    @NotNull
    @Override
    public Class<DecalandSpringBootLibraryJavaPlugin> supplyPluginClass() {
        return DecalandSpringBootLibraryJavaPlugin.class;
    }

    @NotNull
    @Override
    public DecalandSpringBootLibraryJavaPlugin supplyDefaultPluginInstance() {
        return new DecalandSpringBootLibraryJavaPlugin();
    }
}
