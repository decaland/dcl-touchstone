package com.github.decaland.touchstone.utils.scm.git;

import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.Contract;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class CommonPatternMatcher {

    @RegExp
    public static final String PATTERN_SHA = "^[0-9a-fA-F]{40}$";
    private Matcher shaMatcher;

    @RegExp
    public static final String PATTERN_REF_NAME = "^[\\w.-]+(/[\\w.-]+)+$";
    private Matcher refNameMatcher;

    @RegExp
    public static final String PATTERN_COMMIT_MESSAGE = "^[^\\s\"][^\"]*[^\\s\"]$";
    private Matcher commitMessageMatcher;

    private static CommonPatternMatcher instance;

    @Contract(pure = true)
    private CommonPatternMatcher() {
    }

    static CommonPatternMatcher getCommonPatternMatcher() {
        if (instance == null) {
            instance = new CommonPatternMatcher();
        }
        return instance;
    }

    private Matcher getShaMatcher() {
        if (shaMatcher == null) {
            shaMatcher = Pattern.compile(PATTERN_SHA).matcher("");
        }
        return shaMatcher;
    }

    private Matcher getRefNameMatcher() {
        if (refNameMatcher == null) {
            refNameMatcher = Pattern.compile(PATTERN_REF_NAME).matcher("");
        }
        return refNameMatcher;
    }

    private Matcher getCommitMessageMatcher() {
        if (commitMessageMatcher == null) {
            commitMessageMatcher = Pattern.compile(PATTERN_COMMIT_MESSAGE).matcher("");
        }
        return commitMessageMatcher;
    }

    public boolean shaMatches(String input) {
        return getShaMatcher().reset(input).matches();
    }

    public boolean refNameMatches(String input) {
        return getRefNameMatcher().reset(input).matches();
    }

    public boolean commitMessageMatches(String input) {
        return getCommitMessageMatcher().reset(input).matches();
    }
}
