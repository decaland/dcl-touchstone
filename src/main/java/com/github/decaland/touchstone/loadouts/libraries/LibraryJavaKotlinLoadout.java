package com.github.decaland.touchstone.loadouts.libraries;

import com.github.decaland.touchstone.loadouts.SharedFeaturesLoadout;
import org.gradle.api.Project;
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions;
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper;
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile;

import java.util.Collections;

import static com.github.decaland.touchstone.configs.BuildParametersManifest.VERSION_JAVA;
import static com.github.decaland.touchstone.configs.BuildParametersManifest.VERSION_KOTLIN_API;

public class LibraryJavaKotlinLoadout extends SharedFeaturesLoadout {

    public LibraryJavaKotlinLoadout(Project project) {
        super(project);
    }

    @Override
    public void putOn() {
        applyKotlinPlugin();
        configureJavaPlugin();
        configureKotlinPlugin();
        configureMavenPublishPluginExtensionPublications();
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
