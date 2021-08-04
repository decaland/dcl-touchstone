package com.github.decaland.touchstone.loadout.layers.releasing;

import com.github.decaland.touchstone.loadout.Loadout;
import com.github.decaland.touchstone.loadout.layers.ProjectAwareLayer;
import org.gradle.api.Project;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.publish.plugins.PublishingPlugin;

import static org.gradle.api.publish.plugins.PublishingPlugin.PUBLISH_LIFECYCLE_TASK_NAME;
import static org.gradle.language.base.plugins.LifecycleBasePlugin.ASSEMBLE_TASK_NAME;
import static org.gradle.language.base.plugins.LifecycleBasePlugin.BUILD_TASK_NAME;

public class ReleaseFlowLayer extends ProjectAwareLayer {

    public static final String PREPARE_RELEASE_TASK_NAME = "prepareRelease";
    public static final String RELEASE_LIFECYCLE_TASK_NAME = "release";
    public static final String RELEASE_GROUP_NAME = "Release";

    @Override
    public boolean isReady(Project project, Loadout.Layers layers) {
        PluginContainer plugins = project.getPlugins();
        return plugins.hasPlugin(BasePlugin.class)
                && plugins.hasPlugin(PublishingPlugin.class);
    }

    @Override
    public void apply(Project project, Loadout.Layers layers) {
        project.getTasks().register(PREPARE_RELEASE_TASK_NAME, taskConfig -> {
            taskConfig.setGroup(RELEASE_GROUP_NAME);
            taskConfig.setDescription("Creates release commit");
            taskConfig.doLast(task -> {
                task.getLogger().lifecycle("[DEBUG] Preparing release");
            });
        });
        project.getTasks().register(RELEASE_LIFECYCLE_TASK_NAME, taskConfig -> {
            taskConfig.setGroup(RELEASE_GROUP_NAME);
            taskConfig.setDescription("Lifecycle task");
            taskConfig.dependsOn(project.getTasks().named(PREPARE_RELEASE_TASK_NAME));
            taskConfig.dependsOn(project.getTasks().named(PUBLISH_LIFECYCLE_TASK_NAME));
            taskConfig.dependsOn(project.getTasks().named(BUILD_TASK_NAME));
            taskConfig.doLast(task -> {
                task.getProject().getLogger().lifecycle("[DEBUG] Releasing");
            });
        });
        project.getTasks().named(ASSEMBLE_TASK_NAME, classesTask -> {
            classesTask.mustRunAfter(project.getTasks().named(PREPARE_RELEASE_TASK_NAME));
        });
    }
}
