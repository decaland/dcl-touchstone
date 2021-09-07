package com.github.decaland.touchstone.loadout.layers.releasing.services.devisers;

import com.github.decaland.touchstone.loadout.layers.releasing.models.IllegalVersionException;
import com.github.decaland.touchstone.loadout.layers.releasing.models.Version;
import com.github.decaland.touchstone.loadout.layers.releasing.models.VersionTransition;
import com.github.decaland.touchstone.utils.gradle.GradleProperties;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class VersionDeviser {

    public static final String PROPERTY_KEY_VERSION_STRATEGY = "versionStrategy";
    public static final String PROPERTY_KEY_RELEASE_VERSION = "releaseVersion";

    private final Project project;
    private GradleProperties gradleProperties;

    private Version releaseVersion;
    private VersionTransition.Strategy versionStrategy;

    private static final Map<Project, VersionDeviser> managedInstances = new HashMap<>();

    @Contract(pure = true)
    private VersionDeviser(@NotNull Project project) {
        this.project = project;
    }

    synchronized public static @NotNull VersionDeviser forProject(@NotNull Project project) {
        return managedInstances.computeIfAbsent(project, VersionDeviser::new);
    }

    public @NotNull Version deviseReleaseVersion() {
        if (releaseVersion == null) {
            releaseVersion = doDeviseReleaseVersion();
        }
        return releaseVersion;
    }

    public @NotNull VersionTransition.Strategy deviseVersionStrategy() {
        if (versionStrategy == null) {
            versionStrategy = doDeviseVersionStrategy();
        }
        return versionStrategy;
    }

    @Contract(" -> new")
    private @NotNull Version doDeviseReleaseVersion() {
        String releaseVersionString = getGradleProperties().require(PROPERTY_KEY_RELEASE_VERSION);
        try {
            return new Version(releaseVersionString);
        } catch (IllegalVersionException exception) {
            throw new GradleException(String.format(
                    "Received invalid release version '%s'", releaseVersionString
            ));
        }
    }

    private @NotNull VersionTransition.Strategy doDeviseVersionStrategy() {
        String versionStrategyString = getGradleProperties().requireOrDefaultsValidated(
                        PROPERTY_KEY_VERSION_STRATEGY,
                        VersionTransition.Strategy::exists,
                        VersionTransition.Strategy.getDefaultPropertyValue()
                );
        return VersionTransition.Strategy.of(versionStrategyString);
    }

    private @NotNull GradleProperties getGradleProperties() {
        if (gradleProperties == null) {
            gradleProperties = GradleProperties.forProject(project);
        }
        return gradleProperties;
    }
}
