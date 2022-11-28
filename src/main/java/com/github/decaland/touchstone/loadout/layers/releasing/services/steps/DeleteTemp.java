package com.github.decaland.touchstone.loadout.layers.releasing.services.steps;

import com.github.decaland.touchstone.loadout.layers.releasing.models.ReleasePlan;
import com.github.decaland.touchstone.utils.scm.git.GitAdapter;
import com.github.decaland.touchstone.utils.scm.git.GitBranch;
import org.jetbrains.annotations.NotNull;

public class DeleteTemp extends ReleaseStep {

    private final GitBranch releaseBranch;
    private final String stepDescription;

    DeleteTemp(
            @NotNull GitAdapter git,
            @NotNull ReleasePlan releasePlan
    ) {
        super(git);
        this.releaseBranch = releasePlan.getReleaseBranch();
        this.stepDescription = String.format(
                "Deleting temporary release branch '%s'",
                releaseBranch.getName()
        );
    }

    @Override
    protected void doPerform() {
        git.delete(releaseBranch);
    }

    @Override
    protected @NotNull Class<? extends ReleaseStep> getCurrentClass() {
        return DeleteTemp.class;
    }

    @Override
    public @NotNull String getStepDescription() {
        return stepDescription;
    }
}
