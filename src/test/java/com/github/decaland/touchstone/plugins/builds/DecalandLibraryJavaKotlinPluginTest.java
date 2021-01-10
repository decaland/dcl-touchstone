package com.github.decaland.touchstone.plugins.builds;

import com.github.decaland.touchstone.plugins.DecalandPluginTest;
import org.jetbrains.annotations.NotNull;

public class DecalandLibraryJavaKotlinPluginTest extends DecalandPluginTest<DecalandLibraryJavaKotlinPlugin> {

    @NotNull
    @Override
    public Class<DecalandLibraryJavaKotlinPlugin> supplyPluginClass() {
        return DecalandLibraryJavaKotlinPlugin.class;
    }

    @NotNull
    @Override
    public DecalandLibraryJavaKotlinPlugin supplyDefaultPluginInstance() {
        return new DecalandLibraryJavaKotlinPlugin();
    }
}
