package com.github.decaland.touchstone.loadout.layers.flavors;

import com.github.decaland.touchstone.loadout.Loadout;
import com.github.decaland.touchstone.loadout.layers.ProjectAwareLayer;
import io.freefair.gradle.plugins.lombok.LombokPlugin;
import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.testing.Test;
import org.gradle.jvm.tasks.Jar;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.decaland.touchstone.configs.BuildParametersManifest.*;

public class JavaLayer extends ProjectAwareLayer {

    private static final String LOMBOK_CONFIG_TASK_NAME = "generateLombokConfig";

    public JavaLayer() {
    }

    @Override
    public void apply(Project project, Loadout.Layers layers) {
        project.getPluginManager().apply(ApplicationPlugin.class);
    }

    @Override
    public void configure(Project project, Loadout.Layers layers) {
        configureExtension(requireExtension(project, JavaPluginExtension.class));
        configureTasks(project.getTasks());
        addDependencies(project);
    }

    private void configureExtension(@NotNull JavaPluginExtension extension) {
        JavaVersion javaVersion = JavaVersion.toVersion(VERSION_JAVA);
        extension.setSourceCompatibility(javaVersion);
        extension.setTargetCompatibility(javaVersion);
        extension.withSourcesJar();
        extension.withJavadocJar();
    }

    private void configureTasks(@NotNull TaskContainer tasks) {
        tasks.withType(JavaCompile.class, task -> task.getOptions().setEncoding(SOURCE_ENCODING));
        tasks.withType(Test.class, task -> task.getSystemProperties().put("file.encoding", SOURCE_ENCODING));
        tasks.withType(Jar.class, this::configureJarTasks);
        createTaskForDownloadingDependencies(tasks);
    }

    private void configureJarTasks(@NotNull Jar task) {
        task.getArchiveFileName().set(
                task.getProject().provider(() -> generateJarName(task))
        );
    }

    private String generateJarName(@NotNull Jar task) {
        String generatedName = Stream.of(
                task.getArchiveBaseName(),
                task.getArchiveAppendix(),
                task.getArchiveClassifier()
        )
                .map(Provider::getOrNull)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(chunk -> !chunk.isBlank())
                .collect(Collectors.joining("-"));
        String name = generatedName.isEmpty()
                ? task.getProject().getName().toLowerCase(Locale.ROOT)
                : generatedName;
        return Optional.ofNullable(task.getArchiveExtension().getOrNull())
                .map(String::trim)
                .filter(extension -> !extension.isBlank())
                .map(extension -> String.format("%s.%s", name, extension))
                .orElse(name);
    }

    private void createTaskForDownloadingDependencies(@NotNull TaskContainer tasks) {
        tasks.register(TASK_DOWNLOAD_DEPENDENCIES, Copy.class, copyTask -> {
            requireExtension(copyTask.getProject(), JavaPluginExtension.class)
                    .getSourceSets()
                    .forEach(sourceSet -> copyTask.from(sourceSet.getRuntimeClasspath()));
            Path tempDir = Path.of(
                    Optional.ofNullable(System.getProperty("java.io.tmpdir")).orElse("/tmp"),
                    UUID.randomUUID().toString()
            );
            copyTask.into(tempDir);
            copyTask.doLast(ignored -> copyTask.getProject().delete(tempDir));
        });
    }

    private void addDependencies(@NotNull Project project) {
        project.getPluginManager().apply(LombokPlugin.class);
        if (project.getTasks().getNames().contains(LOMBOK_CONFIG_TASK_NAME)) {
            // Gradle plugin 'io.freefair.lombok' version 6 deprecates
            // 'generateLombokConfig' task, so this block may not be needed
            requireTask(project, LOMBOK_CONFIG_TASK_NAME).setEnabled(false);
        }
    }
}
