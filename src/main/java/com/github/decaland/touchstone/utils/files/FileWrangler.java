package com.github.decaland.touchstone.utils.files;

import org.gradle.api.file.RegularFile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.MAX_VALUE;

public final class FileWrangler {

    private static final Map<Path, FileWrangler> managedInstances = new HashMap<>();

    private final Path filePath;

    @Contract(pure = true)
    private FileWrangler(@NotNull Path filePath) {
        this.filePath = filePath;
    }

    public static FileWrangler forPath(@NotNull Path filePath) {
        return managedInstances.computeIfAbsent(filePath, FileWrangler::new);
    }

    public static FileWrangler forFile(@NotNull File file) {
        return managedInstances.computeIfAbsent(file.toPath(), FileWrangler::new);
    }

    public static FileWrangler forRegularFile(@NotNull RegularFile regularFile) {
        return managedInstances.computeIfAbsent(regularFile.getAsFile().toPath(), FileWrangler::new);
    }

    @Contract(pure = true)
    public Path getFilePath() {
        return filePath;
    }

    public boolean contains(@NotNull Pattern pattern) throws FileWranglingException {
        return matchLine(pattern, true).size() == 1;
    }

    public boolean contains(@NotNull String needle) throws FileWranglingException {
        return matchLine(needle, true) == 1;
    }

    public int count(@NotNull Pattern pattern) throws FileWranglingException {
        return matchLine(pattern, false).size();
    }

    public int count(@NotNull String needle) throws FileWranglingException {
        return matchLine(needle, false);
    }

    public @NotNull Optional<MatchResult> matchFirst(@NotNull Pattern pattern)
            throws FileWranglingException {
        return matchLine(pattern, true).stream().findFirst();
    }

    public @NotNull List<MatchResult> matchAll(@NotNull Pattern pattern)
            throws FileWranglingException {
        return matchLine(pattern, false);
    }

    public boolean replaceFirst(
            @NotNull String needle,
            @NotNull Supplier<String> replacementSupplier
    ) throws FileWranglingException {
        return replaceLine(needle, matchNumber -> replacementSupplier.get(), true) == 1;
    }

    public boolean replaceFirst(
            @NotNull Pattern pattern,
            @NotNull Function<MatchResult, String> replacementSupplier
    ) throws FileWranglingException {
        return replaceLine(
                pattern,
                ((matchNumber, matchResult) -> replacementSupplier.apply(matchResult)),
                true
        ) == 1;
    }

    public int replaceAll(
            @NotNull String needle,
            @NotNull Function<Integer, String> replacementSupplier
    ) throws FileWranglingException {
        return replaceLine(needle, replacementSupplier, false);
    }

    public int replaceAll(
            @NotNull Pattern pattern,
            @NotNull BiFunction<Integer, MatchResult, String> replacementSupplier
    ) throws FileWranglingException {
        return replaceLine(pattern, replacementSupplier, false);
    }

    synchronized private int matchLine(
            @NotNull String needle,
            boolean firstOnly
    ) throws FileWranglingException {
        int matched = 0;
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (Objects.equals(needle, line)) {
                    if (firstOnly) {
                        return 1;
                    }
                    if (matched == MAX_VALUE) {
                        throw new FileWranglingException(String.format(
                                "Too many lines matching needle '%s' found in file '%s'",
                                needle, filePath
                        ));
                    }
                    ++matched;
                }
            }
        } catch (IOException exception) {
            throw new FileWranglingException(
                    String.format(
                            "Caught IO exception while matching lines against needle '%s' in file '%s'",
                            needle, filePath
                    ),
                    exception
            );
        }
        return matched;
    }

    synchronized private @NotNull List<MatchResult> matchLine(
            @NotNull Pattern pattern,
            boolean firstOnly
    ) throws FileWranglingException {
        List<MatchResult> result = new ArrayList<>();
        Matcher matcher = pattern.matcher("");
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                matcher.reset(line);
                if (matcher.matches()) {
                    if (result.size() == MAX_VALUE) {
                        throw new FileWranglingException(String.format(
                                "Too many lines matching pattern '%s' found in file '%s'",
                                pattern.pattern(), filePath
                        ));
                    }
                    result.add(matcher.toMatchResult());
                    if (firstOnly) {
                        break;
                    }
                }
            }
        } catch (IOException exception) {
            throw new FileWranglingException(
                    String.format(
                            "Caught IO exception while matching lines against pattern '%s' in file '%s'",
                            pattern.pattern(), filePath
                    ),
                    exception
            );
        }
        return result;
    }

    synchronized private int replaceLine(
            @NotNull String needle,
            @NotNull Function<Integer, String> replacementSupplier,
            boolean firstOnly
    ) throws FileWranglingException {
        int replaced = 0;
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (shouldReplace(firstOnly, replaced) && Objects.equals(needle, line)) {
                    if (replaced == MAX_VALUE) {
                        throw new FileWranglingException(
                                String.format(
                                        "Too many lines matching needle '%s' to replace in file '%s'",
                                        needle, filePath
                                )
                        );
                    }
                    content.append(replacementSupplier.apply(replaced++));
                } else {
                    content.append(line);
                }
                content.append(System.lineSeparator());
            }
            Files.writeString(filePath, content);
        } catch (IOException exception) {
            throw new FileWranglingException(
                    String.format(
                            "Caught IO exception while replacing lines matching needle '%s' in file '%s'",
                            needle, filePath
                    ),
                    exception
            );
        }
        return replaced;
    }

    synchronized private int replaceLine(
            @NotNull Pattern pattern,
            @NotNull BiFunction<Integer, MatchResult, String> replacementSupplier,
            boolean firstOnly
    ) throws FileWranglingException {
        int replaced = 0;
        Matcher matcher = pattern.matcher("");
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (shouldReplace(firstOnly, replaced)) {
                    matcher.reset(line);
                    if (matcher.matches()) {
                        if (replaced == MAX_VALUE) {
                            throw new FileWranglingException(
                                    String.format(
                                            "Too many lines matching pattern '%s' to replace in file '%s'",
                                            pattern.pattern(), filePath
                                    )
                            );
                        }
                        content.append(replacementSupplier.apply(replaced++, matcher.toMatchResult()));
                    } else {
                        content.append(line);
                    }
                } else {
                    content.append(line);
                }
                content.append(System.lineSeparator());
            }
            Files.writeString(filePath, content);
        } catch (IOException exception) {
            throw new FileWranglingException(
                    String.format(
                            "Caught IO exception while replacing lines matching pattern '%s' in file '%s'",
                            pattern.pattern(), filePath
                    ),
                    exception
            );
        }
        return replaced;
    }

    @Contract(value = "false, _ -> true", pure = true)
    private boolean shouldReplace(boolean firstOnly, int replaced) {
        if (firstOnly) {
            return replaced == 0;
        } else {
            return true;
        }
    }
}
