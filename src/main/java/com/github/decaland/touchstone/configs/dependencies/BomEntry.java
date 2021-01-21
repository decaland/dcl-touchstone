package com.github.decaland.touchstone.configs.dependencies;

import io.spring.gradle.dependencymanagement.dsl.DependenciesHandler;

import java.util.List;

public interface BomEntry {

    void apply(DependenciesHandler dependenciesHandler);

    boolean isEntrySet();

    String getGroup();

    List<Artifact> getArtifacts();

    String getVersion();

    interface Artifact {

        String getName();

        List<Exclusion> getExclusions();

        interface Exclusion {

            String getGroup();

            String getName();
        }
    }
}
