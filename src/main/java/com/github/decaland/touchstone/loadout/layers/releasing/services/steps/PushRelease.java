package com.github.decaland.touchstone.loadout.layers.releasing.services.steps;

import com.github.decaland.touchstone.loadout.layers.releasing.models.ReleasePlan;
import com.github.decaland.touchstone.utils.scm.git.GitAdapter;
import com.github.decaland.touchstone.utils.scm.git.GitBranch;
import com.github.decaland.touchstone.utils.scm.git.GitTag;
import org.jetbrains.annotations.NotNull;

public class PushRelease extends ReleaseStep {

    private final GitBranch mainBranch;
    private final GitBranch devBranch;
    private final GitTag releaseTag;
    private final String stepDescription;

    PushRelease(
            @NotNull GitAdapter git,
            @NotNull ReleasePlan releasePlan
    ) {
        super(git);
        this.mainBranch = releasePlan.getMainBranch();
        this.devBranch = releasePlan.getDevBranch();
        this.releaseTag = releasePlan.getReleaseTag();
        this.stepDescription = String.format(
                "Pushing updated branches '%s', '%s' and tag '%s' to default remote 'origin'",
                mainBranch.getName(),
                devBranch.getName(),
                releaseTag.getName()
        );
    }

    @Override
    protected void doPerform() {
        git.push(mainBranch, devBranch, releaseTag);
    }

    @Override
    protected @NotNull Class<? extends ReleaseStep> getCurrentClass() {
        return PushRelease.class;
    }

    @Override
    public @NotNull String getStepDescription() {
        return stepDescription;
    }
}
