package com.github.decaland.touchstone.loadout.layers.releasing.services.steps;

import com.github.decaland.touchstone.loadout.layers.releasing.models.ReleasePlan;
import com.github.decaland.touchstone.utils.scm.git.GitAdapter;
import com.github.decaland.touchstone.utils.scm.git.GitObject;
import com.github.decaland.touchstone.utils.scm.git.GitTag;
import org.jetbrains.annotations.NotNull;

public class TagRelease extends ReversibleReleaseStep {

    private final GitTag releaseTag;
    private final String stepDescription;
    private final String stepReversalDescription;

    TagRelease(
            @NotNull GitAdapter git,
            @NotNull ReleasePlan releasePlan
    ) {
        super(git);
        this.releaseTag = releasePlan.getReleaseTag();
        GitObject releaseCommit = git.locate();
        this.stepDescription = String.format(
                "Placing tag '%s' on release commit '%s'",
                releaseTag.getName(),
                releaseCommit.getShortSha()
        );
        this.stepReversalDescription = String.format(
                "Deleting tag '%s' from release commit '%s'",
                releaseTag.getName(),
                releaseCommit.getShortSha()
        );
    }

    @Override
    protected void doPerform() {
        git.tagCurrentCommit(releaseTag);
    }

    @Override
    protected void doRevert() {
        git.delete(releaseTag);
    }

    @Override
    protected @NotNull Class<? extends ReleaseStep> getCurrentClass() {
        return TagRelease.class;
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
