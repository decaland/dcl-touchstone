package com.github.decaland.touchstone.plugins;

import com.github.decaland.touchstone.plugins.builds.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * The base abstraction of a Decaland Touchstone plugin that configures some
 * aspects of the build process for the consuming project.
 */
public abstract class DecalandBuildConfigPlugin extends DecalandPlugin {

    /**
     * Catalogues all plugin types that extend this base abstraction.
     */
    final static Collection<Class<? extends DecalandPlugin>> DECALAND_BUILD_CONFIG_PLUGIN_TYPES = List.of(
            DecalandLibraryJavaPlugin.class,
            DecalandLibraryJavaKotlinPlugin.class,
            DecalandSpringBootLibraryJavaPlugin.class,
            DecalandSpringBootLibraryJavaKotlinPlugin.class,
            DecalandSpringBootApplicationJavaPlugin.class,
            DecalandSpringBootApplicationJavaKotlinPlugin.class
    );

    /**
     * No more than one Decaland Touchstone plugin of this type should be
     * applied at a time.
     *
     * @return the {@link Collection} of incompatible plugin class objects
     */
    @NotNull
    @Override
    public Collection<Class<? extends DecalandPlugin>> getIncompatiblePlugins() {
        return DECALAND_BUILD_CONFIG_PLUGIN_TYPES;
    }
}
