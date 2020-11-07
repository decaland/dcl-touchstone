package com.github.decaland.touchstone.plugins;

import com.github.decaland.touchstone.loadouts.dependencies.EssentialDependenciesLoadout;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

public class DecalandEssentialDependencies implements Plugin<Project> {

    @Override
    public void apply(@NotNull Project project) {
        new EssentialDependenciesLoadout(project).putOn();
    }
}
