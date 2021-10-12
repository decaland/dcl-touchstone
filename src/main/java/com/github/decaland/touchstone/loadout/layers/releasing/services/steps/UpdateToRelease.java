package com.github.decaland.touchstone.loadout.layers.releasing.services.steps;

import com.github.decaland.touchstone.loadout.layers.releasing.models.ReleasePlan;
import com.github.decaland.touchstone.loadout.layers.releasing.models.Version;
import com.github.decaland.touchstone.loadout.layers.releasing.services.extractor.VersionExtractor;
import com.github.decaland.touchstone.utils.scm.git.GitAdapter;
import com.github.decaland.touchstone.utils.scm.git.GitCommitMessage;
import org.jetbrains.annotations.NotNull;

import static org.gradle.api.Project.GRADLE_PROPERTIES;

public class UpdateToRelease extends ReleaseStep {

    private final VersionExtractor versionExtractor;
    private final Version currentVersion;
    private final Version releaseVersion;
    private final GitCommitMessage releaseCommitMessage;
    private final String stepDescription;

    UpdateToRelease(
            @NotNull GitAdapter git,
            @NotNull VersionExtractor versionExtractor,
            @NotNull ReleasePlan releasePlan
    ) {
        super(git);
        this.versionExtractor = versionExtractor;
        this.currentVersion = releasePlan.getVersionTransition().getCurrent();
        this.releaseVersion = releasePlan.getVersionTransition().getRelease();
        this.releaseCommitMessage = releasePlan.getReleaseCommitMsg();
        this.stepDescription = String.format(
                "Changing project version from '%s' to '%s' in '%s' file and committing this change",
                currentVersion.asString(),
                releaseVersion.asString(),
                GRADLE_PROPERTIES
        );
    }

    @Override
    protected void doPerform() {
        versionExtractor.replaceVersion(currentVersion, releaseVersion);
        try {
            git.commitFiles(releaseCommitMessage, GRADLE_PROPERTIES);
        } catch (Exception exception) {
            versionExtractor.replaceVersion(releaseVersion, currentVersion);
            throw exception;
        }
    }

    @Override
    protected @NotNull Class<? extends ReleaseStep> getCurrentClass() {
        return UpdateToRelease.class;
    }

    @Override
    public @NotNull String getStepDescription() {
        return stepDescription;
    }
}
