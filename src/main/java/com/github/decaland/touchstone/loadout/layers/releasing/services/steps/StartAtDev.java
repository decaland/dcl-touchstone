package com.github.decaland.touchstone.loadout.layers.releasing.services.steps;

import com.github.decaland.touchstone.utils.scm.git.GitAdapter;
import com.github.decaland.touchstone.utils.scm.git.GitBranch;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class StartAtDev extends ReversibleReleaseStep {

    private final GitBranch originalBranch;
    private final GitBranch devBranch;
    private final boolean stepIsNoOp;
    private final String stepDescription;
    private final String stepReversalDescription;

    StartAtDev(
            @NotNull GitAdapter git,
            @NotNull GitBranch devBranch
    ) {
        super(git);
        this.originalBranch = git.requireCurrentBranch();
        this.devBranch = devBranch;
        this.stepIsNoOp = Objects.equals(originalBranch, devBranch);
        this.stepDescription = stepIsNoOp
                ? String.format("Already on branch '%s'", devBranch.getName())
                : String.format(
                "Switching from branch '%s' to branch '%s'",
                originalBranch.getName(),
                devBranch.getName()
        );
        this.stepReversalDescription = String.format("Switching back to branch '%s'", originalBranch.getName());
    }

    @Override
    protected void doPerform() {
        if (!stepIsNoOp) {
            git.checkout(devBranch);
        }
    }

    @Override
    protected void doRevert() {
        git.checkout(originalBranch);
    }

    @Override
    protected @NotNull Class<? extends ReleaseStep> getCurrentClass() {
        return StartAtDev.class;
    }

    @Override
    public @NotNull String getStepDescription() {
        return stepDescription;
    }

    @Override
    public @NotNull String getStepReversalDescription() {
        return stepReversalDescription;
    }
}
