package com.github.decaland.touchstone.plugins;

import com.github.decaland.touchstone.loadouts.BasicDependenciesLoadout;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

public class DecalandBasicDependencies implements Plugin<Project> {

    @Override
    public void apply(@NotNull Project project) {
        new BasicDependenciesLoadout(project).apply();
    }
}
