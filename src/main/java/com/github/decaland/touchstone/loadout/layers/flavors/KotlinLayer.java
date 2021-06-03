package com.github.decaland.touchstone.loadout.layers.flavors;

import com.github.decaland.touchstone.loadout.Loadout;
import com.github.decaland.touchstone.loadout.layers.ProjectAwareLayer;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.internal.logging.services.DefaultLoggingManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.dokka.gradle.DokkaPlugin;
import org.jetbrains.dokka.gradle.DokkaTask;
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension;
import org.jetbrains.kotlin.allopen.gradle.AllOpenGradleSubplugin;
import org.jetbrains.kotlin.allopen.gradle.SpringGradleSubplugin;
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions;
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper;
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile;
import org.jetbrains.kotlin.noarg.gradle.KotlinJpaSubplugin;
import org.jetbrains.kotlinx.serialization.gradle.SerializationGradleSubplugin;
import org.springframework.boot.gradle.plugin.SpringBootPlugin;

import java.io.File;
import java.util.Collections;

import static com.github.decaland.touchstone.configs.BuildParametersManifest.VERSION_JAVA;
import static com.github.decaland.touchstone.configs.BuildParametersManifest.VERSION_KOTLIN_API;
import static org.gradle.api.plugins.internal.JvmPluginsHelper.configureDocumentationVariantWithArtifact;
import static org.gradle.api.plugins.internal.JvmPluginsHelper.findJavaComponent;

public class KotlinLayer extends ProjectAwareLayer {

    private static final String DOKKA_HTML_TASK_NAME = "dokkaHtml";

    private static final String KDOC = "kdoc";
    private static final String KDOC_DIRECTORY_NAME = KDOC;
    private static final String KDOC_JAR_TASK_NAME = KDOC + "Jar";
    private static final String KDOC_ELEMENTS_CONFIGURATION_NAME = KDOC + "Elements";

    public KotlinLayer() {
    }

    @Override
    public void apply(Project project, Loadout.Layers layers) {
        project.getPluginManager().apply(KotlinPluginWrapper.class);
        project.getPlugins().withType(SpringBootPlugin.class, plugin -> {
            project.getPluginManager().apply(SpringGradleSubplugin.class);
        });
        project.getPluginManager().apply(SerializationGradleSubplugin.class);
        project.getPluginManager().apply(AllOpenGradleSubplugin.class);
        project.getPluginManager().apply(KotlinJpaSubplugin.class);
        project.getPluginManager().apply(DokkaPlugin.class);
    }

    @Override
    public void configure(@NotNull Project project, Loadout.Layers layers) {
        project.getTasks().withType(KotlinCompile.class, this::configureKotlinPluginCompileTasks);
        openClassesForJpa(project);
        withKdocJar(project);
        addDependencies(project);
    }

    private void configureKotlinPluginCompileTasks(@NotNull KotlinCompile task) {
        KotlinJvmOptions kotlinOptions = task.getKotlinOptions();
        kotlinOptions.setApiVersion(VERSION_KOTLIN_API);
        kotlinOptions.setLanguageVersion(VERSION_KOTLIN_API);
        kotlinOptions.setJvmTarget(VERSION_JAVA);
        kotlinOptions.setFreeCompilerArgs(Collections.singletonList("-Xjsr305=strict"));
    }

    private void openClassesForJpa(@NotNull Project project) {
        AllOpenExtension allOpenExtension = project.getExtensions().findByType(AllOpenExtension.class);
        if (allOpenExtension != null) {
            allOpenExtension.annotations(
                    "javax.persistence.Entity",
                    "javax.persistence.MappedSuperclass",
                    "javax.persistence.Embeddable"
            );
        }
    }

    private void withKdocJar(@NotNull Project project) {
        // Set up convinience variables
        TaskContainer tasks = project.getTasks();
        ConfigurationContainer configurations = project.getConfigurations();
        JavaPluginConvention convention = project.getConvention().getPlugin(JavaPluginConvention.class);
        File kdocDir = new File(convention.getDocsDir(), KDOC_DIRECTORY_NAME);

        // Set up dokka HTML task
        tasks.named(DOKKA_HTML_TASK_NAME, DokkaTask.class, dokkaHtmlTask -> {
            ((DefaultLoggingManager) dokkaHtmlTask.getLogging()).setLevelInternal(LogLevel.INFO);
            dokkaHtmlTask.getOutputDirectory().set(kdocDir);
        });

        // Configure variant and jar task for kdoc; make kdoc jar depend on Dokka HTML
        configureDocumentationVariantWithArtifact(
                KDOC_ELEMENTS_CONFIGURATION_NAME,
                null,
                KDOC,
                Collections.emptyList(),
                KDOC_JAR_TASK_NAME,
                kdocDir,
                findJavaComponent(project.getComponents()),
                configurations,
                tasks,
                project.getObjects()
        );
        tasks.named(KDOC_JAR_TASK_NAME, kdocJarTask -> kdocJarTask.dependsOn(tasks.named(DOKKA_HTML_TASK_NAME)));
    }

    private void addDependencies(@NotNull Project project) {
        project.getDependencies().add("implementation", "org.jetbrains.kotlin:kotlin-stdlib-jdk8");
        project.getDependencies().add("implementation", "org.jetbrains.kotlin:kotlin-reflect");
        project.getDependencies().add("implementation", "org.jetbrains.kotlinx:kotlinx-serialization-json");
        project.getDependencies().add("implementation", "org.jetbrains.kotlinx:kotlinx-coroutines-core");
        project.getDependencies().add("implementation", "com.fasterxml.jackson.module:jackson-module-kotlin");
    }
}
