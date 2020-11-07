package com.github.decaland.touchstone.loadouts;

import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.publish.PublicationContainer;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.testing.Test;
import org.springframework.boot.gradle.plugin.SpringBootPlugin;

import static com.github.decaland.touchstone.configs.BuildParametersManifest.SOURCE_ENCODING;
import static com.github.decaland.touchstone.configs.BuildParametersManifest.VERSION_JAVA;

abstract class SharedFeaturesLoadout extends CoreConfigurationLoadout {

    public SharedFeaturesLoadout(Project project) {
        super(project);
    }

    protected void configureJavaPlugin() {
        configureJavaPluginExtension();
        project.getTasks().withType(JavaCompile.class, this::configureJavaPluginCompileTasks);
        project.getTasks().withType(Test.class, this::configureJavaPluginTestCompileTasks);
    }

    private void configureJavaPluginExtension() {
        JavaPluginExtension javaPluginExtension = requireExtension(JavaPluginExtension.class);
        JavaVersion javaVersion = JavaVersion.toVersion(VERSION_JAVA);
        javaPluginExtension.setSourceCompatibility(javaVersion);
        javaPluginExtension.setTargetCompatibility(javaVersion);
    }

    private void configureJavaPluginCompileTasks(JavaCompile task) {
        task.getOptions().setEncoding(SOURCE_ENCODING);
    }

    private void configureJavaPluginTestCompileTasks(Test task) {
        task.getSystemProperties().put("file.encoding", SOURCE_ENCODING);
    }

    protected void applySpringBootPlugins() {
        pluginManager.apply(SpringBootPlugin.class);
    }

    protected void configureMavenPublishPluginExtensionPublications() {
        PublicationContainer publications = requireExtension(PublishingExtension.class).getPublications();
        if (project.getPlugins().hasPlugin(SpringBootPlugin.class)) {
            publications.create(
                    "dclSpringBootApp", MavenPublication.class,
                    mavenPublication -> mavenPublication.artifact(project.getTasks().getByName("bootJar"))
            );
        } else {
            publications.create(
                    "dclLib", MavenPublication.class,
                    mavenPublication -> mavenPublication.from(project.getComponents().findByName("java"))
            );
        }
    }
}
