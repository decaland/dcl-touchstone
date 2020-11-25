package com.github.decaland.touchstone.loadout.layers.flavors;

import com.github.decaland.touchstone.loadout.layers.FinalizedLayers;
import com.github.decaland.touchstone.loadout.layers.ProjectAwareLayer;
import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.testing.Test;
import org.jetbrains.annotations.NotNull;

import static com.github.decaland.touchstone.configs.BuildParametersManifest.SOURCE_ENCODING;
import static com.github.decaland.touchstone.configs.BuildParametersManifest.VERSION_JAVA;

public class JavaLayer extends ProjectAwareLayer {

    public JavaLayer(Project project, FinalizedLayers layers) {
        super(project, layers);
    }

    @Override
    public void applyLayer() {
        pluginManager.apply(JavaPlugin.class);
    }

    @Override
    public void configureLayer() {
        configureJavaPluginExtension();
        project.getTasks().withType(JavaCompile.class, this::configureJavaPluginCompileTasks);
        project.getTasks().withType(Test.class, this::configureJavaPluginTestCompileTasks);
    }

    private void configureJavaPluginExtension() {
        JavaPluginExtension javaPluginExtension = requireExtension(JavaPluginExtension.class);
        JavaVersion javaVersion = JavaVersion.toVersion(VERSION_JAVA);
        javaPluginExtension.setSourceCompatibility(javaVersion);
        javaPluginExtension.setTargetCompatibility(javaVersion);
    }

    private void configureJavaPluginCompileTasks(@NotNull JavaCompile task) {
        task.getOptions().setEncoding(SOURCE_ENCODING);
    }

    private void configureJavaPluginTestCompileTasks(@NotNull Test task) {
        task.getSystemProperties().put("file.encoding", SOURCE_ENCODING);
    }
}
