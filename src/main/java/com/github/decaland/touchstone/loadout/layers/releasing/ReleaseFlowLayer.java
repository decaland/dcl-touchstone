package com.github.decaland.touchstone.loadout.layers.releasing;

import com.github.decaland.touchstone.loadout.Loadout;
import com.github.decaland.touchstone.loadout.layers.ProjectAwareLayer;
import com.github.decaland.touchstone.loadout.layers.releasing.services.ReleaseConductor;
import com.github.decaland.touchstone.utils.lazy.Lazy;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.initialization.IncludedBuild;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.publish.plugins.PublishingPlugin;
import org.gradle.api.tasks.TaskProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.stream.Collectors;

import static com.github.decaland.touchstone.configs.BuildParametersManifest.PROP_KEY_SERPNET;
import static org.gradle.api.Project.PATH_SEPARATOR;
import static org.gradle.api.publish.plugins.PublishingPlugin.PUBLISH_LIFECYCLE_TASK_NAME;
import static org.gradle.language.base.plugins.LifecycleBasePlugin.*;

public class ReleaseFlowLayer extends ProjectAwareLayer {

    public static final String TASK_NAME_PLAN_RELEASE = "planRelease";
    public static final String TASK_NAME_CREATE_RELEASE = "createRelease";
    public static final String TASK_NAME_FINALIZE_RELEASE = "finalizeRelease";

    public static final String TASK_NAME_RELEASE = "release";
    public static final String GROUP_NAME_RELEASE = "Release";

    @Override
    public boolean isReady(Project project, Loadout.Layers layers) {
        return project.getPlugins().hasPlugin(BasePlugin.class)
                && project.getPlugins().hasPlugin(PublishingPlugin.class);
    }

    private Project project;
    private Lazy<ReleaseConductor> releaseConductor;

    @Override
    public void apply(Project project, Loadout.Layers layers) {
        // Store reference to the project
        this.project = project;
        this.releaseConductor = ReleaseConductor.lazyFor(project);

        // Check for included builds: this is incompatible with GradleBuild task types
        Collection<IncludedBuild> includedBuilds = project.getGradle().getIncludedBuilds();
        if (!includedBuilds.isEmpty()) {
            throw new GradleException(String.format(
                    "Release flow is incompatible with Gradle composite builds;" +
                            " the following included builds must be removed: '%s'",
                    includedBuilds.stream()
                            .map(IncludedBuild::getName)
                            .collect(Collectors.joining("', '"))
            ));
        }

        // Register tasks
        TaskProvider<Task> releaseTask =
                registerReleaseTask(TASK_NAME_RELEASE, "Lifecycle task");
        TaskProvider<Task> planReleaseTask =
                registerReleaseTask(TASK_NAME_PLAN_RELEASE, "Establishes release plan");
        TaskProvider<Task> createReleaseTask =
                registerReleaseTask(TASK_NAME_CREATE_RELEASE, "Creates release commit");
        TaskProvider<Task> finalizeReleaseTask =
                registerReleaseTask(TASK_NAME_FINALIZE_RELEASE, "Cleans up and creates next iteration");

        // Set up dependencies and ordering
        releaseTask.configure(it -> {
            it.dependsOn(
                    planReleaseTask,
                    createReleaseTask,
                    finalizeReleaseTask,
                    project.getTasks().named(ASSEMBLE_TASK_NAME),
                    project.getTasks().named(CHECK_TASK_NAME),
                    project.getTasks().named(BUILD_TASK_NAME),
                    project.getTasks().named(PUBLISH_LIFECYCLE_TASK_NAME)
            );
        });
        createReleaseTask.configure(it -> it.dependsOn(planReleaseTask));
        finalizeReleaseTask.configure(it -> it.dependsOn(planReleaseTask, createReleaseTask));
        project.getTasks().named(ASSEMBLE_TASK_NAME, it -> it.mustRunAfter(createReleaseTask));
        project.getTasks().named(CHECK_TASK_NAME, it -> it.mustRunAfter(planReleaseTask));
        project.getTasks().named(BUILD_TASK_NAME, it -> it.mustRunAfter(createReleaseTask));

        // Make sure that when releasing, publishing goes into Serpnet Artifactory
        String releaseTaskPath = String.format("%s%s", PATH_SEPARATOR, TASK_NAME_RELEASE);
        project.getGradle().getTaskGraph().whenReady(taskGraph -> {
            if (taskGraph.hasTask(releaseTaskPath) && !project.hasProperty(PROP_KEY_SERPNET)) {
                project.getExtensions().getExtraProperties().set(PROP_KEY_SERPNET, "");
            }
        });

        releaseTask.configure(task -> {
            task.getProject().getGradle().getStartParameter().newInstance();
        });

        // Assign actions to tasks
        planReleaseTask.configure(it -> it.doLast(this::doPlanRelease));
        createReleaseTask.configure(it -> it.doLast(this::doCreateRelease));
        finalizeReleaseTask.configure(it -> it.doLast(this::doFinalizeRelease));
    }

    private @NotNull TaskProvider<Task> registerReleaseTask(
            @NotNull String taskName,
            @NotNull String taskDescription) {
        return project.getTasks()
                .register(taskName, it -> {
                    it.setGroup(GROUP_NAME_RELEASE);
                    it.setDescription(taskDescription);
                });
    }

    private void doPlanRelease(Task prepareReleaseTask) {
        releaseConductor.get().planRelease();
    }

    private void doCreateRelease(Task createReleaseTask) {
        releaseConductor.get().createRelease();
    }

    private void doFinalizeRelease(Task finalizeReleaseTask) {
        releaseConductor.get().finalizeRelease();
    }
}
