package com.github.decaland.touchstone.loadouts;

import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.util.GradleVersion;

import static com.github.decaland.touchstone.configs.BuildParametersManifest.MIN_VERSION_GRADLE;

abstract class GradleVersionAwareLoadout extends BaseLoadout {

    private static final String MSG_UNSUPPORTED_GRADLE_VERSION
            = "Touchstone plugin requires %s. Current version is %s";

    public GradleVersionAwareLoadout(Project project) {
        super(project);
        validateGradleVersion();
    }

    private void validateGradleVersion() {
        GradleVersion currentVersion = GradleVersion.current();
        GradleVersion minVersion = GradleVersion.version(MIN_VERSION_GRADLE);
        if (currentVersion.compareTo(minVersion) < 0) {
            throw new GradleException(String.format(MSG_UNSUPPORTED_GRADLE_VERSION, minVersion, currentVersion));
        }
    }
}
