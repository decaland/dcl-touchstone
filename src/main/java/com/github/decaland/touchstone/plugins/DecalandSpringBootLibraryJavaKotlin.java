package com.github.decaland.touchstone.plugins;

import com.github.decaland.touchstone.loadouts.libraries.boot.SpringBootLibraryJavaKotlinLoadout;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

public class DecalandSpringBootLibraryJavaKotlin implements Plugin<Project> {

    @Override
    public void apply(@NotNull Project project) {
        new SpringBootLibraryJavaKotlinLoadout(project).putOn();
    }
}
