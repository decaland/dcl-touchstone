package com.github.decaland.touchstone.plugins;

import com.github.decaland.touchstone.loadouts.SpringApplicationJavaLoadout;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

public class DecalandSpringApplicationJava implements Plugin<Project> {

    @Override
    public void apply(@NotNull Project project) {
        new SpringApplicationJavaLoadout(project).apply();
    }
}
