package com.github.decaland.touchstone.loadout.layers.flavors;

import com.github.decaland.touchstone.loadout.Loadout;
import com.github.decaland.touchstone.loadout.layers.ProjectAwareLayer;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.allopen.gradle.SpringGradleSubplugin;
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions;
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper;
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile;
import org.jetbrains.kotlinx.serialization.gradle.SerializationGradleSubplugin;
import org.springframework.boot.gradle.plugin.SpringBootPlugin;

import java.util.Collections;

import static com.github.decaland.touchstone.configs.BuildParametersManifest.VERSION_JAVA;
import static com.github.decaland.touchstone.configs.BuildParametersManifest.VERSION_KOTLIN_API;

public class KotlinLayer extends ProjectAwareLayer {

    public KotlinLayer() {
    }

    @Override
    public void apply(Project project, Loadout.Layers layers) {
        project.getPluginManager().apply(KotlinPluginWrapper.class);
        project.getPlugins().withType(SpringBootPlugin.class, plugin -> {
            project.getPluginManager().apply(SpringGradleSubplugin.class);
        });
        project.getPluginManager().apply(SerializationGradleSubplugin.class);
    }

    @Override
    public void configure(Project project, Loadout.Layers layers) {
        project.getTasks().withType(KotlinCompile.class, this::configureKotlinPluginCompileTasks);
        addDependencies(project);
    }

    private void configureKotlinPluginCompileTasks(@NotNull KotlinCompile task) {
        KotlinJvmOptions kotlinOptions = task.getKotlinOptions();
        kotlinOptions.setApiVersion(VERSION_KOTLIN_API);
        kotlinOptions.setLanguageVersion(VERSION_KOTLIN_API);
        kotlinOptions.setJvmTarget(VERSION_JAVA);
        kotlinOptions.setFreeCompilerArgs(Collections.singletonList("-Xjsr305=strict"));
    }

    private void addDependencies(Project project) {
        project.getDependencies().add("implementation", "org.jetbrains.kotlin:kotlin-stdlib-jdk8");
        project.getDependencies().add("implementation", "org.jetbrains.kotlin:kotlin-reflect");
        project.getDependencies().add("implementation", "org.jetbrains.kotlinx:kotlinx-serialization-json");
        project.getDependencies().add("implementation", "org.jetbrains.kotlinx:kotlinx-coroutines-core");
    }
}
