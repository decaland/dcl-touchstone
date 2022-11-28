package com.github.decaland.touchstone.loadout.layers.releasing.models;

import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version implements Comparable<Version> {

    public static final String SEMVER_EXTENSION_SNAPSHOT = "SNAPSHOT";

    @RegExp
    public static final String SEMVER_PATTERN = "^" +
            "([1-9]\\d*|0)" +
            "\\." +
            "([1-9]\\d*|0)" +
            "\\." +
            "([1-9]\\d*|0)" +
            "(?:-(" + SEMVER_EXTENSION_SNAPSHOT + "))?" +
            "$";
    private static Matcher semverMatcher;

    private final int major;
    private final int minor;
    private final int patch;
    private final boolean isSnapshot;

    private String asString = null;

    @Contract(pure = true)
    public Version(int major, int minor, int patch, boolean isSnapshot) throws IllegalVersionException {
        requireValidVersionComponents(major, minor, patch);
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.isSnapshot = isSnapshot;
    }

    public Version(@NotNull String version) throws IllegalVersionException {
        MatchResult matchedVersion = matchSemver(version);
        this.major = Integer.parseInt(matchedVersion.group(1));
        this.minor = Integer.parseInt(matchedVersion.group(2));
        this.patch = Integer.parseInt(matchedVersion.group(3));
        requireValidVersionComponents(major, minor, patch);
        this.isSnapshot = SEMVER_EXTENSION_SNAPSHOT.equals(matchedVersion.group(4));
    }

    private void requireValidVersionComponents(
            int major, int minor, int patch
    ) throws IllegalVersionException {
        if (major < 0 || minor < 0 || patch < 0) {
            throw new IllegalVersionException(String.format(
                    "Version components must be non-negative: received %s, %s, %d", major, minor, patch
            ));
        }
        if (major == 0 && minor == 0 && patch == 0) {
            throw new IllegalVersionException("Version components must not be all zero");
        }
    }

    private synchronized static @NotNull MatchResult matchSemver(
            @NotNull String input
    ) throws IllegalVersionException {
        if (semverMatcher == null) {
            semverMatcher = Pattern.compile(SEMVER_PATTERN).matcher("");
        }
        semverMatcher.reset(input);
        if (!semverMatcher.matches()) {
            throw new IllegalVersionException(String.format(
                    "Version string '%s' does not match required pattern '%s'", input, SEMVER_PATTERN
            ));
        }
        return semverMatcher.toMatchResult();
    }

    public int getMajorAsInt() {
        return major;
    }

    public int getMinorAsInt() {
        return minor;
    }

    public int getPatchAsInt() {
        return patch;
    }

    public boolean isSnapshot() {
        return isSnapshot;
    }

    @Override
    public String toString() {
        return asString();
    }

    public String asString() {
        if (asString == null) {
            asString = String.format(
                    "%d.%d.%d%s",
                    major, minor, patch,
                    isSnapshot ? "-" + SEMVER_EXTENSION_SNAPSHOT : ""
            );
        }
        return asString;
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Version version = (Version) other;
        return major == version.major
                && minor == version.minor
                && patch == version.patch
                && isSnapshot == version.isSnapshot;
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch, isSnapshot);
    }

    @Override
    public int compareTo(@NotNull Version other) {
        if (major != other.major) return major - other.major;
        if (minor != other.minor) return minor - other.minor;
        if (patch != other.patch) return patch - other.patch;
        return (isSnapshot ? 0 : 1) - (other.isSnapshot ? 0 : 1);
    }
}
