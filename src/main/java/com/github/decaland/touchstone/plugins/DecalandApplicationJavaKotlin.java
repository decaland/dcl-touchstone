package com.github.decaland.touchstone.plugins;

import com.github.decaland.touchstone.loadouts.ApplicationJavaKotlinLoadout;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

public class DecalandApplicationJavaKotlin implements Plugin<Project> {

    @Override
    public void apply(@NotNull Project project) {
        new ApplicationJavaKotlinLoadout(project).apply();
    }
}
