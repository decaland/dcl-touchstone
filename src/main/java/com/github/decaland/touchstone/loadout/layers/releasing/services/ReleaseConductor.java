package com.github.decaland.touchstone.loadout.layers.releasing.services;

import com.github.decaland.touchstone.loadout.layers.releasing.models.ReleasePlan;
import com.github.decaland.touchstone.loadout.layers.releasing.models.VersionTransition;
import com.github.decaland.touchstone.loadout.layers.releasing.services.planner.ReleasePlanner;
import com.github.decaland.touchstone.loadout.layers.releasing.services.steps.ReleaseStepper;
import com.github.decaland.touchstone.utils.lazy.Lazy;
import com.github.decaland.touchstone.utils.scm.git.GitAdapter;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.github.decaland.touchstone.loadout.layers.releasing.ReleaseFlowLayer.RELEASE_MESSAGE_MARKER;

public class ReleaseConductor {

    private final Project project;
    private final Lazy<ReleaseStepper> stepper;
    private final Lazy<GitAdapter> git;
    private final Lazy<ReleasePlanner> releasePlanner;

    private boolean releaseIsAborted = false;
    private boolean releaseIsPushed = false;

    private static final Map<Project, ReleaseConductor> managedInstances = new HashMap<>();

    private ReleaseConductor(@NotNull Project project) {
        this.project = project;
        this.stepper = ReleaseStepper.lazyFor(project);
        this.git = GitAdapter.lazyFor(project);
        this.releasePlanner = ReleasePlanner.lazyFor(project);
    }

    synchronized private static @NotNull ReleaseConductor forProject(@NotNull Project project) {
        return managedInstances.computeIfAbsent(project, ReleaseConductor::new);
    }

    @Contract("_ -> new")
    public static @NotNull Lazy<ReleaseConductor> lazyFor(@NotNull Project project) {
        return Lazy.using(() -> ReleaseConductor.forProject(project));
    }

    synchronized public void planRelease() {
        tryOrAbort(this::doPlanRelease, "analyzing current project state");
    }

    synchronized public void createRelease() {
        tryOrAbort(this::doCreateRelease, "creating, tagging, and merging release commit");
    }

    synchronized public void createNext() {
        tryOrAbort(this::doCreateNext, "creating and merging next-iteration commit");
    }

    synchronized public void pushRelease() {
        tryOrAbort(this::doPushRelease, "pushing release-related commits");
    }

    private void doPlanRelease() {
        git.get().requireCleanWorkTree();
        stepper.get().startAtDev();
        announceIntentions(releasePlanner.get().getReleasePlan());
    }

    private void doCreateRelease() {
        ReleaseStepper stepper = this.stepper.get();
        stepper.createTemp();
        stepper.updateToRelease();
        stepper.mergeIntoMain();
        stepper.tagRelease();
    }

    private void doCreateNext() {
        ReleaseStepper stepper = this.stepper.get();
        stepper.switchToTemp();
        stepper.updateToNext();
        stepper.mergeIntoDev();
        stepper.deleteTemp();
    }

    private void doPushRelease() {
        stepper.get().pushRelease();
        releaseIsPushed = true;
    }

    private void tryOrAbort(@NotNull Runnable activity, @NotNull String activityDescription) {
        try {
            activity.run();
        } catch (Exception exception) {
            abortRelease();
            throw new GradleException(
                    String.format("Halted release while %s: %s", activityDescription, exception.getMessage()),
                    exception
            );
        }
    }

    synchronized public void abortRelease() {
        if (releaseIsAborted) {
            return;
        }
        if (releaseIsPushed) {
            project.getLogger().error(
                    "{} Unable to abort release after changes are already pushed",
                    RELEASE_MESSAGE_MARKER
            );
            return;
        }
        project.getLogger().error("{} Aborting release", RELEASE_MESSAGE_MARKER);
        stepper.get().revertSteps();
        releaseIsAborted = true;
    }

    private void announceIntentions(@NotNull ReleasePlan releasePlan) {
        Logger logger = project.getLogger();
        if (!logger.isLifecycleEnabled()) {
            return;
        }
        VersionTransition versionTransition = releasePlan.getVersionTransition();
        logger.lifecycle(
                "{} Release plan for project '{}':\n" +
                        "Working directory       : {}\n" +
                        "Current version         : {}\n" +
                        "Releasing version       : {}\n" +
                        "Next iteration          : {}\n" +
                        "Taking source from      : branch '{}' (currently at commit '{}')\n" +
                        "Merging release into    : branch '{}' (currently at commit '{}')",
                RELEASE_MESSAGE_MARKER, project.getName(),
                project.getProjectDir().getAbsolutePath(),
                versionTransition.getCurrent(),
                versionTransition.getRelease(),
                versionTransition.getNext(),
                releasePlan.getDevBranch().getName(), releasePlan.getDevBranchOrigin().getShortSha(),
                releasePlan.getMainBranch().getName(), releasePlan.getMainBranchOrigin().getShortSha()
        );
    }
}
