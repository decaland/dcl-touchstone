package com.github.decaland.touchstone.loadout.layers.releasing.services.planner;

import com.github.decaland.touchstone.loadout.layers.releasing.models.ReleasePlan;
import com.github.decaland.touchstone.loadout.layers.releasing.models.VersionTransition;
import com.github.decaland.touchstone.loadout.layers.releasing.services.devisers.BranchDeviser;
import com.github.decaland.touchstone.loadout.layers.releasing.services.devisers.VersionDeviser;
import com.github.decaland.touchstone.loadout.layers.releasing.services.extractor.VersionExtractor;
import com.github.decaland.touchstone.utils.gradle.GradleProperties;
import com.github.decaland.touchstone.utils.lazy.Lazy;
import com.github.decaland.touchstone.utils.scm.git.GitAdapter;
import com.github.decaland.touchstone.utils.scm.git.GitBranchSnapshot;
import com.github.decaland.touchstone.utils.scm.git.GitRemote;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.decaland.touchstone.loadout.layers.releasing.services.devisers.VersionDeviser.PROPERTY_KEY_RELEASE_VERSION;
import static com.github.decaland.touchstone.loadout.layers.releasing.services.devisers.VersionDeviser.PROPERTY_KEY_VERSION_STRATEGY;

public class ReleasePlanner {

    private final Lazy<GitAdapter> git;
    private final Lazy<GradleProperties> gradleProperties;
    private final Lazy<BranchDeviser> branchDeviser;
    private final Lazy<VersionDeviser> versionDeviser;
    private final Lazy<VersionExtractor> versionExtractor;

    private final Lazy<ReleasePlan> releasePlan;
    private final Lazy<VersionTransition> versionTransition;

    private static final Map<Project, ReleasePlanner> managedInstances = new HashMap<>();

    @Contract(pure = true)
    private ReleasePlanner(@NotNull Project project) {
        this.git = GitAdapter.lazyFor(project);
        this.gradleProperties = GradleProperties.lazyFor(project);
        this.branchDeviser = BranchDeviser.lazyFor(project);
        this.versionDeviser = VersionDeviser.lazyFor(project);
        this.versionExtractor = VersionExtractor.lazyFor(project);
        this.releasePlan = Lazy.using(this::planRelease);
        this.versionTransition = Lazy.using(this::planVersionTransition);
    }

    synchronized private static @NotNull ReleasePlanner forProject(@NotNull Project project) {
        return managedInstances.computeIfAbsent(project, ReleasePlanner::new);
    }

    @Contract("_ -> new")
    public static @NotNull Lazy<ReleasePlanner> lazyFor(@NotNull Project project) {
        return Lazy.using(() -> ReleasePlanner.forProject(project));
    }

    public @NotNull ReleasePlan getReleasePlan() {
        return releasePlan.get();
    }

    public @NotNull VersionTransition getVersionTransition() {
        return versionTransition.get();
    }

    @Contract(" -> new")
    private @NotNull ReleasePlan planRelease() {
        BranchDeviser branchDeviser = this.branchDeviser.get();
        GitAdapter git = this.git.get();
        GitBranchSnapshot mainBranchSnapshot = branchDeviser.deviseMainBranch().asSnapshot(git);
        GitBranchSnapshot devBranchSnapshot = branchDeviser.deviseDevBranch().asSnapshot(git);
        return new ReleasePlan(
                getVersionTransition(),
                mainBranchSnapshot,
                devBranchSnapshot,
                getRemotes()
        );
    }

    private @NotNull VersionTransition planVersionTransition() {
        GradleProperties gradleProperties = this.gradleProperties.get();
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
                    versionExtractor.get().extractVersion(),
                    versionDeviser.get().deviseReleaseVersion()
            );
        }
        return VersionTransition.from(
                versionExtractor.get().extractVersion(),
                versionDeviser.get().deviseVersionStrategy()
        );
    }

    private List<GitRemote> getRemotes() {
        GradleProperties gradleProperties = this.gradleProperties.get();
        // TODO: Go on
        return Collections.emptyList();
    }
}
