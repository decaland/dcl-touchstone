package com.github.decaland.touchstone.loadout.layers.dependencies;

import com.github.decaland.touchstone.loadout.layers.LayerAccumulator;
import com.github.decaland.touchstone.loadout.layers.ProjectAwareLayer;
import io.freefair.gradle.plugins.lombok.LombokPlugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.testing.Test;
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper;
import org.springframework.boot.gradle.plugin.SpringBootPlugin;

import java.util.Map;

public class EssentialDependenciesLayer extends ProjectAwareLayer {

    private final DependencyHandler dependencies;

    public EssentialDependenciesLayer(Project project, LayerAccumulator.Finalized layers) {
        super(project, layers);
        this.dependencies = project.getDependencies();
    }

    @Override
    public void configureLayer() {
        project.getPlugins().withType(JavaPlugin.class, plugin -> this.addDependenciesForJava());
        project.getPlugins().withType(KotlinPluginWrapper.class, plugin -> this.addDependenciesForKotlin());
        project.getPlugins().withType(SpringBootPlugin.class, plugin -> this.addDependenciesForSpring());
    }

    private void addDependenciesForJava() {
        pluginManager.apply(LombokPlugin.class);
        requireTask("generateLombokConfig").setEnabled(false);
    }

    private void addDependenciesForKotlin() {
        dependencies.add("implementation", "org.jetbrains.kotlin:kotlin-stdlib-jdk8");
        dependencies.add("implementation", "org.jetbrains.kotlin:kotlin-reflect");
        dependencies.add("testImplementation", "org.jetbrains.kotlin:kotlin-test-junit");
    }

    private void addDependenciesForSpring() {
        ModuleDependency springBootStarterTest
                = (ModuleDependency) dependencies.create("org.springframework.boot:spring-boot-starter-test");
        springBootStarterTest.exclude(
                Map.of("group", "org.junit.vintage", "module", "junit-vintage-engine")
        );
        dependencies.add("testImplementation", springBootStarterTest);

        project.getTasks().withType(Test.class, Test::useJUnitPlatform);

        if (project.getPlugins().hasPlugin(KotlinPluginWrapper.class)) {
            dependencies.add("implementation", "com.fasterxml.jackson.module:jackson-module-kotlin");
        }
    }
}
