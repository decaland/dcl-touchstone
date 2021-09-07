package com.github.decaland.touchstone.loadout.layers.releasing.services.extractors;

import com.github.decaland.touchstone.loadout.layers.releasing.models.IllegalVersionException;
import com.github.decaland.touchstone.loadout.layers.releasing.models.Version;
import com.github.decaland.touchstone.utils.gradle.GradlePropertiesFile;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static org.gradle.api.Project.GRADLE_PROPERTIES;

public class VersionExtractor {

    public static final String PROPERTY_KEY_VERSION = "version";

    private final Project project;
    private GradlePropertiesFile gradlePropertiesFile;

    private static final Map<Project, VersionExtractor> managedInstances = new HashMap<>();

    @Contract(pure = true)
    private VersionExtractor(Project project) {
        this.project = project;
    }

    synchronized public static @NotNull VersionExtractor forProject(@NotNull Project project) {
        return managedInstances.computeIfAbsent(project, VersionExtractor::new);
    }

    public @NotNull Version extractVersion() {
        String versionString = getGradlePropertiesFile().require(PROPERTY_KEY_VERSION);
        try {
            return new Version(versionString);
        } catch (IllegalVersionException exception) {
            throw new GradleException(String.format(
                    "File '%s' contains invalid current version '%s'", GRADLE_PROPERTIES, versionString
            ));
        }
    }

    public void replaceVersion(@NotNull Version oldVersion, @NotNull Version newVersion) {
        getGradlePropertiesFile().replace(
                PROPERTY_KEY_VERSION,
                oldVersion.asString(),
                newVersion.asString()
        );
    }

    private @NotNull GradlePropertiesFile getGradlePropertiesFile() {
        if (gradlePropertiesFile == null) {
            gradlePropertiesFile = GradlePropertiesFile.forProject(project);
        }
        return gradlePropertiesFile;
    }
}
