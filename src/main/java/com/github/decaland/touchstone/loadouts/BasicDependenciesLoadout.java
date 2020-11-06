package com.github.decaland.touchstone.loadouts;

import io.freefair.gradle.plugins.lombok.LombokPlugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.testing.Test;
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper;
import org.springframework.boot.gradle.plugin.SpringBootPlugin;

import java.util.Map;

public class BasicDependenciesLoadout extends GradleVersionAwareLoadout {

    private final DependencyHandler dependencies;

    public BasicDependenciesLoadout(Project project) {
        super(project);
        this.dependencies = project.getDependencies();
    }

    @Override
    public void apply() {
        project.getPlugins().withType(JavaPlugin.class, plugin -> this.addDependenciesForJava());
        project.getPlugins().withType(KotlinPluginWrapper.class, plugin -> this.addDependenciesForKotlin());
        project.getPlugins().withType(SpringBootPlugin.class, plugin -> this.addDependenciesForSpring());
    }

    private void addDependenciesForJava() {
        pluginManager.apply(LombokPlugin.class);
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
