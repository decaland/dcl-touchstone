package com.github.decaland.touchstone.loadout.layers.flavors;

import com.github.decaland.touchstone.loadout.Loadout;
import com.github.decaland.touchstone.loadout.layers.ProjectAwareLayer;
import io.freefair.gradle.plugins.lombok.LombokPlugin;
import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.testing.Test;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static com.github.decaland.touchstone.configs.BuildParametersManifest.*;

public class JavaLayer extends ProjectAwareLayer {

    public JavaLayer() {
    }

    @Override
    public void apply(Project project, Loadout.Layers layers) {
        project.getPluginManager().apply(JavaPlugin.class);
    }

    @Override
    public void configure(Project project, Loadout.Layers layers) {
        configureJavaPluginExtension(project);
        project.getTasks().withType(JavaCompile.class, this::configureJavaPluginCompileTasks);
        project.getTasks().withType(Test.class, this::configureJavaPluginTestCompileTasks);
        project.getTasks().withType(Jar.class, task -> configureJavaPluginJarTasks(project, task));
        createTaskForDownloadingDependencies(project);
        addDependencies(project);
    }

    private void configureJavaPluginExtension(@NotNull Project project) {
        JavaPluginExtension javaPluginExtension = requireExtension(project, JavaPluginExtension.class);
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

    private void configureJavaPluginJarTasks(@NotNull Project project, @NotNull Jar task) {
        task.getArchiveFileName().set(
                project.provider(
                        () -> String.format("%s.jar", project.getName())
                )
        );
    }

    private void createTaskForDownloadingDependencies(@NotNull Project project) {
        project.getTasks().register(TASK_DOWNLOAD_DEPENDENCIES, Copy.class, copyTask -> {
            project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets().forEach(sourceSet -> {
                copyTask.from(sourceSet.getRuntimeClasspath());
            });
            String tempDir = String.format("/tmp/%s", UUID.randomUUID());
            copyTask.into(tempDir);
            copyTask.doLast(ignored -> project.delete(tempDir));
        });
    }

    private void addDependencies(@NotNull Project project) {
        project.getPluginManager().apply(LombokPlugin.class);
        requireTask(project, "generateLombokConfig").setEnabled(false);
    }
}
