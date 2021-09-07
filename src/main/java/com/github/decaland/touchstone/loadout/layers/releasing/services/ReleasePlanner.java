package com.github.decaland.touchstone.loadout.layers.releasing.services;

import com.github.decaland.touchstone.loadout.layers.releasing.models.ReleasePlan;
import com.github.decaland.touchstone.loadout.layers.releasing.models.VersionTransition;
import com.github.decaland.touchstone.loadout.layers.releasing.services.devisers.BranchDeviser;
import com.github.decaland.touchstone.loadout.layers.releasing.services.devisers.VersionDeviser;
import com.github.decaland.touchstone.loadout.layers.releasing.services.extractors.VersionExtractor;
import com.github.decaland.touchstone.utils.gradle.GradleProperties;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.github.decaland.touchstone.loadout.layers.releasing.services.devisers.VersionDeviser.PROPERTY_KEY_RELEASE_VERSION;
import static com.github.decaland.touchstone.loadout.layers.releasing.services.devisers.VersionDeviser.PROPERTY_KEY_VERSION_STRATEGY;

public final class ReleasePlanner {

    private final Project project;
    private GradleProperties gradleProperties;
    private BranchDeviser branchDeviser;
    private VersionDeviser versionDeviser;
    private VersionExtractor versionExtractor;

    private ReleasePlan releasePlan;
    private VersionTransition versionTransition;

    private static final Map<Project, ReleasePlanner> managedInstances = new HashMap<>();

    @Contract(pure = true)
    private ReleasePlanner(@NotNull Project project) {
        this.project = project;
    }

    synchronized public static @NotNull ReleasePlanner forProject(@NotNull Project project) {
        return managedInstances.computeIfAbsent(project, ReleasePlanner::new);
    }

    public @NotNull ReleasePlan getReleasePlan() {
        if (releasePlan == null) {
            releasePlan = planRelease();
        }
        return releasePlan;
    }

    public @NotNull VersionTransition getVersionTransition() {
        if (versionTransition == null) {
            versionTransition = planVersionTransition();
        }
        return versionTransition;
    }

    @Contract(" -> new")
    private @NotNull ReleasePlan planRelease() {
        BranchDeviser branchDeviser = getBranchDeviser();
        return new ReleasePlan(
                getVersionTransition(),
                branchDeviser.deviseMainBranch(),
                branchDeviser.deviseDevBranch()
        );
    }

    private @NotNull VersionTransition planVersionTransition() {
        GradleProperties gradleProperties = getGradleProperties();
        boolean versionGivenExplicitly = gradleProperties.has(PROPERTY_KEY_RELEASE_VERSION);
        boolean strategyGivenExplicitly = gradleProperties.has(PROPERTY_KEY_VERSION_STRATEGY);
        if (versionGivenExplicitly && strategyGivenExplicitly) {
            throw new GradleException(String.format(
                    "Gradle project properties '%s' and '%s' cannot be used together",
                    PROPERTY_KEY_RELEASE_VERSION, PROPERTY_KEY_VERSION_STRATEGY
            ));
        }
        if (versionGivenExplicitly) {
            return VersionTransition.between(
                    getVersionExtractor().extractVersion(),
                    getVersionDeviser().deviseReleaseVersion()
            );
        }
        return VersionTransition.from(
                getVersionExtractor().extractVersion(),
                getVersionDeviser().deviseVersionStrategy()
        );
    }

    private @NotNull GradleProperties getGradleProperties() {
        if (gradleProperties == null) {
            gradleProperties = GradleProperties.forProject(project);
        }
        return gradleProperties;
    }

    private @NotNull BranchDeviser getBranchDeviser() {
        if (branchDeviser == null) {
            branchDeviser = BranchDeviser.forProject(project);
        }
        return branchDeviser;
    }

    private @NotNull VersionDeviser getVersionDeviser() {
        if (versionDeviser == null) {
            versionDeviser = VersionDeviser.forProject(project);
        }
        return versionDeviser;
    }

    private @NotNull VersionExtractor getVersionExtractor() {
        if (versionExtractor == null) {
            versionExtractor = VersionExtractor.forProject(project);
        }
        return versionExtractor;
    }
}
