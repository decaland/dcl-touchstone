package com.github.decaland.touchstone.loadout.layers.releasing.services.steps;

import com.github.decaland.touchstone.loadout.layers.releasing.models.ReleasePlan;
import com.github.decaland.touchstone.utils.scm.git.GitAdapter;
import com.github.decaland.touchstone.utils.scm.git.GitBranch;
import org.jetbrains.annotations.NotNull;

public class SwitchToTemp extends ReleaseStep {

    private final GitBranch releaseBranch;
    private final String stepDescription;

    SwitchToTemp(
            @NotNull GitAdapter git,
            @NotNull ReleasePlan releasePlan
    ) {
        super(git);
        this.releaseBranch = releasePlan.getReleaseBranch();
        this.stepDescription = String.format("Checking out temporary release branch '%s'", releaseBranch.getName());
    }

    @Override
    protected void doPerform() {
        git.checkout(releaseBranch);
    }

    @Override
    protected @NotNull Class<? extends ReleaseStep> getCurrentClass() {
        return SwitchToTemp.class;
    }

    @Override
    public @NotNull String getStepDescription() {
        return stepDescription;
    }
}
