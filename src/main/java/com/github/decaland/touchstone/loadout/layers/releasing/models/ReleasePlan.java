package com.github.decaland.touchstone.loadout.layers.releasing.models;

import com.github.decaland.touchstone.utils.scm.git.GitBranch;
import com.github.decaland.touchstone.utils.scm.git.GitCommitMessage;
import com.github.decaland.touchstone.utils.scm.git.GitTag;
import org.jetbrains.annotations.NotNull;

public class ReleasePlan {

    private final VersionTransition versionTransition;
    private final GitBranch mainBranch;
    private final GitBranch devBranch;
    private final GitBranch releaseBranch;
    private final GitTag releaseTag;
    private final GitCommitMessage releaseCommitMsg;
    private final GitCommitMessage nextCommitMsg;

    /*
    private final VersionTransition versionTransition;
    private final String mainBranch;
    private final String devBranch;
    private final String releaseBranch;
    private final String releaseTag;
    private final String releaseTagMsg;
    private final String releaseCommitMsg;
    private final String nextCommitMsg;
    */

    public ReleasePlan(
            @NotNull VersionTransition versionTransition,
            @NotNull GitBranch mainBranch,
            @NotNull GitBranch devBranch
    ) {
        this.versionTransition = versionTransition;
        this.mainBranch = mainBranch;
        this.devBranch = devBranch;
        this.releaseBranch = GitBranch.named(String.format("release/%s", versionTransition.getRelease()));
        this.releaseTag = GitTag.from(
                String.format("v%s", versionTransition.getRelease()),
                String.format("[release] Version %s", versionTransition.getRelease())
        );
        this.releaseCommitMsg = new GitCommitMessage(
                String.format("[release] Finalize version %s", versionTransition.getRelease())
        );
        this.nextCommitMsg = new GitCommitMessage(
                String.format("[release] Introduce version %s", versionTransition.getNext())
        );
    }

    public VersionTransition getVersionTransition() {
        return versionTransition;
    }

    public GitBranch getMainBranch() {
        return mainBranch;
    }

    public GitBranch getDevBranch() {
        return devBranch;
    }

    public GitBranch getReleaseBranch() {
        return releaseBranch;
    }

    public GitTag getReleaseTag() {
        return releaseTag;
    }

    public GitCommitMessage getReleaseCommitMsg() {
        return releaseCommitMsg;
    }

    public GitCommitMessage getNextCommitMsg() {
        return nextCommitMsg;
    }
}
