package com.github.decaland.touchstone.loadout.layers.releasing.services.devisers;

import com.github.decaland.touchstone.loadout.layers.releasing.models.IllegalVersionException;
import com.github.decaland.touchstone.loadout.layers.releasing.models.Version;
import com.github.decaland.touchstone.loadout.layers.releasing.models.VersionTransition;
import com.github.decaland.touchstone.utils.gradle.GradleProperties;
import com.github.decaland.touchstone.utils.lazy.Lazy;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class VersionDeviser {

    public static final String PROPERTY_KEY_VERSION_STRATEGY = "versionStrategy";
    public static final String PROPERTY_KEY_RELEASE_VERSION = "releaseVersion";

    private final Lazy<GradleProperties> gradleProperties;

    private final Lazy<Version> releaseVersion;
    private final Lazy<VersionTransition.Strategy> versionStrategy;

    private static final Map<Project, VersionDeviser> managedInstances = new HashMap<>();

    @Contract(pure = true)
    private VersionDeviser(@NotNull Project project) {
        this.gradleProperties = GradleProperties.lazyFor(project);
        this.releaseVersion = Lazy.using(this::doDeviseReleaseVersion);
        this.versionStrategy = Lazy.using(this::doDeviseVersionStrategy);
    }

    synchronized private static @NotNull VersionDeviser forProject(@NotNull Project project) {
        return managedInstances.computeIfAbsent(project, VersionDeviser::new);
    }

    @Contract("_ -> new")
    public static @NotNull Lazy<VersionDeviser> lazyFor(@NotNull Project project) {
        return Lazy.using(() -> VersionDeviser.forProject(project));
    }

    public @NotNull Version deviseReleaseVersion() {
        return releaseVersion.get();
    }

    public @NotNull VersionTransition.Strategy deviseVersionStrategy() {
        return versionStrategy.get();
    }

    @Contract(" -> new")
    private @NotNull Version doDeviseReleaseVersion() {
        String releaseVersionString = gradleProperties.get().require(PROPERTY_KEY_RELEASE_VERSION);
        try {
            return new Version(releaseVersionString);
        } catch (IllegalVersionException exception) {
            throw new GradleException(String.format(
                    "Received invalid release version '%s'", releaseVersionString
            ));
        }
    }

    private @NotNull VersionTransition.Strategy doDeviseVersionStrategy() {
        String versionStrategyString = gradleProperties.get().requireOrDefaultsValidated(
                        PROPERTY_KEY_VERSION_STRATEGY,
                        VersionTransition.Strategy::exists,
                        VersionTransition.Strategy.getDefaultPropertyValue()
                );
        return VersionTransition.Strategy.of(versionStrategyString);
    }
}
