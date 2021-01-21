package com.github.decaland.touchstone.configs.dependencies;

import io.spring.gradle.dependencymanagement.dsl.DependenciesHandler;
import io.spring.gradle.dependencymanagement.dsl.DependencyHandler;
import io.spring.gradle.dependencymanagement.dsl.DependencySetHandler;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

class MultiBomEntry implements BomEntry {

    private String group;
    private List<Artifact> artifacts;
    private String version;

    private MultiBomEntry() {
    }

    @NotNull
    static Builder library(String group) {
        return Builder.fromGroup(group);
    }

    @Override
    public void apply(@NotNull DependenciesHandler dependenciesHandler) {
        dependenciesHandler.dependencySet(
                String.format("%s:%s", group, version),
                this::applySetEntries
        );
    }

    private void applySetEntries(DependencySetHandler dependencySetHandler) {
        for (Artifact artifact : artifacts) {
            dependencySetHandler.entry(
                    artifact.getName(),
                    dependencyHandler -> applyExclusions(dependencyHandler, artifact.getExclusions())
            );
        }
    }

    private void applyExclusions(DependencyHandler dependencyHandler, List<Artifact.Exclusion> exclusions) {
        for (Artifact.Exclusion exclusion : exclusions) {
            dependencyHandler.exclude(
                    String.format("%s:%s", exclusion.getGroup(), exclusion.getName())
            );
        }
    }

    @Override
    public boolean isEntrySet() {
        return true;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public List<Artifact> getArtifacts() {
        return List.copyOf(artifacts);
    }

    @Override
    public String getVersion() {
        return version;
    }

    static class Builder {

        private final LinkedList<BaseArtifact> artifacts = new LinkedList<>();
        private String group;
        private String version;

        private Builder() {
        }

        @NotNull
        private static Builder fromGroup(String group) {
            Builder builder = new Builder();
            builder.group = group;
            return builder;
        }

        @NotNull
        private static Builder fromVersion(String version) {
            Builder builder = new Builder();
            builder.version = version;
            return builder;
        }

        Builder group(String group) {
            this.group = group;
            return this;
        }

        Builder name(String name) {
            BaseArtifact baseArtifact = new BaseArtifact();
            baseArtifact.name = name;
            artifacts.addLast(baseArtifact);
            return this;
        }

        Builder except(String group, String name) {
            BaseArtifact baseArtifact = artifacts.getLast();
            if (baseArtifact != null) {
                baseArtifact.exclusions.add(new BaseExclusion(group, name));
            }
            return this;
        }

        Builder version(String version) {
            this.version = version;
            return this;
        }

        void add() {
            MultiBomEntry entry = new MultiBomEntry();
            entry.group = group;
            entry.artifacts = List.copyOf(artifacts);
            entry.version = version;
            DependencyBom.entries.add(entry);
        }
    }
}
