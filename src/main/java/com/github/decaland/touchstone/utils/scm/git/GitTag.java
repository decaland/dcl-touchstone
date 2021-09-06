package com.github.decaland.touchstone.utils.scm.git;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class GitTag extends GitRef {

    private static final Map<String, GitTag> managedInstances = new HashMap<>();

    private final GitCommitMessage message;

    private GitTag(
            @NotNull String name,
            @NotNull GitCommitMessage message
    ) {
        super(Type.TAG, name);
        this.message = message;
    }

    @Contract("_ -> new")
    public static @NotNull GitTag named(@NotNull String name) {
        return new GitTag(name, new GitCommitMessage(name));
    }

    @Contract("_, _ -> new")
    public static @NotNull GitTag from(
            @NotNull String name,
            @NotNull String message
    ) {
        return new GitTag(name, new GitCommitMessage(message));
    }

    @Contract(pure = true)
    public @NotNull String getMessage() {
        return message.toString();
    }
}
