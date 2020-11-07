package com.github.decaland.touchstone.plugins;

import com.github.decaland.touchstone.loadouts.applications.SpringBootApplicationJavaKotlinLoadout;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

public class DecalandSpringBootApplicationJavaKotlin implements Plugin<Project> {

    @Override
    public void apply(@NotNull Project project) {
        new SpringBootApplicationJavaKotlinLoadout(project).putOn();
    }
}
