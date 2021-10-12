package com.github.decaland.touchstone.loadout.layers.releasing.services.steps;

import com.github.decaland.touchstone.loadout.layers.releasing.services.devisers.BranchDeviser;
import com.github.decaland.touchstone.loadout.layers.releasing.services.extractor.VersionExtractor;
import com.github.decaland.touchstone.loadout.layers.releasing.services.planner.ReleasePlanner;
import com.github.decaland.touchstone.utils.lazy.Lazy;
import com.github.decaland.touchstone.utils.scm.git.GitAdapter;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.github.decaland.touchstone.loadout.layers.releasing.ReleaseFlowLayer.RELEASE_MESSAGE_MARKER;

public class ReleaseStepper {

    private final Logger logger;
    private final Lazy<GitAdapter> git;
    private final Lazy<BranchDeviser> branchDeviser;
    private final Lazy<ReleasePlanner> releasePlanner;
    private final Lazy<VersionExtractor> versionExtractor;

    private boolean stepsAreReverted = false;
    private boolean changesArePushed = false;

    private final Lazy<Deque<ReleaseStep>> stepsPerformed = Lazy.using(ArrayDeque::new);

    private static final Map<Project, ReleaseStepper> managedInstances = new HashMap<>();

    private ReleaseStepper(@NotNull Project project) {
        this.logger = project.getLogger();
        this.git = GitAdapter.lazyFor(project);
        this.branchDeviser = BranchDeviser.lazyFor(project);
        this.releasePlanner = ReleasePlanner.lazyFor(project);
        this.versionExtractor = VersionExtractor.lazyFor(project);
    }

    synchronized private static @NotNull ReleaseStepper forProject(@NotNull Project project) {
        return managedInstances.computeIfAbsent(project, ReleaseStepper::new);
    }

    @Contract("_ -> new")
    public static @NotNull Lazy<ReleaseStepper> lazyFor(@NotNull Project project) {
        return Lazy.using(() -> ReleaseStepper.forProject(project));
    }

    synchronized public void startAtDev() {
        performAndStore(
                StartAtDev.class,
                () -> new StartAtDev(git.get(), branchDeviser.get().deviseDevBranch())
        );
    }

    synchronized public void createTemp() {
        performAndStore(
                CreateTemp.class,
                () -> new CreateTemp(git.get(), releasePlanner.get().getReleasePlan())
        );
    }

    synchronized public void updateToRelease() {
        performAndStore(
                UpdateToRelease.class,
                () -> new UpdateToRelease(git.get(), versionExtractor.get(), releasePlanner.get().getReleasePlan())
        );
    }

    synchronized public void mergeIntoMain() {
        performAndStore(
                MergeIntoMain.class,
                () -> new MergeIntoMain(git.get(), releasePlanner.get().getReleasePlan())
        );
    }

    synchronized public void tagRelease() {
        performAndStore(
                TagRelease.class,
                () -> new TagRelease(git.get(), releasePlanner.get().getReleasePlan())
        );
    }

    synchronized public void switchToTemp() {
        performAndStore(
                SwitchToTemp.class,
                () -> new SwitchToTemp(git.get(), releasePlanner.get().getReleasePlan())
        );
    }

    synchronized public void updateToNext() {
        performAndStore(
                UpdateToNext.class,
                () -> new UpdateToNext(git.get(), versionExtractor.get(), releasePlanner.get().getReleasePlan())
        );
    }

    synchronized public void mergeIntoDev() {
        performAndStore(
                MergeIntoDev.class,
                () -> new MergeIntoDev(git.get(), releasePlanner.get().getReleasePlan())
        );
    }

    synchronized public void deleteTemp() {
        performAndStore(
                DeleteTemp.class,
                () -> new DeleteTemp(git.get(), releasePlanner.get().getReleasePlan())
        );
    }

    synchronized public void pushRelease() {
        performAndStore(
                PushRelease.class,
                () -> new PushRelease(git.get(), releasePlanner.get().getReleasePlan())
        );
        changesArePushed = true;
    }

    private <T extends ReleaseStep> void performAndStore(
            @NotNull Class<T> releaseStepClass,
            @NotNull Supplier<T> releaseStepSupplier
    ) {
        if (stepsAreReverted) {
            throw new RuntimeException(String.format(
                    "Attempted to perform release step %s when release is already aborted",
                    releaseStepClass.getSimpleName()
            ));
        }
        if (changesArePushed) {
            throw new RuntimeException(String.format(
                    "Attempted to perform release step %s when release is already pushed",
                    releaseStepClass.getSimpleName()
            ));
        }
        try {
            ReleaseStep releaseStep = releaseStepSupplier.get();
            logger.lifecycle(String.format(
                    "%s Performing    >> %-16s : %s",
                    RELEASE_MESSAGE_MARKER,
                    releaseStep.getClass().getSimpleName(),
                    releaseStep.getStepDescription()
            ));
            releaseStep.perform();
            stepsPerformed.get().push(releaseStep);
        } catch (Exception exception) {
            logger.error(String.format(
                    "%s Error         xx %-16s : %s",
                    RELEASE_MESSAGE_MARKER,
                    releaseStepClass.getSimpleName(),
                    exception.getMessage()
            ));
            throw exception;
        }
    }

    synchronized public void revertSteps() {
        if (stepsAreReverted) {
            return;
        }
        if (changesArePushed) {
            throw new RuntimeException("Attempted to revert release steps when release is already pushed");
        }
        Deque<ReleaseStep> stepsPerformed = this.stepsPerformed.get();
        while (!stepsPerformed.isEmpty()) {
            ReleaseStep latestStep = stepsPerformed.pop();
            if (!(latestStep instanceof ReversibleReleaseStep)) {
                continue;
            }
            logger.error(String.format(
                    "%s Reverting     << %-16s : %s",
                    RELEASE_MESSAGE_MARKER,
                    latestStep.getClass().getSimpleName(),
                    ((ReversibleReleaseStep) latestStep).getStepReversalDescription()
            ));
            try {
                ((ReversibleReleaseStep) latestStep).revert();
            } catch (Exception exception) {
                logger.error(
                        String.format(
                                "%s Error         xx %-16s : %s",
                                RELEASE_MESSAGE_MARKER,
                                latestStep.getClass().getSimpleName(),
                                exception.getMessage()
                        ),
                        exception
                );
                stopRevertingSteps();
                break;
            }
        }
        stepsAreReverted = true;
    }

    synchronized private void stopRevertingSteps() {
        Deque<ReleaseStep> stepsPerformed = this.stepsPerformed.get();
        List<ReversibleReleaseStep> remainingReversibleSteps = new ArrayList<>(stepsPerformed.size());
        while (!stepsPerformed.isEmpty()) {
            ReleaseStep latestStep = stepsPerformed.pop();
            if (latestStep instanceof ReversibleReleaseStep) {
                remainingReversibleSteps.add((ReversibleReleaseStep) latestStep);
            }
        }
        if (remainingReversibleSteps.isEmpty()) {
            logger.error("{} All other steps have been reverted", RELEASE_MESSAGE_MARKER);
        }
        logger.error(
                "{} The following steps were not reverted:\n{}",
                RELEASE_MESSAGE_MARKER,
                remainingReversibleSteps.stream()
                        .map(step -> String.format(
                                "                        -- %-16s : %s",
                                step.getClass().getSimpleName(),
                                step.getStepReversalDescription()
                        ))
                        .collect(Collectors.joining("\n"))
        );
    }
}
