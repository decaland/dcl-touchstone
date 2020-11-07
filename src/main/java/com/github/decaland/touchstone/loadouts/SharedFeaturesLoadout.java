package com.github.decaland.touchstone.loadouts;

import org.gradle.api.Action;
import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.testing.Test;
import org.springframework.boot.gradle.plugin.SpringBootPlugin;

import static com.github.decaland.touchstone.configs.BuildParametersManifest.SOURCE_ENCODING;
import static com.github.decaland.touchstone.configs.BuildParametersManifest.VERSION_JAVA;

public abstract class SharedFeaturesLoadout extends CoreConfigurationLoadout {

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

    protected void configureSpringBootLibrary() {
        project.getTasks().getByName("bootJar").setEnabled(false);
        project.getTasks().getByName("jar").setEnabled(true);
    }

    protected void configureMavenPublishPluginExtensionPublications() {
        String publicationName;
        Action<MavenPublication> publicationAction;
        if (project.getPlugins().hasPlugin(SpringBootPlugin.class)) {
            if (isApplication()) {
                publicationName = "decalandSpringBootApplication";
                publicationAction = pub -> pub.artifact(project.getTasks().getByName("bootJar"));
            } else {
                publicationName = "decalandSpringBootLibrary";
                publicationAction = pub -> pub.artifact(project.getTasks().getByName("jar"));
            }
        } else {
            publicationName = "decalandLibrary";
            publicationAction = pub -> pub.from(project.getComponents().findByName("java"));
        }
        requireExtension(PublishingExtension.class)
                .getPublications()
                .create(publicationName, MavenPublication.class, publicationAction);
    }
}
