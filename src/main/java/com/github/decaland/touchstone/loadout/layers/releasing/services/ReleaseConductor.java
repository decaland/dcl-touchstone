package com.github.decaland.touchstone.loadout.layers.releasing.services;

import com.github.decaland.touchstone.loadout.layers.releasing.models.ReleasePlan;
import com.github.decaland.touchstone.loadout.layers.releasing.models.VersionTransition;
import com.github.decaland.touchstone.loadout.layers.releasing.services.devisers.BranchDeviser;
import com.github.decaland.touchstone.loadout.layers.releasing.services.extractors.VersionExtractor;
import com.github.decaland.touchstone.loadout.layers.releasing.steps.ReleaseStep;
import com.github.decaland.touchstone.utils.lazy.Lazy;
import com.github.decaland.touchstone.utils.scm.git.GitAdapter;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static org.gradle.api.Project.GRADLE_PROPERTIES;

public class ReleaseConductor {

    private final Project project;
    private final Lazy<GitAdapter> git;
    private final Lazy<BranchDeviser> branchDeviser;
    private final Lazy<VersionExtractor> versionExtractor;
    private final Lazy<ReleasePlanner> releasePlanner;

    private final Lazy<Deque<ReleaseStep>> stepsPerformed = Lazy.using(ArrayDeque::new);

    private static final Map<Project, ReleaseConductor> managedInstances = new HashMap<>();

    private ReleaseConductor(@NotNull Project project) {
        this.project = project;
        this.git = GitAdapter.lazyFor(project);
        this.branchDeviser = BranchDeviser.lazyFor(project);
        this.versionExtractor = VersionExtractor.lazyFor(project);
        this.releasePlanner = ReleasePlanner.lazyFor(project);
    }

    synchronized private static @NotNull ReleaseConductor forProject(@NotNull Project project) {
        return managedInstances.computeIfAbsent(project, ReleaseConductor::new);
    }

    @Contract("_ -> new")
    public static @NotNull Lazy<ReleaseConductor> lazyFor(@NotNull Project project) {
        return Lazy.using(() -> ReleaseConductor.forProject(project));
    }

    public void planRelease() {
        doAnalyze();
    }

    public void createRelease() {
        doPrepareRelease();
        doMergeRelease();
    }

    public void finalizeRelease() {
        doPrepareNext();
        doMergeNext();
        doCleanup();
        doPush();
    }

    private void doAnalyze() {
        try {
            GitAdapter git = this.git.get();
            git.requireCleanWorkTree();
            git.checkout(branchDeviser.get().deviseDevBranch());
            announceIntentions(releasePlanner.get().getReleasePlan());
        } catch (GradleException exception) {
            throw new GradleException(String.format(
                    "Halted release while analyzing current project state: %s", exception.getMessage()
            ));
        }
    }

    private void doPrepareRelease() {
        try {
            GitAdapter git = this.git.get();
            ReleasePlan releasePlan = releasePlanner.get().getReleasePlan();
            VersionTransition versionTransition = releasePlan.getVersionTransition();
            git.checkoutNew(releasePlan.getReleaseBranch());
            versionExtractor.get().replaceVersion(versionTransition.getCurrent(), versionTransition.getRelease());
            git.commitFiles(releasePlan.getReleaseCommitMsg(), GRADLE_PROPERTIES);
            git.tagCurrentCommit(releasePlan.getReleaseTag());
        } catch (GradleException exception) {
            throw new GradleException(String.format(
                    "Error while preparing release commit: %s", exception.getMessage()
            ));
        }
    }

    private void doMergeRelease() {
        try {
            GitAdapter git = this.git.get();
            ReleasePlan releasePlan = releasePlanner.get().getReleasePlan();
            git.checkout(releasePlan.getMainBranch());
            git.mergeIntoCurrentBranch(releasePlan.getReleaseBranch());
        } catch (GradleException exception) {
            throw new GradleException(String.format(
                    "Error while merging release into main branch: %s", exception.getMessage()
            ));
        }
    }

    private void doPrepareNext() {
        try {
            GitAdapter git = this.git.get();
            ReleasePlan releasePlan = releasePlanner.get().getReleasePlan();
            VersionTransition versionTransition = releasePlan.getVersionTransition();
            git.checkout(releasePlan.getReleaseBranch());
            versionExtractor.get().replaceVersion(versionTransition.getRelease(), versionTransition.getNext());
            git.commitFiles(releasePlan.getNextCommitMsg(), GRADLE_PROPERTIES);
        } catch (GradleException exception) {
            throw new GradleException(String.format(
                    "Error while preparing next iteration commit: %s", exception.getMessage()
            ));
        }
    }

    private void doMergeNext() {
        try {
            GitAdapter git = this.git.get();
            ReleasePlan releasePlan = releasePlanner.get().getReleasePlan();
            git.checkout(releasePlan.getDevBranch());
            git.mergeIntoCurrentBranch(releasePlan.getReleaseBranch());
        } catch (GradleException exception) {
            throw new GradleException(String.format(
                    "Error while merging next iteration into dev branch: %s", exception.getMessage()
            ));
        }
    }

    private void doCleanup() {
        try {
            GitAdapter git = this.git.get();
            ReleasePlan releasePlan = releasePlanner.get().getReleasePlan();
            git.delete(releasePlan.getReleaseBranch());
        } catch (GradleException exception) {
            throw new GradleException(String.format(
                    "Error while cleaning up after release: %s", exception.getMessage()
            ));
        }
    }

    private void doPush() {
        try {
            GitAdapter git = this.git.get();
            ReleasePlan releasePlan = releasePlanner.get().getReleasePlan();
            git.push(releasePlan.getMainBranch(), releasePlan.getDevBranch(), releasePlan.getReleaseTag());
        } catch (GradleException exception) {
            throw new GradleException(String.format(
                    "Error while pushing release objects: %s", exception.getMessage()
            ));
        }
    }

    private void announceIntentions(@NotNull ReleasePlan releasePlan) {
        Logger logger = project.getLogger();
        if (!logger.isLifecycleEnabled()) {
            return;
        }
        VersionTransition versionTransition = releasePlan.getVersionTransition();
        logger.lifecycle(
                "Releasing project '{}': current version is '{}'," +
                        " releasing version '{}', next version will be '{}';" +
                        " Splitting from branch '{}', merging into branch '{}'",
                project.getName(), versionTransition.getCurrent(),
                versionTransition.getRelease(), versionTransition.getNext(),
                releasePlan.getDevBranch().getName(), releasePlan.getMainBranch().getName()
        );
    }

    private synchronized void revertSteps() {
        Deque<ReleaseStep> stepsPerformed = this.stepsPerformed.get();
        while (!stepsPerformed.isEmpty()) {
            try {
                stepsPerformed.pop().revert();
            } catch (Exception exception) {
                if (stepsPerformed.isEmpty()) throw exception;
                String newMessage = String.format(
                        "%s; Consequently, the following steps were not reverted: %s",
                        exception.getMessage(),
                        describeRemainingSteps()
                );
                if (exception instanceof GradleException) {
                    throw new GradleException(newMessage, exception);
                } else {
                    throw new RuntimeException(newMessage, exception);
                }
            }
        }
    }

    private @NotNull String describeRemainingSteps() {
        Iterator<ReleaseStep> iterator = stepsPerformed.get().descendingIterator();
        ReleaseStep lastStep = iterator.next();
        StringBuilder stepList = new StringBuilder()
                .append(String.format(
                        "%s (%s)",
                        lastStep.getClass().getSimpleName(),
                        lastStep.getStepDescription()
                ));
        while (iterator.hasNext()) {
            lastStep = iterator.next();
            stepList.append(String.format(
                    ", %s (%s)",
                    lastStep.getClass().getSimpleName(),
                    lastStep.getStepDescription()
            ));
        }
        return stepList.toString();
    }
}
