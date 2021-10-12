package com.github.decaland.touchstone.utils.scm.git;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class GitBranchSnapshot {

    private final GitBranch branch;
    private final GitObject location;

    @Contract(pure = true)
    public GitBranchSnapshot(
            @NotNull GitBranch branch,
            @NotNull GitObject location
    ) {
        this.branch = branch;
        this.location = location;
    }

    public GitBranch getBranch() {
        return branch;
    }

    public GitObject getLocation() {
        return location;
    }
}
