package com.github.decaland.touchstone.loadout.layers.releasing.services.devisers;

import com.github.decaland.touchstone.loadout.layers.releasing.services.gradle.GradleProperties;
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

    private final Project project;
    private GitAdapter git;
    private GradleProperties gradleProperties;

    private GitBranch mainBranch;
    private GitBranch devBranch;

    private static final Map<Project, BranchDeviser> managedInstances = new HashMap<>();

    @Contract(pure = true)
    private BranchDeviser(@NotNull Project project) {
        this.project = project;
    }

    synchronized public static @NotNull BranchDeviser forProject(@NotNull Project project) {
        return managedInstances.computeIfAbsent(project, BranchDeviser::new);
    }

    public @NotNull GitBranch deviseMainBranch() {
        if (mainBranch == null) {
            mainBranch = deviseBranch(PROPERTY_KEY_BRANCH_MAIN, "main", "master");
        }
        return mainBranch;
    }

    public @NotNull GitBranch deviseDevBranch() {
        if (devBranch == null) {
            devBranch = deviseBranch(PROPERTY_KEY_BRANCH_DEV, "dev");
        }
        return devBranch;
    }

    private @NotNull GitBranch deviseBranch(
            @NotNull String propertyKey,
            @NotNull String... defaultValues
    ) {
        return GitBranch.named(
                getGradleProperties().requireOrDefaultsValidated(
                        propertyKey, this::branchExists, defaultValues
                )
        );
    }

    private boolean branchExists(@NotNull String branchName) {
        return getGit().exists(GitBranch.named(branchName));
    }

    private @NotNull GradleProperties getGradleProperties() {
        if (gradleProperties == null) {
            gradleProperties = GradleProperties.forProject(project);
        }
        return gradleProperties;
    }

    private @NotNull GitAdapter getGit() {
        if (git == null) {
            git = GitAdapter.forProject(project);
        }
        return git;
    }
}
