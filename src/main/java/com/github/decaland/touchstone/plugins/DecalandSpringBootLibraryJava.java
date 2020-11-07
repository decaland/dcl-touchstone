package com.github.decaland.touchstone.plugins;

import com.github.decaland.touchstone.loadouts.libraries.boot.SpringBootLibraryJavaLoadout;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

public class DecalandSpringBootLibraryJava implements Plugin<Project> {

    @Override
    public void apply(@NotNull Project project) {
        new SpringBootLibraryJavaLoadout(project).putOn();
    }
}
