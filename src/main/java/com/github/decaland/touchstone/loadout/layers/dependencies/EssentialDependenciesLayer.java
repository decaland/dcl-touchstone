package com.github.decaland.touchstone.loadout.layers.dependencies;

import com.github.decaland.touchstone.loadout.Loadout;
import com.github.decaland.touchstone.loadout.layers.ProjectAwareLayer;
import io.freefair.gradle.plugins.lombok.LombokPlugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.testing.Test;
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper;
import org.springframework.boot.gradle.plugin.SpringBootPlugin;

import java.util.Map;

public class EssentialDependenciesLayer extends ProjectAwareLayer {

    public EssentialDependenciesLayer() {
    }

    @Override
    public void configure(Project project, Loadout.Layers layers) {
        project.getPlugins().withType(JavaPlugin.class, plugin -> this.addDependenciesForJava(project));
        project.getPlugins().withType(KotlinPluginWrapper.class, plugin -> this.addDependenciesForKotlin(project));
        project.getPlugins().withType(SpringBootPlugin.class, plugin -> this.addDependenciesForSpring(project));
        project.afterEvaluate(this::addFinalDependencies);
    }

    private void addDependenciesForJava(Project project) {
        project.getPluginManager().apply(LombokPlugin.class);
        requireTask(project, "generateLombokConfig").setEnabled(false);
    }

    private void addDependenciesForKotlin(Project project) {
        project.getDependencies().add("implementation", "org.jetbrains.kotlin:kotlin-stdlib-jdk8");
        project.getDependencies().add("implementation", "org.jetbrains.kotlin:kotlin-reflect");
    }

    private void addDependenciesForSpring(Project project) {
    }

    private void addFinalDependencies(Project project) {
        if (project.getPlugins().hasPlugin(KotlinPluginWrapper.class)) {
            project.getDependencies().add("implementation", "com.fasterxml.jackson.module:jackson-module-kotlin");
        }
        addJUnitDependency(project);
    }

    private void addJUnitDependency(Project project) {
        if (project.getPlugins().hasPlugin(SpringBootPlugin.class)) {
            addJUnitDependencyForSpring(project);
        } else {
            addJUnitDependencyForJava(project);
        }
        if (project.getPlugins().hasPlugin(KotlinPluginWrapper.class)) {
            addJUnitDependencyForKotlin(project);
        }
    }

    private void addJUnitDependencyForSpring(Project project) {
        ModuleDependency springBootStarterTest
                = (ModuleDependency) project.getDependencies()
                .create("org.springframework.boot:spring-boot-starter-test");
        springBootStarterTest.exclude(
                Map.of("group", "org.junit.vintage", "module", "junit-vintage-engine")
        );
        project.getDependencies().add("testImplementation", springBootStarterTest);

        project.getTasks().withType(Test.class, testTask -> testTask.useJUnitPlatform(jUnitPlatformOptions -> {
            jUnitPlatformOptions.includeEngines("junit-jupiter");
            jUnitPlatformOptions.excludeEngines("junit-vintage");
        }));
    }

    private void addJUnitDependencyForKotlin(Project project) {
        project.getDependencies().add("testImplementation", "org.jetbrains.kotlin:kotlin-test-junit");
    }

    private void addJUnitDependencyForJava(Project project) {
        project.getDependencies().add("testImplementation", "org.junit.jupiter:junit-jupiter");
    }
}
