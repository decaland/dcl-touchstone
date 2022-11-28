package com.github.decaland.touchstone.utils.shell;

import org.jetbrains.annotations.NotNull;

public interface ShellExecutor {

    void require(@NotNull String executable);

    ExecutionResult exec(@NotNull String command);

    boolean test(@NotNull String command);

    ExecutionResult insist(@NotNull String command);
}
