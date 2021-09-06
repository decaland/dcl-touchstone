package com.github.decaland.touchstone.loadout.layers.releasing.services.gradle;

import com.github.decaland.touchstone.utils.files.FileWrangler;
import com.github.decaland.touchstone.utils.files.FileWranglingException;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.gradle.api.Project.GRADLE_PROPERTIES;

public final class GradlePropertiesFile {

    private final Project project;
    private FileWrangler file;

    @RegExp
    public static final String PROPERTY_KEY_PATTERN = "^[\\w-]+(\\.[\\w-]+)*$";
    private static Matcher propertyKeyMatcher;

    private static final Map<Project, GradlePropertiesFile> managedInstances = new HashMap<>();

    @Contract(pure = true)
    private GradlePropertiesFile(@NotNull Project project) {
        this.project = project;
    }

    synchronized public static @NotNull GradlePropertiesFile forProject(@NotNull Project project) {
        return managedInstances.computeIfAbsent(project, GradlePropertiesFile::new);
    }

    private static boolean keyIsInvalid(@NotNull String key) {
        if (propertyKeyMatcher == null) {
            propertyKeyMatcher = Pattern.compile(PROPERTY_KEY_PATTERN).matcher("");
        }
        return !propertyKeyMatcher.reset(key).matches();
    }

    private @NotNull Pattern requireKeyValuePattern(@NotNull String key) {
        if (keyIsInvalid(key)) {
            throw new RuntimeException(String.format(
                    "Unable to search for invalid Gradle property key '%s' in '%s' file", key, GRADLE_PROPERTIES
            ));
        }
        return Pattern.compile(String.format(
                "(^\\s*%s\\s*=\\s*)(\\S.*)$",
                key.replace(".", "\\.")
        ));
    }

    public @NotNull String require(@NotNull String key) {
        return get(key).orElseThrow(() -> new GradleException(String.format(
                "Failed to find project property key '%s' in file '%s'", key, GRADLE_PROPERTIES
        )));
    }

    synchronized public @NotNull Optional<String> get(@NotNull String key) {
        try {
            return getFile().matchFirst(requireKeyValuePattern(key))
                    .map(match -> match.group(2).strip());
        } catch (FileWranglingException exception) {
            throw new GradleException(String.format(
                    "Failed while extracting version from file '%s'", GRADLE_PROPERTIES
            ));
        }
    }

    synchronized public void replace(
            @NotNull String key,
            @NotNull String oldValue,
            @NotNull String newValue
    ) {
        AtomicReference<String> matchedValue = new AtomicReference<>();
        boolean successfullyUpdated;
        try {
            successfullyUpdated = getFile().replaceFirst(
                    requireKeyValuePattern(key),
                    matchResult -> {
                        matchedValue.set(matchResult.group(2).strip());
                        return String.format("%s%s", matchResult.group(1), newValue);
                    }
            );
        } catch (FileWranglingException exception) {
            throw new GradleException(String.format(
                    "Failed while updating Gradle property '%s' from '%s' to '%s' in '%s' file",
                    key, oldValue, newValue, GRADLE_PROPERTIES
            ));
        }
        if (!successfullyUpdated) {
            throw new GradleException(String.format(
                    "Failed to find Gradle property '%s' in '%s' file (to update it from '%s' to '%s')",
                    key, GRADLE_PROPERTIES, oldValue, newValue
            ));
        }
        if (!oldValue.equals(matchedValue.get())) {
            throw new GradleException(String.format(
                    "Encountered unexpected value '%s' for Gradle property '%s' in '%s' file" +
                            " while updating it from '%s' to '%s'",
                    matchedValue.get(), key, GRADLE_PROPERTIES, oldValue, newValue
            ));
        }
    }

    synchronized public boolean has(@NotNull String key) {
        try {
            return getFile().contains(requireKeyValuePattern(key));
        } catch (FileWranglingException exception) {
            throw new GradleException(String.format(
                    "Failed while searching for Gradle property key '%s' in '%s' file", key, GRADLE_PROPERTIES
            ));
        }
    }

    private @NotNull FileWrangler getFile() {
        if (file == null) {
            this.file = FileWrangler.forRegularFile(
                    project.getLayout().getProjectDirectory().file(GRADLE_PROPERTIES)
            );
        }
        return file;
    }
}
