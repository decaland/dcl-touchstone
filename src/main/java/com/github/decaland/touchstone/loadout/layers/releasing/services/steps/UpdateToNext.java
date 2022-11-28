package com.github.decaland.touchstone.loadout.layers.releasing.services.steps;

import com.github.decaland.touchstone.loadout.layers.releasing.models.ReleasePlan;
import com.github.decaland.touchstone.loadout.layers.releasing.models.Version;
import com.github.decaland.touchstone.loadout.layers.releasing.services.extractor.VersionExtractor;
import com.github.decaland.touchstone.utils.scm.git.GitAdapter;
import com.github.decaland.touchstone.utils.scm.git.GitCommitMessage;
import org.jetbrains.annotations.NotNull;

import static org.gradle.api.Project.GRADLE_PROPERTIES;

public class UpdateToNext extends ReleaseStep {

    private final VersionExtractor versionExtractor;
    private final Version releaseVersion;
    private final Version nextVersion;
    private final GitCommitMessage nextCommitMessage;
    private final String stepDescription;

    UpdateToNext(
            @NotNull GitAdapter git,
            @NotNull VersionExtractor versionExtractor,
            @NotNull ReleasePlan releasePlan
    ) {
        super(git);
        this.versionExtractor = versionExtractor;
        this.releaseVersion = releasePlan.getVersionTransition().getRelease();
        this.nextVersion = releasePlan.getVersionTransition().getNext();
        this.nextCommitMessage = releasePlan.getNextCommitMsg();
        this.stepDescription = String.format(
                "Changing project version from '%s' to '%s' in '%s' file and committing this change",
                releaseVersion.asString(),
                nextVersion.asString(),
                GRADLE_PROPERTIES
        );
    }

    @Override
    protected void doPerform() {
        versionExtractor.replaceVersion(releaseVersion, nextVersion);
        try {
            git.commitFiles(nextCommitMessage, GRADLE_PROPERTIES);
        } catch (Exception exception) {
            versionExtractor.replaceVersion(nextVersion, releaseVersion);
            throw exception;
        }
    }

    @Override
    protected @NotNull Class<? extends ReleaseStep> getCurrentClass() {
        return UpdateToNext.class;
    }

    @Override
    public @NotNull String getStepDescription() {
        return stepDescription;
    }
}
