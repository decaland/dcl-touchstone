package com.github.decaland.touchstone.plugins;

import com.github.decaland.touchstone.loadout.Loadout;
import com.github.decaland.touchstone.loadout.layers.Layer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.util.GradleVersion;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Extended Gradle {@link Plugin}, immutable and stateless, that employs the
 * {@link Loadout} construct to aggregate and systematically apply the logic in
 * {@link Layer}s, while also being aware of other {@link DecalandPlugin}s that
 * were applied to the Gradle {@link Project} before the current one.
 */
public interface DecalandPlugin extends Plugin<Project> {

    /**
     * Message to be printed to Gradle lifecycle log whenever a
     * {@link DecalandPlugin} is applied; should contain a single
     * <code>%s</code> placeholder that is to be substituted for the plugin ID,
     * as returned by the {@link DecalandPlugin#getPluginId()} method.
     */
    String LIFECYCLE_LOG_APPLY_PLUGIN = "  Apply Touchstone plugin '%s'";

    /**
     * Ensures that the user’s Gradle is of compatible version and that the
     * current plugin is compatible with other {@link DecalandPlugin}s applied
     * before, then constructs the {@link Loadout} and applies it to the given
     * {@link Project}.
     *
     * @param target the Gradle {@link Project} to apply the plugin to
     */
    void apply(@NotNull Project target);

    /**
     * Returns the {@link Loadout} instance that is associated with this plugin
     * instance.
     *
     * @return the {@link Loadout} that is used by this plugin
     */
    @NotNull
    Loadout getLoadout();

    /**
     * Returns the textual plugin ID, the one that is used by the end user when
     * applying this {@link DecalandPlugin} in their <code>build.gradle</code>
     * file.
     *
     * @return the textual plugin ID
     */
    @NotNull
    String getPluginId();

    /**
     * Returns the minimum {@link GradleVersion} compatible with this plugin.
     *
     * @return the minimum {@link GradleVersion} compatible with this plugin
     */
    @NotNull
    GradleVersion getMinimumGradleVersion();

    /**
     * Returns a collection of {@link DecalandPlugin} class objects,
     * representing the set plugins, at least one of which must be applied
     * before the current one.
     *
     * @return the list of plugins, at least one of which is required
     */
    @NotNull
    Collection<Class<? extends DecalandPlugin>> getAnyRequiredPlugins();

    /**
     * Returns a collection of {@link DecalandPlugin} class objects,
     * representing the set plugins, all of which must be applied before the
     * current one.
     *
     * @return the list of plugins, all of which is required
     */
    @NotNull
    Collection<Class<? extends DecalandPlugin>> getAllRequiredPlugins();

    /**
     * Returns a collection of {@link DecalandPlugin} class objects,
     * representing the set plugins, none of which must be applied before the
     * current one.
     *
     * @return the list of plugins that are incompatible with the current one
     */
    @NotNull
    Collection<Class<? extends DecalandPlugin>> getIncompatiblePlugins();
}
