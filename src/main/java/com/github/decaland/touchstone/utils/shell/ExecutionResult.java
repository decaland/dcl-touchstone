package com.github.decaland.touchstone.utils.shell;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ExecutionResult {

    private final String command;
    private final int exitValue;
    private final ByteArrayOutputStream stdOutStream;
    private final ByteArrayOutputStream stdErrStream;

    private String stdOut = null;
    private String stdErr = null;
    private String summary = null;

    private static Pattern lineBreak = null;

    @Contract(pure = true)
    ExecutionResult(
            @NotNull String command,
            int exitValue,
            @NotNull ByteArrayOutputStream stdOutStream,
            @NotNull ByteArrayOutputStream stdErrStream
    ) {
        this.command = command;
        this.exitValue = exitValue;
        this.stdOutStream = stdOutStream;
        this.stdErrStream = stdErrStream;
    }

    @Contract(pure = true)
    public boolean exitedNormally() {
        return exitValue == 0;
    }

    @Contract(pure = true)
    public boolean exitedAbnormally() {
        return exitValue != 0;
    }

    public synchronized @NotNull String getStdOut() {
        if (this.stdOut == null) {
            this.stdOut = this.stdOutStream.toString(StandardCharsets.UTF_8).strip();
        }
        return this.stdOut;
    }

    public synchronized @NotNull String getStdErr() {
        if (this.stdErr == null) {
            this.stdErr = this.stdErrStream.toString(StandardCharsets.UTF_8).strip();
        }
        return this.stdErr;
    }

    public synchronized @NotNull String getSummary() {
        if (this.summary == null) {
            this.summary = composeSummary();
        }
        return this.summary;
    }

    private @NotNull String composeSummary() {
        return (
                exitedNormally()
                        ? "Successfully executed"
                        : String.format("Abnormal exit value '%d' after executing", exitValue)
        ) + (
                String.format(" command `%s`", command)
        ) + (
                (stdOutStream.size() > 0 || stdErrStream.size() > 0) ? ":" : ""
        ) + (
                stdOutStream.size() > 0
                        ? String.format("\nStandard output:\n%s", enquoteStringLines(getStdOut()))
                        : ""
        ) + (
                stdErrStream.size() > 0
                        ? String.format("\nStandard error:\n%s", enquoteStringLines(getStdErr()))
                        : ""
        );
    }

    private static @NotNull String enquoteStringLines(@NotNull String input) {
        return Arrays.stream(getLineBreak().split(input))
                .collect(Collectors.joining("\n  > ", "  > ", ""));
    }

    private static @NotNull Pattern getLineBreak() {
        if (lineBreak == null) {
            lineBreak = Pattern.compile("\\r?\\n");
        }
        return lineBreak;
    }
}
