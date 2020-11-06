package com.github.decaland.touchstone.plugins;

import com.github.decaland.touchstone.loadouts.SpringApplicationJavaKotlinLoadout;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

public class DecalandSpringApplicationJavaKotlin implements Plugin<Project> {

    @Override
    public void apply(@NotNull Project project) {
        new SpringApplicationJavaKotlinLoadout(project).apply();
    }
}
