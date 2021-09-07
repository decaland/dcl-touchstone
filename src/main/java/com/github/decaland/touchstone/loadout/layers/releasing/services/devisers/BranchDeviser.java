package com.github.decaland.touchstone.loadout.layers.releasing.services.devisers;

import com.github.decaland.touchstone.utils.gradle.GradleProperties;
import com.github.decaland.touchstone.utils.lazy.Lazy;
import com.github.decaland.touchstone.utils.scm.git.GitAdapter;
import com.github.decaland.touchstone.utils.scm.git.GitBranch;
import org.gradle.api.Project;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BranchDeviser {

    public static final String PROPERTY_KEY_BRANCH_MAIN = "mainBranch";
    public static final String PROPERTY_KEY_BRANCH_DEV = "devBranch";

    private final Lazy<GitAdapter> git;
    private final Lazy<GradleProperties> gradleProperties;

    private final Lazy<GitBranch> mainBranch;
    private final Lazy<GitBranch> devBranch;

    private static final Map<Project, BranchDeviser> managedInstances = new HashMap<>();

    @Contract(pure = true)
    private BranchDeviser(@NotNull Project project) {
        this.git = GitAdapter.lazyFor(project);
        this.gradleProperties = GradleProperties.lazyFor(project);
        this.mainBranch = Lazy.using(() -> deviseBranch(PROPERTY_KEY_BRANCH_MAIN, "main", "master"));
        this.devBranch = Lazy.using(() -> deviseBranch(PROPERTY_KEY_BRANCH_DEV, "dev"));
    }

    synchronized private static @NotNull BranchDeviser forProject(@NotNull Project project) {
        return managedInstances.computeIfAbsent(project, BranchDeviser::new);
    }

    @Contract("_ -> new")
    public static @NotNull Lazy<BranchDeviser> lazyFor(@NotNull Project project) {
        return Lazy.using(() -> BranchDeviser.forProject(project));
    }

    public @NotNull GitBranch deviseMainBranch() {
        return mainBranch.get();
    }

    public @NotNull GitBranch deviseDevBranch() {
        return devBranch.get();
    }

    private @NotNull GitBranch deviseBranch(
            @NotNull String propertyKey,
            @NotNull String... defaultValues
    ) {
        return GitBranch.named(
                gradleProperties.get().requireOrDefaultsValidated(
                        propertyKey, this::branchExists, defaultValues
                )
        );
    }

    private boolean branchExists(@NotNull String branchName) {
        return git.get().exists(GitBranch.named(branchName));
    }
}
