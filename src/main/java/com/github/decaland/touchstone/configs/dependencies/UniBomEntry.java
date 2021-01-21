package com.github.decaland.touchstone.configs.dependencies;

import io.spring.gradle.dependencymanagement.dsl.DependenciesHandler;
import io.spring.gradle.dependencymanagement.dsl.DependencyHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

class UniBomEntry implements BomEntry {

    private String group;
    private Artifact artifact;
    private String version;

    private UniBomEntry() {
    }

    @NotNull
    static Builder group(String group) {
        return Builder.fromGroup(group);
    }

    @Override
    public void apply(@NotNull DependenciesHandler dependenciesHandler) {
        dependenciesHandler.dependency(
                String.format("%s:%s:%s", group, artifact.getName(), version),
                this::applyExclusions
        );
    }

    private void applyExclusions(DependencyHandler dependencyHandler) {
        for (Artifact.Exclusion exclusion : artifact.getExclusions()) {
            dependencyHandler.exclude(
                    String.format("%s:%s", exclusion.getGroup(), exclusion.getName())
            );
        }
    }

    @Override
    public boolean isEntrySet() {
        return false;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public List<Artifact> getArtifacts() {
        return Collections.singletonList(artifact);
    }

    @Override
    public String getVersion() {
        return version;
    }

    static class Builder {

        private final BaseArtifact artifact = new BaseArtifact();
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
        private static Builder fromName(String name) {
            Builder builder = new Builder();
            builder.artifact.name = name;
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
            this.artifact.name = name;
            return this;
        }

        Builder except(String group, String name) {
            this.artifact.exclusions.add(new BaseExclusion(group, name));
            return this;
        }

        Builder version(String version) {
            this.version = version;
            return this;
        }

        void add() {
            UniBomEntry entry = new UniBomEntry();
            entry.group = group;
            entry.artifact = artifact;
            entry.version = version;
            DependencyBom.entries.add(entry);
        }
    }
}
