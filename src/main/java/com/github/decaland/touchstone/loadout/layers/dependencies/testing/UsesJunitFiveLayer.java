package com.github.decaland.touchstone.loadout.layers.dependencies.testing;

import com.github.decaland.touchstone.loadout.Loadout;
import com.github.decaland.touchstone.loadout.layers.ProjectAwareLayer;
import org.gradle.api.Project;
import org.gradle.api.tasks.testing.Test;
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper;
import org.springframework.boot.gradle.plugin.SpringBootPlugin;

public class UsesJunitFiveLayer extends ProjectAwareLayer {

    private static final String JUNIT_DEP = "org.junit.jupiter:junit-jupiter";
    private static final String JUNIT_DEP_KOTLIN = "org.jetbrains.kotlin:kotlin-test-junit5";
    private static final String JUNIT_DEP_SPRING = "org.springframework.boot:spring-boot-starter-test";
    private static final String MOCKITO_DEP = "org.mockito:mockito-junit-jupiter";
    private static final String ASSERTJ_DEP = "org.assertj:assertj-core";

    public UsesJunitFiveLayer() {
    }

    @Override
    public void configure(Project project, Loadout.Layers layers) {
        project.afterEvaluate(this::addJunitFiveDependencies);
    }

    private void addJunitFiveDependencies(Project project) {
        final String config = "testImplementation";
        project.getDependencies().add(config, JUNIT_DEP);
        project.getDependencies().add(config, MOCKITO_DEP);
        project.getDependencies().add(config, ASSERTJ_DEP);
        if (project.getPlugins().hasPlugin(KotlinPluginWrapper.class)) {
            project.getDependencies().add(config, JUNIT_DEP_KOTLIN);
        }
        if (project.getPlugins().hasPlugin(SpringBootPlugin.class)) {
            project.getDependencies().add(config, JUNIT_DEP_SPRING);
        }
        project.getTasks().withType(
                Test.class,
                testTask -> testTask.useJUnitPlatform(jUnitPlatformOptions -> {
                    jUnitPlatformOptions.includeEngines("junit-jupiter");
                    jUnitPlatformOptions.excludeEngines("junit-vintage");
                })
        );
    }
}
