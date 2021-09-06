package com.github.decaland.touchstone.utils.scm.git;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.github.decaland.touchstone.utils.scm.git.CommonPatternMatcher.getCommonPatternMatcher;

public final class GitObject {

    private static final Map<String, GitObject> managedInstances = new HashMap<>();

    private static void validateSha(String sha) {
        if (!getCommonPatternMatcher().shaMatches(sha)) {
            throw new RuntimeException(String.format(
                    "Attempted to refer to Git object" +
                            " with SHA value that is not a 40-character hexadecimal string: '%s'",
                    sha
            ));
        }
    }

    private final String sha;
    private final String shortSha;

    @Contract(pure = true)
    private GitObject(@NotNull String sha) {
        validateSha(sha);
        this.sha = sha;
        this.shortSha = sha.substring(0, 7);
    }

    static GitObject ofSha(String sha) {
        return managedInstances.computeIfAbsent(sha, GitObject::new);
    }

    @Contract(pure = true)
    public @NotNull String getSha() {
        return sha;
    }

    @Contract(pure = true)
    public @NotNull String getShortSha() {
        return shortSha;
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof GitObject)) return false;
        GitObject gitObject = (GitObject) other;
        return sha.equals(gitObject.sha);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sha);
    }

    @Contract(pure = true)
    public String toString() {
        return String.format("GitObject{sha='%s'}", shortSha);
    }
}
