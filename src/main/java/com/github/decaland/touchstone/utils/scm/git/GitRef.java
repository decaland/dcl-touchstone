package com.github.decaland.touchstone.utils.scm.git;

import org.gradle.api.GradleException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.github.decaland.touchstone.utils.scm.git.CommonPatternMatcher.getCommonPatternMatcher;

public abstract class GitRef {

    private static final int REF_NAME_LENGTH_LIMIT = 255;

    protected final Type type;
    protected final String name;
    protected final String fullName;

    protected GitRef(
            @NotNull Type type,
            @NotNull String name
    ) {
        validateRefName(name);
        this.type = type;
        this.name = name;
        this.fullName = String.format("%s%s", type.getNamePrefix(), name);
    }

    protected void validateRefName(@NotNull String name) {
        if (name.isBlank()) {
            throw new GradleException(String.format(
                    "Attempted to refer to %s with blank name",
                    type.getHumanReadableType()
            ));
        }
        if (name.length() > REF_NAME_LENGTH_LIMIT) {
            throw new GradleException(String.format(
                    "Attempted to refer to %s with a name longer than %d characters: '%s...' (%d characters)",
                    type.getHumanReadableType(), REF_NAME_LENGTH_LIMIT, name.substring(0, 16), name.length()
            ));
        }
        if (!getCommonPatternMatcher().refNameMatches(name)) {
            throw new GradleException(String.format(
                    "Attempted to refer to %s with an illegal name: '%s'",
                    type.getHumanReadableType(), name
            ));
        }
    }

    @Contract(pure = true)
    public final @NotNull Type getType() {
        return type;
    }

    @Contract(pure = true)
    public final @NotNull String getName() {
        return name;
    }

    @Contract(pure = true)
    public final @NotNull String getFullName() {
        return fullName;
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public final boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof GitRef)) return false;
        GitRef gitRef = (GitRef) other;
        return type == gitRef.type && name.equals(gitRef.name);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(type, name);
    }

    @Override
    public String toString() {
        return String.format("%s{name='%s'}", type.getStringType(), name);
    }

    public enum Type {

        BRANCH("refs/heads/", "GitBranch", "Git branch"),
        TAG("refs/tags/", "GitTag", "Git tag");

        private final String namePrefix;
        private final String stringType;
        private final String humanReadableType;

        @Contract(pure = true)
        Type(
                @NotNull String namePrefix,
                @NotNull String stringType,
                @NotNull String humanReadableType
        ) {
            this.namePrefix = namePrefix;
            this.stringType = stringType;
            this.humanReadableType = humanReadableType;
        }

        @Contract(pure = true)
        public @NotNull String getNamePrefix() {
            return namePrefix;
        }

        @Contract(pure = true)
        public @NotNull String getStringType() {
            return stringType;
        }

        @Contract(pure = true)
        public @NotNull String getHumanReadableType() {
            return humanReadableType;
        }
    }
}
