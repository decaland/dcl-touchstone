package com.github.decaland.touchstone.loadout.layers.releasing;

import com.github.decaland.touchstone.loadout.Loadout;
import com.github.decaland.touchstone.loadout.layers.ProjectAwareLayer;
import com.github.decaland.touchstone.loadout.layers.releasing.services.ReleaseConductor;
import com.github.decaland.touchstone.utils.lazy.Lazy;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.execution.TaskExecutionGraph;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.publish.plugins.PublishingPlugin;
import org.gradle.api.tasks.TaskProvider;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.decaland.touchstone.configs.BuildParametersManifest.PROP_KEY_SERPNET;
import static org.gradle.api.Project.PATH_SEPARATOR;
import static org.gradle.api.publish.plugins.PublishingPlugin.PUBLISH_LIFECYCLE_TASK_NAME;
import static org.gradle.language.base.plugins.LifecycleBasePlugin.*;

public class ReleaseFlowLayer extends ProjectAwareLayer {

    public static final String RELEASE_MESSAGE_MARKER = "[RELEASE]";

    public static final String TASK_NAME_PLAN_RELEASE = "planRelease";
    public static final String TASK_NAME_CREATE_RELEASE = "createRelease";
    public static final String TASK_NAME_CREATE_NEXT = "createNext";
    public static final String TASK_NAME_PUSH_RELEASE = "pushRelease";

    public static final String TASK_NAME_RELEASE = "release";
    public static final String GROUP_NAME_RELEASE = "Release";

    @Override
    public boolean isReady(Project project, Loadout.Layers layers) {
        return project.getPlugins().hasPlugin(BasePlugin.class)
                && project.getPlugins().hasPlugin(PublishingPlugin.class);
    }

    private Project project;
    private Project rootProject;
    private Lazy<ReleaseConductor> releaseConductor;

    @Override
    public void apply(Project project, Loadout.Layers layers) {
        // Store reference to both current project and root project of the hierarchy
        this.project = project;
        this.rootProject = project.getRootProject();
        this.releaseConductor = ReleaseConductor.lazyFor(rootProject);

        // Register tasks
        TaskProvider<Task> releaseTask =
                registerReleaseTask(TASK_NAME_RELEASE, "Lifecycle task");
        TaskProvider<Task> planReleaseTask =
                registerReleaseTask(TASK_NAME_PLAN_RELEASE, "Establishes release plan");
        TaskProvider<Task> createReleaseTask =
                registerReleaseTask(TASK_NAME_CREATE_RELEASE, "Creates, tags, and merges release commit");
        TaskProvider<Task> createNextTask =
                registerReleaseTask(TASK_NAME_CREATE_NEXT, "Creates and merges next-iteration commit");
        TaskProvider<Task> pushReleaseTask =
                registerReleaseTask(TASK_NAME_PUSH_RELEASE, "Pushes release-related commits");

        List<Task> publishTasks = Stream.of(
                        ASSEMBLE_TASK_NAME,
                        CHECK_TASK_NAME,
                        BUILD_TASK_NAME,
                        PUBLISH_LIFECYCLE_TASK_NAME
                )
                .flatMap(taskName -> rootProject.getTasksByName(taskName, true).stream())
                .collect(Collectors.toUnmodifiableList());

        // Set up dependencies and ordering
        createReleaseTask.configure(it -> it.dependsOn(planReleaseTask));
        publishTasks.forEach(it -> it.mustRunAfter(createReleaseTask));
        createNextTask.configure(it -> it.dependsOn(createReleaseTask, publishTasks));
        pushReleaseTask.configure(it -> it.dependsOn(createNextTask));
        releaseTask.configure(it -> it.dependsOn(pushReleaseTask));

        // Make sure that when releasing, publishing goes into Serpnet Artifactory
        project.getGradle().getTaskGraph().whenReady(taskGraph -> {
            if (!project.hasProperty(PROP_KEY_SERPNET) && includesReleaseTask(taskGraph)) {
                project.getExtensions().getExtraProperties().set(PROP_KEY_SERPNET, "");
            }
        });

        // Assign actions to tasks
        planReleaseTask.configure(it -> it.doLast(this::doPlanRelease));
        createReleaseTask.configure(it -> it.doLast(this::doCreateRelease));
        createNextTask.configure(it -> it.doLast(this::doCreateNext));
        pushReleaseTask.configure(it -> it.doLast(this::doPushRelease));
        project.getGradle().getTaskGraph().afterTask(this::onAnyFailureAbortRelease);
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

    private boolean includesReleaseTask(@NotNull TaskExecutionGraph taskGraph) {
        String releasePathSuffix = String.format("%s%s", PATH_SEPARATOR, TASK_NAME_RELEASE);
        return taskGraph.getAllTasks()
                .stream()
                .map(Task::getPath)
                .anyMatch(taskPath -> taskPath.endsWith(releasePathSuffix));
    }

    private void doPlanRelease(Task prepareReleaseTask) {
        releaseConductor.get().planRelease();
    }

    private void doCreateRelease(Task createReleaseTask) {
        releaseConductor.get().createRelease();
    }

    private void doCreateNext(Task createNextTask) {
        releaseConductor.get().createNext();
    }

    private void doPushRelease(Task finalizeReleaseTask) {
        releaseConductor.get().pushRelease();
    }

    private void onAnyFailureAbortRelease(@NotNull Task anyPrecedingTask) {
        if (anyPrecedingTask.getState().getFailure() != null) {
            if (releaseConductor.isInitialized()) {
                releaseConductor.get().abortRelease();
            }
        }
    }
}
