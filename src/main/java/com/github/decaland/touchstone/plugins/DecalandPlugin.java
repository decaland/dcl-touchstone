package com.github.decaland.touchstone.plugins;

import com.github.decaland.touchstone.loadout.Loadout;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.util.GradleVersion;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.decaland.touchstone.configs.BuildParametersManifest.MIN_VERSION_GRADLE;
import static java.util.stream.Collectors.joining;

/**
 * The root abstraction of a Decaland Touchstone plugin.
 */
public abstract class DecalandPlugin implements Plugin<Project> {

    private static final String MSG_UNSUPPORTED_GRADLE_VERSION
            = "Touchstone plugins require %s. Current version is %s";
    private static final String MSG_MISSING_REQUIRED_PLUGINS
            = "Cannot apply Touchstone plugin '%s': it requires all of these plugins to be applied first: %s";
    private static final String MSG_MISSING_ANY_REQUIRED_PLUGIN
            = "Cannot apply Touchstone plugin '%s': it requires any of these plugins to be applied first: %s";
    private static final String MSG_FOUND_INCOMPATIBLE_PLUGINS
            = "Cannot apply Touchstone plugin '%s': it conflicts with these plugins: %s";

    /**
     * Applies built-in validation logic, instantiates the plugin’s
     * {@link Loadout}, and configures it according to the implementor’s
     * instructions.
     *
     * @param project the consuming Gradle project
     */
    @Override
    public final void apply(@NotNull Project project) {
        validateGradleVersion();
        ensurePluginIsApplicable(project);
        Loadout pluginLoadout = new Loadout(project);
        configurePluginLoadout(pluginLoadout);
        pluginLoadout.putOn();
    }

    protected abstract void configurePluginLoadout(Loadout pluginLoadout);

    /**
     * Should return the implementor’s {@link Class} object, which is used in
     * calculating applicability of this Decaland Touchstone plugin by comparing
     * it to other applied Decaland Touchstone plugins.
     *
     * @return the Class object of the implementor
     */
    @NotNull
    protected abstract Class<? extends DecalandPlugin> getPluginType();

    /**
     * Calculates and returns the id of this Decaland Touchstone plugin, which
     * is derived from the implementor’s class name according to the convention
     * outlined in the method {@link #extractDecalandPluginId(Class)}.
     *
     * @return the id of this plugin
     */
    @NotNull
    public final String getPluginId() {
        return extractDecalandPluginId(getPluginType());
    }

    /**
     * Returns the list of Decaland Touchstone plugins that must also be applied
     * in order for this plugin to work properly.
     *
     * @return the {@link Collection} of required plugin class objects
     */
    @NotNull
    protected Collection<Class<? extends DecalandPlugin>> getRequiredPlugins() {
        return List.of();
    }

    /**
     * Returns the list of Decaland Touchstone plugins, from which any one (or
     * more) must also be applied in order for this plugin to work properly.
     *
     * @return the {@link Collection} of plugin class objects, of which any
     * one (or more) is required
     */
    @NotNull
    protected Collection<Class<? extends DecalandPlugin>> getAnyRequiredPlugins() {
        return List.of();
    }

    /**
     * Returns the list of Decaland Touchstone plugins, of which not one may be
     * applied together with this plugin.
     *
     * @return the {@link Collection} of incompatible plugin class objects
     */
    @NotNull
    protected Collection<Class<? extends DecalandPlugin>> getIncompatiblePlugins() {
        return List.of();
    }

    /**
     * Ensures that this plugin is being applied with a version of Gradle that
     * is supported.
     */
    private void validateGradleVersion() {
        GradleVersion currentVersion = GradleVersion.current();
        GradleVersion minVersion = GradleVersion.version(MIN_VERSION_GRADLE);
        if (currentVersion.compareTo(minVersion) < 0) {
            throw new GradleException(String.format(MSG_UNSUPPORTED_GRADLE_VERSION, minVersion, currentVersion));
        }
    }

    /**
     * Analyzes Gradle plugins that are currently applied to the project and
     * ensures that the current Decaland Touchstone plugin is applicable
     * according to the declared dependency and compatibility rules. If not,
     * throws a {@link GradleException}.
     *
     * Because this plugin may only see the Gradle plugins applied before
     * itself, all dependency and compatibility rules between Decaland
     * Touchstone plugins must be declared symmetrically.
     *
     * @param project the consuming Gradle project
     */
    private void ensurePluginIsApplicable(@NotNull Project project) {
        PluginContainer pluginContainer = project.getPlugins();

        Collection<Class<? extends DecalandPlugin>> requiredPlugins = getRequiredPlugins();
        if (!requiredPlugins.stream().allMatch(pluginContainer::hasPlugin)) {
            handlePluginConflict(requiredPlugins, MSG_MISSING_REQUIRED_PLUGINS);
        }

        Collection<Class<? extends DecalandPlugin>> anyRequiredPlugins = getAnyRequiredPlugins();
        if (!anyRequiredPlugins.isEmpty() && anyRequiredPlugins.stream().noneMatch(pluginContainer::hasPlugin)) {
            handlePluginConflict(anyRequiredPlugins, MSG_MISSING_ANY_REQUIRED_PLUGIN);
        }

        Collection<Class<? extends DecalandPlugin>> incompatiblePlugins = getIncompatiblePlugins()
                .stream()
                .filter(pluginContainer::hasPlugin)
                .collect(Collectors.toUnmodifiableList());
        if (!incompatiblePlugins.isEmpty()) {
            handlePluginConflict(incompatiblePlugins, MSG_FOUND_INCOMPATIBLE_PLUGINS);
        }
    }

    /**
     * Encapsulates the common logic of throwing a Gradle exception.
     *
     * @param conflictingPlugins a {@link Collection} of plugin objects that
     *                           cause a conflict
     * @param errorMessage a {@link String#format(String, Object...)} template
     *                     for the error message; must follow a hard-coded
     *                     pattern
     */
    private void handlePluginConflict(Collection<Class<? extends DecalandPlugin>> conflictingPlugins, String errorMessage) {
        throw new GradleException(String.format(
                errorMessage,
                getPluginId(),
                composeListOfClasses(conflictingPlugins)
        ));
    }

    @NotNull
    private String composeListOfClasses(Collection<Class<? extends DecalandPlugin>> pluginClasses) {
        return pluginClasses.stream()
                .map(this::extractDecalandPluginId)
                .collect(joining("', '", "'", "'"));
    }

    /**
     * Defines logic that converts the simple name of this plugin’s class object
     * to its id: CamelCase is converted to kebab-case, the '-plugin' suffix is
     * removed, and some words are shortened. The plugin must follow this
     * convention in order for potential error messages to be human-readable.
     *
     * @param clazz the class object of a Decaland Touchstone plugin
     * @return the plugin id
     */
    @NotNull
    private String extractDecalandPluginId(Class<? extends DecalandPlugin> clazz) {
        return clazz.getSimpleName()
                .replaceAll("([a-z])([A-Z])", "$1-$2")
                .toLowerCase()
                .replace("decaland", "dcl")
                .replace("spring-boot", "boot")
                .replace("library", "lib")
                .replace("application", "app")
                .replace("-plugin", "");
    }
}
