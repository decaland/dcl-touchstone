package com.github.decaland.touchstone.loadout.layers.releasing.models;

import com.github.decaland.touchstone.utils.scm.git.*;
import org.jetbrains.annotations.NotNull;

import static com.github.decaland.touchstone.loadout.layers.releasing.ReleaseFlowLayer.RELEASE_MESSAGE_MARKER;

public class ReleasePlan {

    private final VersionTransition versionTransition;
    private final GitBranch mainBranch;
    private final GitBranch devBranch;
    private final GitBranch releaseBranch;
    private final GitTag releaseTag;
    private final GitCommitMessage releaseCommitMsg;
    private final GitCommitMessage nextCommitMsg;
    private final GitObject mainBranchOrigin;
    private final GitObject devBranchOrigin;

    public ReleasePlan(
            @NotNull VersionTransition versionTransition,
            @NotNull GitBranchSnapshot mainBranchSnapshot,
            @NotNull GitBranchSnapshot devBranchSnapshot
    ) {
        this.versionTransition = versionTransition;
        this.mainBranch = mainBranchSnapshot.getBranch();
        this.devBranch = devBranchSnapshot.getBranch();
        this.mainBranchOrigin = mainBranchSnapshot.getLocation();
        this.devBranchOrigin = devBranchSnapshot.getLocation();
        this.releaseBranch = GitBranch.named(String.format("release/%s", versionTransition.getRelease()));
        this.releaseTag = GitTag.from(
                String.format("v%s", versionTransition.getRelease()),
                String.format("%s Version %s", RELEASE_MESSAGE_MARKER, versionTransition.getRelease())
        );
        this.releaseCommitMsg = new GitCommitMessage(
                String.format("%s Finalize version %s", RELEASE_MESSAGE_MARKER, versionTransition.getRelease())
        );
        this.nextCommitMsg = new GitCommitMessage(
                String.format("%s Introduce version %s", RELEASE_MESSAGE_MARKER, versionTransition.getNext())
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

    public GitObject getMainBranchOrigin() {
        return mainBranchOrigin;
    }

    public GitObject getDevBranchOrigin() {
        return devBranchOrigin;
    }
}
