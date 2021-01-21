package com.github.decaland.touchstone.plugins;

import com.github.decaland.touchstone.plugins.dependencies.DecalandEssentialDependenciesPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

import static com.github.decaland.touchstone.plugins.DecalandBuildConfigPlugin.DECALAND_BUILD_CONFIG_PLUGIN_TYPES;

/**
 * The base abstraction of a Decaland Touchstone plugin that configures a set of
 * dependencies for the consuming project.
 */
public abstract class DecalandDependenciesPlugin extends DecalandBasePlugin {

    /**
     * Catalogues all plugin types that extend this base abstraction.
     */
    final static Collection<Class<? extends DecalandPlugin>> DECALAND_DEPENDENCIES_PLUGIN_TYPES = List.of(
            DecalandEssentialDependenciesPlugin.class
    );

    /**
     * A build config Decaland Touchstone plugin must be applied before
     * dependencies are added.
     *
     * @return the {@link Collection} of plugin class objects, of which any
     * one (or more) is required
     */
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
