package com.github.decaland.touchstone.loadout.layers.releasing.models;

import org.gradle.api.GradleException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class VersionTransition {

    private final Version current;
    private final Version release;
    private final Version next;

    private VersionTransition(
            @NotNull Version current,
            @NotNull Strategy strategy
    ) {
        requireIncrementableCurrentVersion(current, strategy);
        this.current = current;
        this.release = calculateReleaseVersion(current, strategy);
        this.next = calculateNextVersion(release);
    }

    private VersionTransition(
            @NotNull Version current,
            @NotNull Version release
    ) {
        requireSequentialReleaseVersion(current, release);
        this.current = current;
        this.release = release;
        this.next = calculateNextVersion(release);
    }

    @Contract("_, _ -> new")
    public static @NotNull VersionTransition from(@NotNull Version current, @NotNull Strategy strategy) {
        return new VersionTransition(current, strategy);
    }

    @Contract("_, _ -> new")
    public static @NotNull VersionTransition between(@NotNull Version current, @NotNull Version release) {
        return new VersionTransition(current, release);
    }

    @Contract("_ -> new")
    private static @NotNull Version calculateNextVersion(@NotNull Version release) {
        try {
            return new Version(
                    release.getMajorAsInt(),
                    release.getMinorAsInt(),
                    release.getPatchAsInt() + 1,
                    true
            );
        } catch (IllegalVersionException exception) {
            throw new GradleException(
                    String.format("Failed to prepare next snapshot from version '%s'", release),
                    exception
            );
        }
    }

    @Contract("_, _ -> new")
    private static @NotNull Version calculateReleaseVersion(
            @NotNull Version current,
            @NotNull VersionTransition.Strategy strategy
    ) {
        try {
            switch (strategy) {
                case INCREMENT_MAJOR:
                    return new Version(
                            current.getMajorAsInt() + 1,
                            0,
                            0,
                            false
                    );
                case INCREMENT_MINOR:
                    return new Version(
                            current.getMajorAsInt(),
                            current.getMinorAsInt() + 1,
                            0,
                            false
                    );
                case DEFAULT:
                    return new Version(
                            current.getMajorAsInt(),
                            current.getMinorAsInt(),
                            current.isSnapshot() ? current.getPatchAsInt() : current.getPatchAsInt() + 1,
                            false
                    );
                default:
                    throw new GradleException(String.format(
                            "Unrecognized version transition strategy '%s'",
                            strategy
                    ));
            }
        } catch (IllegalVersionException exception) {
            throw new GradleException(
                    String.format("Failed to increment version '%s' using strategy '%s'", current, strategy),
                    exception
            );
        }
    }

    private static void requireIncrementableCurrentVersion(
            @NotNull Version current,
            @NotNull VersionTransition.Strategy strategy
    ) {
        switch (strategy) {
            case INCREMENT_MAJOR:
                if (current.getMajorAsInt() == Integer.MAX_VALUE) {
                    throw new GradleException(String.format(
                            "Unable to increment maxed-out major component of version '%s'",
                            current
                    ));
                }
                break;
            case INCREMENT_MINOR:
                if (current.getMinorAsInt() == Integer.MAX_VALUE) {
                    throw new GradleException(String.format(
                            "Unable to increment maxed-out minor component of version '%s'",
                            current
                    ));
                }
                break;
            case DEFAULT:
                int threshold = Integer.MAX_VALUE;
                if (!current.isSnapshot()) --threshold;
                if (current.getPatchAsInt() == threshold) {
                    throw new GradleException(String.format(
                            "Unable to increment maxed-out patch component of version '%s'",
                            current
                    ));
                }
                break;
            default:
                throw new GradleException(String.format(
                        "Unrecognized version transition strategy '%s'",
                        strategy
                ));
        }
    }

    private static void requireSequentialReleaseVersion(
            @NotNull Version current,
            @NotNull Version release
    ) {
        int comparison = current.compareTo(release);
        if (comparison == 0) {
            throw new GradleException(String.format(
                    "Unable to use release version '%s' because it is equal to current version '%s'",
                    release, current
            ));
        }
        if (comparison < 0) {
            throw new GradleException(String.format(
                    "Unable to use release version '%s' because it is below current version '%s'",
                    release, current
            ));
        }
        if (release.isSnapshot()) {
            throw new GradleException(String.format("Unable to release snapshot version '%s'", release));
        }
        if (release.getPatchAsInt() == Integer.MAX_VALUE) {
            throw new GradleException(String.format(
                    "Unable to increment maxed-out patch component of version '%s'",
                    release
            ));
        }
    }

    public Version getCurrent() {
        return current;
    }

    public Version getRelease() {
        return release;
    }

    public Version getNext() {
        return next;
    }

    public enum Strategy {
        DEFAULT("patch"),
        INCREMENT_MINOR("minor"),
        INCREMENT_MAJOR("major");

        @Contract(pure = true)
        Strategy(String propertyValue) {
            this.propertyValue = propertyValue;
        }

        private final String propertyValue;

        @Contract(pure = true)
        public String getPropertyValue() {
            return propertyValue;
        }

        @Contract(pure = true)
        public static Strategy getDefault() {
            return DEFAULT;
        }

        @Contract(pure = true)
        public static String getDefaultPropertyValue() {
            return DEFAULT.getPropertyValue();
        }

        public static @NotNull VersionTransition.Strategy of(@Nullable String value) {
            return Arrays.stream(values())
                    .filter(strategy -> strategy.propertyValue.equals(value))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(String.format(
                            "Failed to retrieve %s instance for value '%s'",
                            Strategy.class.getSimpleName(), value
                    )));
        }

        public static boolean exists(@Nullable String value) {
            return Arrays.stream(values())
                    .anyMatch(strategy -> strategy.propertyValue.equals(value));
        }
    }
}
