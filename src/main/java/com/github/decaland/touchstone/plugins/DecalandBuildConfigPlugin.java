package com.github.decaland.touchstone.plugins;

import com.github.decaland.touchstone.plugins.builds.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public abstract class DecalandBuildConfigPlugin extends DecalandBasePlugin {

    final static Collection<Class<? extends DecalandPlugin>> DECALAND_BUILD_CONFIG_PLUGIN_TYPES = List.of(
            DecalandLibraryJavaPlugin.class,
            DecalandLibraryJavaKotlinPlugin.class,
            DecalandSpringBootLibraryJavaPlugin.class,
            DecalandSpringBootLibraryJavaKotlinPlugin.class,
            DecalandSpringBootApplicationJavaPlugin.class,
            DecalandSpringBootApplicationJavaKotlinPlugin.class
    );

    @NotNull
    @Override
    public Collection<Class<? extends DecalandPlugin>> getIncompatiblePlugins() {
        return DECALAND_BUILD_CONFIG_PLUGIN_TYPES;
    }
}
