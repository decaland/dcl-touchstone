package com.github.decaland.touchstone.loadout.layers.flavors;

import com.github.decaland.touchstone.loadout.layers.FinalizedLayers;
import com.github.decaland.touchstone.loadout.layers.ProjectAwareLayer;
import io.spring.gradle.dependencymanagement.DependencyManagementPlugin;
import org.gradle.api.Project;
import org.springframework.boot.gradle.plugin.SpringBootPlugin;

import static org.gradle.api.plugins.JavaPlugin.JAR_TASK_NAME;
import static org.springframework.boot.gradle.plugin.SpringBootPlugin.BOOT_JAR_TASK_NAME;

public class SpringBootLayer extends ProjectAwareLayer {

    private boolean isApplication = false;

    public SpringBootLayer(Project project, FinalizedLayers layers) {
        super(project, layers);
    }

    @Override
    public void applyLayer() {
        if (!pluginContainer.hasPlugin(DependencyManagementPlugin.class)) {
            pluginManager.apply(DependencyManagementPlugin.class);
        }
        pluginManager.apply(SpringBootPlugin.class);
    }

    @Override
    public void configureLayer() {
        if (!isApplication()) {
            configureSpringBootLibrary();
        }
    }

    protected void configureSpringBootLibrary() {
        requireTask(BOOT_JAR_TASK_NAME).setEnabled(false);
        requireTask(JAR_TASK_NAME).setEnabled(true);
    }

    public boolean isApplication() {
        return isApplication;
    }

    public void markAsApplication() {
        this.isApplication = true;
    }
}
