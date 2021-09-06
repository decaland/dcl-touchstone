package com.github.decaland.touchstone.loadout.layers.flavors;

import com.github.decaland.touchstone.loadout.Loadout;
import com.github.decaland.touchstone.loadout.layers.ProjectAwareLayer;
import io.spring.gradle.dependencymanagement.DependencyManagementPlugin;
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.gradle.plugin.SpringBootPlugin;

import static com.github.decaland.touchstone.configs.BuildParametersManifest.VERSION_SPRING_CLOUD;
import static org.gradle.api.plugins.JavaPlugin.JAR_TASK_NAME;
import static org.springframework.boot.gradle.plugin.SpringBootPlugin.BOOT_JAR_TASK_NAME;

public class SpringBootLayer extends ProjectAwareLayer {

    private boolean isApplication = false;

    public SpringBootLayer() {
    }

    public SpringBootLayer(boolean isApplication) {
        this.isApplication = isApplication;
    }

    @Override
    public void apply(Project project, Loadout.Layers layers) {
        if (!project.getPlugins().hasPlugin(DependencyManagementPlugin.class)) {
            project.getPluginManager().apply(DependencyManagementPlugin.class);
        }
        project.getPluginManager().apply(SpringBootPlugin.class);
    }

    @Override
    public void configure(Project project, Loadout.Layers layers) {
        if (isLibrary()) {
            configureSpringBootLibrary(project);
        }
        addSpringCloudVersion(project);
        addDependencyManagement(project);
        addDependencies(project);
    }

    private void configureSpringBootLibrary(Project project) {
        requireTask(project, BOOT_JAR_TASK_NAME).setEnabled(false);
        requireTask(project, JAR_TASK_NAME).setEnabled(true);
    }

    public boolean isApplication() {
        return isApplication;
    }

    public boolean isLibrary() {
        return !isApplication;
    }

    private void addSpringCloudVersion(@NotNull Project project) {
        project.getExtensions()
                .getExtraProperties()
                .set("springCloudVersion", VERSION_SPRING_CLOUD);
    }

    private void addDependencyManagement(Project project) {
        project.getExtensions().configure(
                DependencyManagementExtension.class,
                dependencyManagement -> dependencyManagement.imports(
                        importsHandler -> importsHandler.mavenBom(String.format(
                                "org.springframework.cloud:spring-cloud-dependencies:%s",
                                VERSION_SPRING_CLOUD
                        ))
                )
        );
    }

    private void addDependencies(@NotNull Project project) {
        project.getDependencies().add("implementation", "org.springframework.boot:spring-boot-starter");
    }
}
