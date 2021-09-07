package com.github.decaland.touchstone.utils.scm.git;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class GitBranch extends GitRef {

    private static final Map<String, GitBranch> managedInstances = new HashMap<>();

    private GitBranch(@NotNull String name) {
        super(Type.BRANCH, name);
    }

    public static GitBranch named(@NotNull String name) {
        return managedInstances.computeIfAbsent(name, GitBranch::new);
    }
}
