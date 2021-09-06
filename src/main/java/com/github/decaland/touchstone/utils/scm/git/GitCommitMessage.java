package com.github.decaland.touchstone.utils.scm.git;

import org.gradle.api.GradleException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static com.github.decaland.touchstone.utils.scm.git.CommonPatternMatcher.getCommonPatternMatcher;

public final class GitCommitMessage {

    private static final int COMMIT_MESSAGE_LENGTH_MIN = 10;
    private static final int COMMIT_MESSAGE_LENGTH_MAX = 255;

    private final String message;

    @Contract(pure = true)
    public GitCommitMessage(@NotNull String message) {
        validateCommitMessage(message);
        this.message = message;
    }

    private void validateCommitMessage(@NotNull String message) {
        if (message.isBlank()) {
            throw new GradleException("Attempted to create a blank Git commit message");
        }
        if (message.length() > COMMIT_MESSAGE_LENGTH_MAX || message.length() < COMMIT_MESSAGE_LENGTH_MIN) {
            throw new GradleException(String.format(
                    "Attempted to create a Git commit message outside of allowed length limits" +
                            " (%d-%d, both inclusive): '%s...' (%d characters)",
                    COMMIT_MESSAGE_LENGTH_MIN, COMMIT_MESSAGE_LENGTH_MAX, message.substring(0, 32), message.length()
            ));
        }
        if (!getCommonPatternMatcher().commitMessageMatches(message)) {
            throw new GradleException(String.format(
                    "Attempted to create a Git commit message with illegal characters in it" +
                            " (such as '\"'): '%s'",
                    message
            ));
        }
    }

    @Contract(pure = true)
    public String getMessage() {
        return message;
    }

    @Contract(pure = true)
    @Override
    public String toString() {
        return message;
    }
}
