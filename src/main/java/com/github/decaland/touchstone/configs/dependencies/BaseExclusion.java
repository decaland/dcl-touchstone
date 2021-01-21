package com.github.decaland.touchstone.configs.dependencies;

class BaseExclusion implements BomEntry.Artifact.Exclusion {

    String group;
    String name;

    BaseExclusion(String group, String name) {
        this.group = group;
        this.name = name;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public String getName() {
        return name;
    }
}
