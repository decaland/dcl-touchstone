package com.github.decaland.touchstone.loadout.layers.configs;

import com.github.decaland.touchstone.loadout.Loadout;
import com.github.decaland.touchstone.loadout.layers.ProjectAwareLayer;
import com.github.decaland.touchstone.loadout.layers.flavors.SpringBootLayer;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.publish.PublicationContainer;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;

import static com.github.decaland.touchstone.configs.BuildParametersManifest.*;
import static org.springframework.boot.gradle.plugin.SpringBootPlugin.BOOT_JAR_TASK_NAME;

public class MavenPublishLayer extends ProjectAwareLayer {

    public static final String PUBLICATION_NAME_BOOT_APP = "decalandSpringBootApplication";
    public static final String PUBLICATION_NAME_BOOT_LIB = "decalandSpringBootLibrary";
    public static final String PUBLICATION_NAME_LIB = "decalandLibrary";

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
                publicationAction = pub -> pub.artifact(requireTask(project, BOOT_JAR_TASK_NAME));
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
