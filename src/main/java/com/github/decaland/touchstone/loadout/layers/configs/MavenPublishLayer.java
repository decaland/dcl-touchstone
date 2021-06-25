package com.github.decaland.touchstone.loadout.layers.configs;

import com.github.decaland.touchstone.loadout.Loadout;
import com.github.decaland.touchstone.loadout.layers.ProjectAwareLayer;
import com.github.decaland.touchstone.loadout.layers.flavors.KotlinLayer;
import com.github.decaland.touchstone.loadout.layers.flavors.SpringBootLayer;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.api.publish.PublicationContainer;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;
import org.gradle.api.tasks.bundling.Jar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.decaland.touchstone.configs.BuildParametersManifest.*;

public class MavenPublishLayer extends ProjectAwareLayer {

    public static final String PUBLICATION_NAME_BOOT_APP = "decalandSpringBootApplication";
    public static final String PUBLICATION_NAME_BOOT_LIB = "decalandSpringBootLibrary";
    public static final String PUBLICATION_NAME_LIB = "decalandLibrary";

    private List<Class<? extends Plugin<Project>>> dependencies;

    public MavenPublishLayer() {
    }

    @Override
    public void apply(Project project, Loadout.Layers layers) {
        project.getPluginManager().apply(MavenPublishPlugin.class);
    }

    @Override
    public void configure(Project project, Loadout.Layers layers) {
        PublishingExtension publishingExtension = requireExtension(project, PublishingExtension.class);
        publishingExtension.repositories(repositories -> configureRepositories(project, repositories));
        configurePublications(project, layers, publishingExtension.getPublications());
    }

    @Override
    public boolean isReady(Project project, Loadout.Layers layers) {
        if (!usesSpringBoot(layers) || !isApplication(layers)) {
            return true;
        }
        if (dependencies == null) {
            dependencies = generateDependencies(layers);
        }
        return dependencies.stream()
                .allMatch(dependency -> project.getPlugins().hasPlugin(dependency));
    }

    private List<Class<? extends Plugin<Project>>> generateDependencies(@NotNull Loadout.Layers layers) {
        return Stream.of(
                ApplicationPlugin.class,
                layers.stream().anyMatch(layer -> layer instanceof KotlinLayer)
                        ? KotlinPluginWrapper.class
                        : null
        )
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableList());
    }

    private void configureRepositories(Project project, RepositoryHandler repositories) {
        if (project.hasProperty(PROP_KEY_SERPNET)) {
            repositories.maven(repository -> {
                if (project.getVersion().toString().endsWith("SNAPSHOT")) {
                    repository.setUrl(REPO_MAVEN_SNAPSHOTS_URL);
                } else {
                    repository.setUrl(REPO_MAVEN_RELEASES_URL);
                }
                repository.credentials(passwordCredentials -> {
                    passwordCredentials.setUsername(
                            requireProjectProperty(project, PROP_KEY_SERPNET_USERNAME, Object::toString)
                    );
                    passwordCredentials.setPassword(
                            requireProjectProperty(project, PROP_KEY_SERPNET_PASSWORD, Object::toString)
                    );
                });
            });
        } else {
            repositories.mavenLocal();
        }
    }

    private void configurePublications(Project project, Loadout.Layers layers, PublicationContainer publications) {
        String publicationName;
        Action<MavenPublication> publicationAction;
        if (usesSpringBoot(layers)) {
            if (isApplication(layers)) {
                publicationName = PUBLICATION_NAME_BOOT_APP;
                publicationAction = pub -> project.getTasks()
                        .withType(Jar.class)
                        .stream()
                        .filter(DefaultTask::isEnabled)
                        .forEach(pub::artifact);
            } else {
                publicationName = PUBLICATION_NAME_BOOT_LIB;
                publicationAction = pub -> pub.from(requireComponent(project, "java"));
            }
        } else {
            publicationName = PUBLICATION_NAME_LIB;
            publicationAction = pub -> pub.from(requireComponent(project, "java"));
        }
        publications.create(publicationName, MavenPublication.class, publicationAction);
    }

    private boolean usesSpringBoot(Loadout.Layers layers) {
        return layers.stream().anyMatch(layer -> layer instanceof SpringBootLayer);
    }

    private boolean isApplication(Loadout.Layers layers) {
        return layers.stream()
                .filter(layer -> layer instanceof SpringBootLayer)
                .anyMatch(layer -> ((SpringBootLayer) layer).isApplication());
    }
}
