package com.github.decaland.touchstone.loadouts;

import org.gradle.api.Project;
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions;
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper;
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile;

import java.util.Collections;

import static com.github.decaland.touchstone.configs.BuildParametersManifest.VERSION_JAVA;
import static com.github.decaland.touchstone.configs.BuildParametersManifest.VERSION_KOTLIN_API;

public class ApplicationJavaKotlinLoadout extends SharedFeaturesLoadout {

    public ApplicationJavaKotlinLoadout(Project project) {
        super(project);
    }

    @Override
    public void apply() {
        applyKotlinPlugin();
        configureJavaPlugin();
        configureKotlinPlugin();
    }

    private void applyKotlinPlugin() {
        pluginManager.apply(KotlinPluginWrapper.class);
    }

    private void configureKotlinPlugin() {
        project.getTasks().withType(KotlinCompile.class, this::configureKotlinPluginCompileTasks);
    }

    private void configureKotlinPluginCompileTasks(KotlinCompile task) {
        KotlinJvmOptions kotlinOptions = task.getKotlinOptions();
        kotlinOptions.setApiVersion(VERSION_KOTLIN_API);
        kotlinOptions.setLanguageVersion(VERSION_KOTLIN_API);
        kotlinOptions.setJvmTarget(VERSION_JAVA);
        kotlinOptions.setFreeCompilerArgs(Collections.singletonList("-Xjsr305=strict"));
    }
}
