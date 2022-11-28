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

/**
 * Introduces tasks for creating a set of Git commits that represent a released
 * version of the application.
 * <p/>
 * The {@link ReleaseFlowLayer} makes the following assumptions:
 * <ul>
 *     <li>The project is version controlled using Git and adheres to the
 *     Git-flow pattern, specifically:
 *     <ul>
 *         <li>Each commit to the {@code main} branch is a released version of
 *         the application.</li>
 *         <li>Each commit to the {@code dev} branch is a potentially
 *         releasable version of the application.</li>
 *         <li>Commits to the {@code main} branch are stamped with their
 *         version using annotated Git tags that follow the pattern
 *         {@code vX.Y.Z}, where {@code X.Y.Z} is a <i>semver</i>-compliant
 *         version.</li>
 *         <li>Commits to the {@code dev} branch are similarly stamped with
 *         their potentially releasable version, suffixed by
 *         <nobr>{@code -SNAPSHOT}</nobr>.
 *         </li>
 *     </ul>
 *     </li>
 *     <li>The root Gradle {@link Project} is the one being released as a
 *     whole.</li>
 *     <li>The root Gradle {@link Project} is also the root of the Git
 *     repository, i.e., is the immediate parent of the {@code .git} directory.
 *     </li>
 *     <li>The {@code gradle.properties} file in the root of the Git repository
 *     is the one and only place that contains the project's current version in
 *     the form of a Gradle property
 *     <nobr>{@code version=X.Y.Z[-SNAPSHOT]}</nobr>.</li>
 * </ul>
 * The {@link ReleaseFlowLayer} imposes the following requirements:
 * <ul>
 *     <li>The Git work tree is clean before the release flow is started.</li>
 * </ul>
 * The {@link ReleaseFlowLayer} configures a lifecycle task named
 * {@code release}, which is set to depend on the following internal Gradle
 * tasks, which are not intended to be invoked directly:
 * <ul>
 *     <li>Task {@code planRelease}:
 *     <ul>
 *         <li>analyzes the current state of the project;</li>
 *         <li>finalizes the list of steps that will be taken to create the
 *         release;</li>
 *         <li>prints the summary to the user.</li>
 *     </ul>
 *     </li>
 *     <li>Task {@code createRelease}:
 *     <ul>
 *         <li>switches to the {@code dev} branch;</li>
 *         <li>creates a temporary release branch;</li>
 *         <li>modifies the version in the {@code gradle.properties} file by
 *         removing the <nobr>{@code -SNAPSHOT}</nobr> suffix and commits this
 *         change;</li>
 *         <li>merges the temporary branch into the {@code main} branch;</li>
 *         <li>tags the merge commit with the version.</li>
 *     </ul>
 *     </li>
 *     <li>Task {@code createNext}:
 *     <ul>
 *         <li>switches to the temporary branch;</li>
 *         <li>changes the version in the {@code gradle.properties} file to the
 *         next patch with the <nobr>{@code -SNAPSHOT}</nobr> suffix and
 *         commits this change;</li>
 *         <li>merges the temporary branch into the {@code dev} branch;</li>
 *         <li>deletes the temporary branch.</li>
 *     </ul>
 *     </li>
 *     <li>Task {@code pushRelease}:
 *     <ul>
 *         <li>pushes the two updated branches ({@code main} and {@code dev})
 *         along with the release tag to the {@code origin} remote.</li>
 *     </ul>
 *     </li>
 * </ul>
 * <p/>
 * The following Gradle properties are recognized by the
 * {@link ReleaseFlowLayer}:
 * <ul>
 *     <li><nobr>{@code -PmainBranch=NAME}</nobr> — the name of an existing Git
 *     branch, every commit into which is a released version of the
 *     application. If not given explicitly, the following defaults are
 *     considered (the first that exists is taken): {@code main},
 *     {@code master}.</li>
 *     <li><nobr>{@code -PdevBranch=NAME}</nobr> — the name of an existing Git
 *     branch, every commit into which is is a potentially releasable version
 *     of the application. If not given explicitly, the {@code dev} branch is
 *     taken, if it exists.</li>
 *     <li><nobr>{@code -PversionStrategy=STRATEGY}</nobr> — the strategy of
 *     devising the name of the released version. The following values are
 *     accepted:
 *     <ul>
 *         <li>{@code patch} — creates the next patch version from the current
 *         version, e.g.: <nobr>{@code 1.2.3-SNAPSHOT}</nobr> becomes
 *         <nobr>{@code 1.2.3}</nobr>.</li>
 *         <li>{@code minor} — creates the next minor version from the current
 *         version, e.g.: <nobr>{@code 1.2.3-SNAPSHOT}</nobr> becomes
 *         <nobr>{@code 1.3.0}</nobr>.</li>
 *         <li>{@code major} — creates the next major version from the current
 *         version, e.g.: <nobr>{@code 1.2.3-SNAPSHOT}</nobr> becomes
 *         <nobr>{@code 2.0.0}</nobr>.</li>
 *     </ul>
 *     If not given explicitly, the default strategy is {@code patch}. This
 *     property is incompatible with the
 *     <nobr>{@code -PreleaseVersion=VERSION}</nobr> property.
 *     </li>
 *     <li><nobr>{@code -PreleaseVersion=VERSION}</nobr> — the released
 *     version, given explicitly, e.g., {@code 1.6.0}. This property is
 *     incompatible with the <nobr>{@code -PversionStrategy=STRATEGY}</nobr>
 *     property.</li>
 *     <li><nobr>{@code -Premote=REMOTE_LIST}</nobr> — the comma-delimited list
 *     of names of Git remotes to push the release to. An empty value will
 *     result in the push step being skipped completely. If not given
 *     explicitly, the default single remote named {@code origin} is used.</li>
 *     <li><nobr>{@code -PnoPush}</nobr> — if this property is given, the push
 *     step is skipped completely.</li>
 *     <li><nobr>{@code -Pprop}</nobr> — description.</li>
 * </ul>
 */
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

        List<Task> buildAndPublishTasks = Stream.of(
                        ASSEMBLE_TASK_NAME,
                        CHECK_TASK_NAME,
                        BUILD_TASK_NAME,
                        PUBLISH_LIFECYCLE_TASK_NAME
                )
                .flatMap(taskName -> rootProject.getTasksByName(taskName, true).stream())
                .collect(Collectors.toUnmodifiableList());

        // Set up dependencies and ordering
        createReleaseTask.configure(it -> it.dependsOn(planReleaseTask));
        buildAndPublishTasks.forEach(it -> it.mustRunAfter(createReleaseTask));
        createNextTask.configure(it -> it.dependsOn(createReleaseTask, buildAndPublishTasks));
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
