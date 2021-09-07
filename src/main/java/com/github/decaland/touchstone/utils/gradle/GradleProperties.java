package com.github.decaland.touchstone.utils.gradle;

import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class GradleProperties {

    private final Project project;

    private static final Map<Project, GradleProperties> managedInstances = new HashMap<>();

    @Contract(pure = true)
    private GradleProperties(@NotNull Project project) {
        this.project = project;
    }

    synchronized public static @NotNull GradleProperties forProject(@NotNull Project project) {
        return managedInstances.computeIfAbsent(project, GradleProperties::new);
    }

    public @NotNull String requireOrDefaultsValidated(
            @NotNull String key,
            @NotNull Predicate<String> validator,
            @NotNull String... defaultValues
    ) {
        Optional<String> maybeValue = get(key);
        if (maybeValue.isPresent()) {
            return maybeValue
                    .filter(validator)
                    .orElseThrow(() -> new GradleException(String.format(
                            "Invalid value '%s' for required Gradle property '%s'", maybeValue.get(), key
                    )));
        } else {
            if (defaultValues.length == 0) {
                throw new GradleException(String.format(
                        "Missing required Gradle property '%s', and no defaults are pre-configured", key
                ));
            }
            return Arrays.stream(defaultValues)
                    .filter(validator)
                    .findFirst()
                    .orElseThrow(() -> new GradleException(String.format(
                            "Could not find a valid default value for required Gradle property '%s'" +
                                    " among candidates: '%s'",
                            key, String.join("', '", defaultValues)
                    )));
        }
    }

    public @NotNull String requireValidated(@NotNull String key, @NotNull Predicate<String> validator) {
        String value = require(key);
        if (!validator.test(value)) {
            throw new GradleException(String.format(
                    "Invalid value '%s' for required Gradle property '%s'", value, key
            ));
        }
        return value;
    }

    public @NotNull String require(@NotNull String key) {
        return get(key).orElseThrow(() -> new GradleException(String.format(
                "Missing required Gradle property '%s'", key
        )));
    }

    public @NotNull Optional<String> getOrDefaultsValidated(
            @NotNull String key,
            @NotNull Predicate<String> validator,
            @NotNull String... defaultValues
    ) {
        return get(key)
                .map(Collections::singletonList)
                .orElseGet(() -> Arrays.asList(defaultValues))
                .stream()
                .filter(validator)
                .findFirst();
    }

    public @NotNull Optional<String> getValidated(@NotNull String key, @NotNull Predicate<String> validator) {
        return get(key).filter(validator);
    }

    public @NotNull String getOrDefault(@NotNull String key, @NotNull Supplier<String> supplier) {
        return get(key).orElseGet(supplier);
    }

    public @NotNull String getOrDefault(@NotNull String key, @NotNull String defaultValue) {
        return get(key).orElse(defaultValue);
    }

    public @NotNull Optional<String> get(@NotNull String key) {
        Object value = project.findProperty(key);
        if (value instanceof String) {
            return Optional.of((String) value);
        }
        return Optional.empty();
    }

    public boolean has(@NotNull String key) {
        return project.findProperty(key) instanceof String;
    }
}
