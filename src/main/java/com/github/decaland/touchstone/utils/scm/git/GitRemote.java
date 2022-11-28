package com.github.decaland.touchstone.utils.scm.git;

import org.gradle.api.GradleException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.github.decaland.touchstone.utils.scm.git.CommonPatternMatcher.getCommonPatternMatcher;

public class GitRemote {

    private static final int REMOTE_NAME_LENGTH_LIMIT = 255;

    private final String name;

    private static final Map<String, GitRemote> managedInstances = new HashMap<>();

    private GitRemote(
            @NotNull String name
    ) {
        this.name = name;
        validateRemoteName();
    }

    public static @NotNull GitRemote named(@NotNull String name) {
        return managedInstances.computeIfAbsent(name, GitRemote::new);
    }

    private void validateRemoteName() {
        if (name.isBlank()) {
            throw new GradleException("Attempted to refer to Git remote with blank name");
        }
        if (name.length() > REMOTE_NAME_LENGTH_LIMIT) {
            throw new GradleException(String.format(
                    "Attempted to refer to Git remote with a name longer than %d characters: '%s...' (%d characters)",
                    REMOTE_NAME_LENGTH_LIMIT, name.substring(0, 16), name.length()
            ));
        }
        if (!getCommonPatternMatcher().remoteNameMatches(name)) {
            throw new GradleException(String.format(
                    "Attempted to refer to Git remote with an illegal name: '%s'", name
            ));
        }
    }

    @Contract(pure = true)
    public final @NotNull String getName() {
        return name;
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof GitRemote)) return false;
        GitRemote gitRemote = (GitRemote) other;
        return name.equals(gitRemote.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return String.format("GitRemote{name='%s'}", name);
    }
}
