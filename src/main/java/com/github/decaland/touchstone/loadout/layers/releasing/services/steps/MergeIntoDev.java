package com.github.decaland.touchstone.loadout.layers.releasing.services.steps;

import com.github.decaland.touchstone.loadout.layers.releasing.models.ReleasePlan;
import com.github.decaland.touchstone.utils.scm.git.GitAdapter;
import com.github.decaland.touchstone.utils.scm.git.GitBranch;
import com.github.decaland.touchstone.utils.scm.git.GitObject;
import org.jetbrains.annotations.NotNull;

public class MergeIntoDev extends ReversibleReleaseStep {

    private final GitBranch devBranch;
    private final GitBranch releaseBranch;
    private final GitObject originalLocation;
    private final String stepDescription;
    private final String stepReversalDescription;

    MergeIntoDev(
            @NotNull GitAdapter git,
            @NotNull ReleasePlan releasePlan
    ) {
        super(git);
        this.devBranch = releasePlan.getDevBranch();
        this.releaseBranch = releasePlan.getReleaseBranch();
        this.originalLocation = git.locate(devBranch);
        this.stepDescription = String.format(
                "Merging branch '%s' into branch '%s'",
                releaseBranch.getName(),
                devBranch.getName()
        );
        this.stepReversalDescription = String.format(
                "Resetting branch '%s' to its pre-release location '%s'",
                devBranch.getName(),
                originalLocation.getShortSha()
        );
    }

    @Override
    protected void doPerform() {
        git.checkout(devBranch);
        git.mergeIntoCurrentBranch(releaseBranch);
    }

    @Override
    protected void doRevert() {
        git.checkout(devBranch);
        git.reset(originalLocation);
    }

    @Override
    protected @NotNull Class<? extends ReleaseStep> getCurrentClass() {
        return MergeIntoDev.class;
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
