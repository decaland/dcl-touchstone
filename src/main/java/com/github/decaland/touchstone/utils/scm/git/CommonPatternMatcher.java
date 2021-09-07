package com.github.decaland.touchstone.utils.scm.git;

import com.github.decaland.touchstone.utils.lazy.Lazy;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class CommonPatternMatcher {

    @RegExp
    public static final String PATTERN_SHA = "^[0-9a-fA-F]{40}$";

    @RegExp
    public static final String PATTERN_REF_NAME = "^[\\w.-]+(/[\\w.-]+)+$";

    @RegExp
    public static final String PATTERN_COMMIT_MESSAGE = "^[^\\s\"][^\"]*[^\\s\"]$";

    private final Lazy<Matcher> shaMatcher;
    private final Lazy<Matcher> refNameMatcher;
    private final Lazy<Matcher> commitMessageMatcher;

    private static CommonPatternMatcher instance;

    @Contract(pure = true)
    private CommonPatternMatcher() {
        shaMatcher = Lazy.using(() -> compile(PATTERN_SHA));
        refNameMatcher = Lazy.using(() -> compile(PATTERN_REF_NAME));
        commitMessageMatcher = Lazy.using(() -> compile(PATTERN_COMMIT_MESSAGE));
    }

    public static @NotNull CommonPatternMatcher getCommonPatternMatcher() {
        if (instance == null) {
            instance = new CommonPatternMatcher();
        }
        return instance;
    }

    @Contract("_ -> new")
    private static @NotNull Matcher compile(@NotNull String pattern) {
        return Pattern.compile(pattern).matcher("");
    }

    public boolean shaMatches(@NotNull String input) {
        return shaMatcher.get().reset(input).matches();
    }

    public boolean refNameMatches(@NotNull String input) {
        return refNameMatcher.get().reset(input).matches();
    }

    public boolean commitMessageMatches(@NotNull String input) {
        return commitMessageMatcher.get().reset(input).matches();
    }
}
