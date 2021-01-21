package com.github.decaland.touchstone.plugins;

import com.github.decaland.touchstone.plugins.dependencies.DecalandEssentialDependenciesPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

import static com.github.decaland.touchstone.plugins.DecalandBuildConfigPlugin.DECALAND_BUILD_CONFIG_PLUGIN_TYPES;

public abstract class DecalandDependenciesPlugin extends DecalandBasePlugin {

    final static Collection<Class<? extends DecalandPlugin>> DECALAND_DEPENDENCIES_PLUGIN_TYPES = List.of(
            DecalandEssentialDependenciesPlugin.class
    );

    @NotNull
    @Override
    public Collection<Class<? extends DecalandPlugin>> getAnyRequiredPlugins() {
        return DECALAND_BUILD_CONFIG_PLUGIN_TYPES;
    }

    @NotNull
    @Override
    public Collection<Class<? extends DecalandPlugin>> getIncompatiblePlugins() {
        return DECALAND_DEPENDENCIES_PLUGIN_TYPES;
    }
}
