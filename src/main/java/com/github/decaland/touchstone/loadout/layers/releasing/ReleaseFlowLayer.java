package com.github.decaland.touchstone.loadout.layers.releasing;

import com.github.decaland.touchstone.loadout.Loadout;
import com.github.decaland.touchstone.loadout.layers.ProjectAwareLayer;
import org.gradle.api.Project;

import java.util.Optional;

public class ReleaseFlowLayer extends ProjectAwareLayer {

    @Override
    public void apply(Project project, Loadout.Layers layers) {
        project.getTasks().register("prepareRelease", taskConfig -> {
            taskConfig.setGroup("Release");
            taskConfig.setDescription("Creates release commit");
            taskConfig.doLast(task -> {
                task.getLogger().lifecycle("[DEBUG] Preparing release");
            });
        });
        project.getTasks().register("release", taskConfig -> {
            taskConfig.setGroup("Release");
            taskConfig.setDescription("Lifecycle task");
            taskConfig.dependsOn(project.getTasks().named("prepareRelease"));
            Optional.ofNullable(project.getTasks().findByName("publish"))
                    .map(taskConfig::dependsOn);
            taskConfig.dependsOn(project.getTasks().named("build"));
            taskConfig.doLast(task -> {
                task.getProject().getLogger().lifecycle("[DEBUG] Releasing");
            });
        });
        project.getTasks().named("assemble", classesTask -> {
            classesTask.mustRunAfter(project.getTasks().named("prepareRelease"));
        });
    }
}
