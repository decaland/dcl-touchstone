package com.github.decaland.touchstone.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

public interface DecalandPlugin extends Plugin<Project> {

    void apply(@NotNull Project target);

    @NotNull
    String getPluginId();
}
