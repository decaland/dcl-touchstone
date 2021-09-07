package com.github.decaland.touchstone.utils.scm.git;

import com.github.decaland.touchstone.utils.lazy.Lazy;
import com.github.decaland.touchstone.utils.shell.BashExecutor;
import com.github.decaland.touchstone.utils.shell.ShellExecutor;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GitAdapter {

    private static final Map<Project, GitAdapter> managedInstances = new HashMap<>();

    private final Lazy<? extends ShellExecutor> shell;

    private GitAdapter(@NotNull Project project) {
        shell = BashExecutor.lazyFor(project, "git");
    }

    synchronized private static @NotNull GitAdapter forProject(@NotNull Project project) {
        return managedInstances.computeIfAbsent(project, GitAdapter::new);
    }

    @Contract("_ -> new")
    public static @NotNull Lazy<GitAdapter> lazyFor(@NotNull Project project) {
        return Lazy.using(() -> GitAdapter.forProject(project));
    }

    synchronized public boolean workTreeIsDirty() {
        return shell.get().test("test -n \"$( git status --porcelain )\"");
    }

    public void requireCleanWorkTree() {
        if (workTreeIsDirty()) {
            throw new GradleException("Git work tree is dirty");
        }
    }

    synchronized public boolean exists(@NotNull GitRef ref) {
        switch (ref.getType()) {
            case BRANCH:
                return shell.get().test(String.format(
                        "test -n \"$( git branch --list -- \"%s\" )\"", ref.getName()
                ));
            case TAG:
                return shell.get().test(String.format(
                        "test -n \"$( git tag --list -- \"%s\" )\"", ref.getName()
                ));
            default:
                throw new IllegalStateException(String.format(
                        "Unexpected value '%s' of enum %s", ref.getType(), GitRef.Type.class.getSimpleName()
                ));
        }
    }

    synchronized public void checkout(@NotNull GitBranch branch) {
        if (!exists(branch)) {
            throw new GradleException(String.format(
                    "Attempted to checkout non-existent Git branch '%s' without creating it first", branch.getName()
            ));
        }
        shell.get().insist(String.format(
                "git checkout \"%s\"", branch.getName()
        ));
    }

    synchronized public void checkoutNew(@NotNull GitBranch branch) {
        if (exists(branch)) {
            throw new GradleException(String.format(
                    "Attempted to create already existing %s '%s'",
                    branch.getType().getHumanReadableType(), branch.getName()
            ));
        }
        shell.get().insist(String.format(
                "git checkout -b \"%s\"", branch.getName()
        ));
    }

    synchronized public GitObject locate(@NotNull GitRef ref) {
        if (!exists(ref)) {
            throw new GradleException(String.format(
                    "Attempted to locate non-existent %s '%s'",
                    ref.getType().getHumanReadableType(), ref.getName()
            ));
        }
        return GitObject.ofSha(
                shell.get().insist(String.format(
                        "git rev-parse --verify \"%s\"", ref.getFullName()
                )).getStdOut()
        );
    }

    synchronized public void delete(@NotNull GitRef ref) {
        switch (ref.getType()) {
            case BRANCH:
                shell.get().insist(String.format(
                        "git branch --delete \"%s\"", ref.getName()
                ));
                break;
            case TAG:
                shell.get().insist(String.format(
                        "git tag --delete \"%s\"", ref.getName()
                ));
                break;
            default:
                throw new IllegalStateException(String.format(
                        "Unexpected value '%s' of enum %s", ref.getType(), GitRef.Type.class.getSimpleName()
                ));
        }
    }

    synchronized public void reset(@NotNull GitObject location) {
        shell.get().insist(String.format(
                "git reset --hard \"%s\"", location.getSha()
        ));
    }

    synchronized public void addFiles(
            @NotNull String file,
            String... additionalFiles
    ) {
        shell.get().insist(String.format(
                "git add -- \"%s\"",
                Stream.concat(Stream.of(file), Arrays.stream(additionalFiles))
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining("\" \""))
        ));
    }

    synchronized public void commitFiles(
            @NotNull GitCommitMessage message,
            @NotNull String file,
            String... additionalFiles
    ) {
        addFiles(file, additionalFiles);
        shell.get().insist(String.format(
                "git commit --message=\"%s\"", message
        ));
    }

    public void tagCurrentCommit(@NotNull GitTag tag) {
        shell.get().insist(String.format(
                "git tag --annotate \"%s\" --message=\"%s\"",
                tag.getName(), tag.getMessage()
        ));
    }

    synchronized public void mergeIntoCurrentBranch(@NotNull GitBranch other) {
        shell.get().insist(String.format(
                "git merge --no-ff --no-edit -- \"%s\"", other.getName()
        ));
    }

    synchronized public void push(@NotNull GitRef... refs) {
        shell.get().insist(String.format(
                "git push origin \"%s\"",
                Arrays.stream(refs)
                        .map(GitRef::getFullName)
                        .collect(Collectors.joining("\" \""))
        ));
    }
}
