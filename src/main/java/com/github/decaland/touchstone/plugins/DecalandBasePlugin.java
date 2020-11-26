package com.github.decaland.touchstone.plugins;

import com.github.decaland.touchstone.loadout.BaseLoadout;
import com.github.decaland.touchstone.loadout.Loadout;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.util.GradleVersion;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.decaland.touchstone.configs.BuildParametersManifest.MIN_VERSION_GRADLE;
import static java.util.stream.Collectors.joining;

public abstract class DecalandBasePlugin implements DecalandPlugin {

    private static final String MSG_UNSUPPORTED_GRADLE_VERSION
            = "Touchstone plugins require %s. Current version is %s";
    private static final String MSG_MISSING_REQUIRED_PLUGINS
            = "Cannot apply Touchstone plugin '%s': it requires all of these plugins to be applied first: %s";
    private static final String MSG_MISSING_ANY_REQUIRED_PLUGIN
            = "Cannot apply Touchstone plugin '%s': it requires any of these plugins to be applied first: %s";
    private static final String MSG_FOUND_INCOMPATIBLE_PLUGINS
            = "Cannot apply Touchstone plugin '%s': it conflicts with these plugins: %s";

    @Override
    public final void apply(@NotNull Project project) {
        project.getLogger().lifecycle(String.format("  Apply Touchstone plugin '%s'", getPluginId()));
        validateGradleVersion();
        ensurePluginIsApplicable(project);
        Loadout.Builder loadoutBuilder = BaseLoadout.builder(project, getPluginId());
        configurePluginLoadout(loadoutBuilder);
        loadoutBuilder.build().putOn();
    }

    protected abstract Loadout.Builder configurePluginLoadout(Loadout.Builder loadoutBuilder);

    @NotNull
    protected abstract Class<? extends DecalandPlugin> getPluginType();

    @Override
    @NotNull
    public final String getPluginId() {
        return extractDecalandPluginName(getPluginType());
    }

    @NotNull
    protected Collection<Class<? extends DecalandPlugin>> getRequiredPlugins() {
        return List.of();
    }

    @NotNull
    protected Collection<Class<? extends DecalandPlugin>> getAnyRequiredPlugins() {
        return List.of();
    }

    @NotNull
    protected Collection<Class<? extends DecalandPlugin>> getIncompatiblePlugins() {
        return List.of();
    }

    private void validateGradleVersion() {
        GradleVersion currentVersion = GradleVersion.current();
        GradleVersion minVersion = GradleVersion.version(MIN_VERSION_GRADLE);
        if (currentVersion.compareTo(minVersion) < 0) {
            throw new GradleException(String.format(MSG_UNSUPPORTED_GRADLE_VERSION, minVersion, currentVersion));
        }
    }

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
                .map(this::extractDecalandPluginName)
                .collect(joining("', '", "'", "'"));
    }

    @NotNull
    private String extractDecalandPluginName(Class<? extends DecalandPlugin> clazz) {
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
