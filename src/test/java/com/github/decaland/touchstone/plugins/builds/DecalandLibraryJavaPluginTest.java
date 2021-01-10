package com.github.decaland.touchstone.plugins.builds;

import com.github.decaland.touchstone.plugins.DecalandPluginTest;
import org.jetbrains.annotations.NotNull;

public class DecalandLibraryJavaPluginTest extends DecalandPluginTest<DecalandLibraryJavaPlugin> {

    @NotNull
    @Override
    public Class<DecalandLibraryJavaPlugin> supplyPluginClass() {
        return DecalandLibraryJavaPlugin.class;
    }

    @NotNull
    @Override
    public DecalandLibraryJavaPlugin supplyDefaultPluginInstance() {
        return new DecalandLibraryJavaPlugin();
    }
}
