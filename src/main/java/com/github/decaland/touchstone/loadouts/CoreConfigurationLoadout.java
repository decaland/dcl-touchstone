package com.github.decaland.touchstone.loadouts;

import com.github.decaland.touchstone.configs.DependencyVersionBom;
import io.spring.gradle.dependencymanagement.DependencyManagementPlugin;
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.github.decaland.touchstone.configs.BuildParametersManifest.REPO_MAVEN_RELEASES;
import static com.github.decaland.touchstone.configs.BuildParametersManifest.REPO_MAVEN_SNAPSHOTS;

abstract class CoreConfigurationLoadout extends GradleVersionAwareLoadout {

    public CoreConfigurationLoadout(Project project) {
        super(project);
        setUpRepositories();
        applyCorePlugins();
        configureCoreExtensions();
    }

    private void setUpRepositories() {
        RepositoryHandler repositories = project.getRepositories();
        repositories.mavenCentral();
        List<String> repositoryUrls = Arrays.asList(
                REPO_MAVEN_RELEASES,
                REPO_MAVEN_SNAPSHOTS
        );
        for (String repositoryUrl : repositoryUrls) {
            repositories.maven(repository -> repository.setUrl(repositoryUrl));
        }
    }

    private void applyCorePlugins() {
        pluginManager.apply(MavenPublishPlugin.class);
        pluginManager.apply(DependencyManagementPlugin.class);
    }

    private void configureMavenPublishPluginExtension() {
        requireExtension(PublishingExtension.class).getRepositories().maven(repository -> {
            if (project.getVersion().toString().endsWith("SNAPSHOT")) {
                repository.setUrl(REPO_MAVEN_SNAPSHOTS);
            } else {
                repository.setUrl(REPO_MAVEN_RELEASES);
            }
            repository.credentials(passwordCredentials -> {
                passwordCredentials.setUsername(
                        requireProjectProperty("dcl.repository.maven.username", Object::toString)
                );
                passwordCredentials.setPassword(
                        requireProjectProperty("dcl.repository.maven.password", Object::toString)
                );
            });
        });
    }

    private void configureCoreExtensions() {
        configureMavenPublishPluginExtension();
        requireExtension(DependencyManagementExtension.class)
                .dependencies(DependencyVersionBom::applyDependencyVersionConstraints);
    }
}
