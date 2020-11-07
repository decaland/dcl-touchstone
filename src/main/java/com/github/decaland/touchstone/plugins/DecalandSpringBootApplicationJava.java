package com.github.decaland.touchstone.plugins;

import com.github.decaland.touchstone.loadouts.applications.SpringBootApplicationJavaLoadout;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

public class DecalandSpringBootApplicationJava implements Plugin<Project> {

    @Override
    public void apply(@NotNull Project project) {
        new SpringBootApplicationJavaLoadout(project).putOn();
    }
}
