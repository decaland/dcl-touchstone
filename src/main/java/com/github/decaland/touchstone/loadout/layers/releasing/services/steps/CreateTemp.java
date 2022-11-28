package com.github.decaland.touchstone.loadout.layers.releasing.services.steps;

import com.github.decaland.touchstone.loadout.layers.releasing.models.ReleasePlan;
import com.github.decaland.touchstone.utils.scm.git.GitAdapter;
import com.github.decaland.touchstone.utils.scm.git.GitBranch;
import org.jetbrains.annotations.NotNull;

public class CreateTemp extends ReversibleReleaseStep {

    private final GitBranch devBranch;
    private final GitBranch releaseBranch;
    private final String stepDescription;
    private final String stepReversalDescription;

    CreateTemp(
            @NotNull GitAdapter git,
            @NotNull ReleasePlan releasePlan
    ) {
        super(git);
        this.devBranch = releasePlan.getDevBranch();
        this.releaseBranch = releasePlan.getReleaseBranch();
        this.stepDescription = String.format(
                "Creating and checking out temporary release branch '%s'",
                releaseBranch.getName()
        );
        this.stepReversalDescription = String.format(
                "Deleting temporary release branch '%s'",
                releaseBranch.getName()
        );
    }

    @Override
    protected void doPerform() {
        git.checkoutNew(releaseBranch);
    }

    @Override
    protected void doRevert() {
        git.checkout(devBranch);
        if (git.exists(releaseBranch)) {
            git.delete(releaseBranch);
        }
    }

    @Override
    protected @NotNull Class<? extends ReleaseStep> getCurrentClass() {
        return CreateTemp.class;
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
