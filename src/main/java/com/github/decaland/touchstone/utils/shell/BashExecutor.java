package com.github.decaland.touchstone.utils.shell;

import com.github.decaland.touchstone.utils.lazy.Lazy;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.process.ExecResult;
import org.gradle.process.internal.ExecException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.io.InputStream.nullInputStream;
import static java.io.OutputStream.nullOutputStream;

public class BashExecutor implements ShellExecutor {

    public static final String BASH_EXECUTABLE = "bash";

    private static final Map<Project, BashExecutor> managedInstances = new HashMap<>();

    private final Project project;
    private final Logger logger;

    private BashExecutor(@NotNull Project project) {
        this.project = project;
        this.logger = project.getLogger();
        require(BASH_EXECUTABLE);
    }

    synchronized private static @NotNull BashExecutor forProject(
            @NotNull Project project,
            @NotNull String... requiredExecutables
    ) {
        BashExecutor bashExecutor = managedInstances.computeIfAbsent(project, BashExecutor::new);
        Arrays.stream(requiredExecutables).forEach(bashExecutor::require);
        return bashExecutor;
    }

    @Contract("_, _ -> new")
    public static @NotNull Lazy<BashExecutor> lazyFor(
            @NotNull Project project,
            @NotNull String... requiredExecutables
    ) {
        return Lazy.using(() -> BashExecutor.forProject(project, requiredExecutables));
    }

    synchronized public void require(@NotNull String executable) {
        try {
            project.exec(execSpec -> {
                execSpec.setIgnoreExitValue(true);
                execSpec.setExecutable(executable);
                execSpec.setStandardInput(nullInputStream());
                execSpec.setStandardOutput(nullOutputStream());
                execSpec.setErrorOutput(nullOutputStream());
            });
        } catch (ExecException exception) {
            throw new GradleException(String.format(
                    "Failed to access required executable `%s` in current system", executable
            ));
        }
    }

    @Contract("_ -> new")
    @Override
    synchronized public @NotNull ExecutionResult exec(@NotNull String command) {
        if (logger.isInfoEnabled()) {
            logger.info("Executing command `{}` in shell `{}`", command, BASH_EXECUTABLE);
        }
        ByteArrayOutputStream stdOutStream = new ByteArrayOutputStream();
        ByteArrayOutputStream stdErrStream = new ByteArrayOutputStream();
        ExecResult execResult;
        try {
            execResult = project.exec(execSpec -> {
                execSpec.setIgnoreExitValue(true);
                execSpec.setStandardOutput(stdOutStream);
                execSpec.setErrorOutput(stdErrStream);
                execSpec.commandLine(BASH_EXECUTABLE, "-c", command);
            });
        } catch (ExecException exception) {
            throw new GradleException(String.format(
                    "Unexpected failure while executing command `%s` in shell `%s`: '%s'",
                    command, BASH_EXECUTABLE, exception.getMessage()
            ));
        }
        return new ExecutionResult(command, execResult.getExitValue(), stdOutStream, stdErrStream);
    }

    @Override
    synchronized public boolean test(@NotNull String command) {
        return exec(command).exitedNormally();
    }

    @Override
    public @NotNull ExecutionResult insist(@NotNull String command) {
        ExecutionResult executionResult = exec(command);
        if (executionResult.exitedAbnormally()) {
            if (logger.isErrorEnabled()) {
                logger.error(executionResult.getSummary());
            }
            throw new GradleException(String.format(
                    "Unexpected exit value while executing command `%s` in shell `%s`",
                    command, BASH_EXECUTABLE
            ));
        }
        return executionResult;
    }
}
