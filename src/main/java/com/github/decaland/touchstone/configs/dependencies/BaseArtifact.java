package com.github.decaland.touchstone.configs.dependencies;

import java.util.LinkedList;
import java.util.List;

class BaseArtifact implements BomEntry.Artifact {

    final List<BomEntry.Artifact.Exclusion> exclusions = new LinkedList<>();
    String name;

    BaseArtifact() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Exclusion> getExclusions() {
        return List.copyOf(exclusions);
    }
}
