package com.github.decaland.touchstone.loadout.layers.configs;

import com.github.decaland.touchstone.loadout.layers.LayerAccumulator;
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

    private static final String PUBLICATION_NAME_BOOT_APP = "decalandSpringBootApplication";
    private static final String PUBLICATION_NAME_BOOT_LIB = "decalandSpringBootLibrary";
    private static final String PUBLICATION_NAME_LIB = "decalandLibrary";

    public MavenPublishLayer(Project project, LayerAccumulator.Finalized layers) {
        super(project, layers);
    }

    @Override
    public void applyLayer() {
        pluginManager.apply(MavenPublishPlugin.class);
    }

    @Override
    public void configureLayer() {
        PublishingExtension publishingExtension = requireExtension(PublishingExtension.class);
        publishingExtension.repositories(this::configureRepositories);
        configurePublications(publishingExtension.getPublications());
    }

    private void configureRepositories(RepositoryHandler repositories) {
        if (project.hasProperty(PROP_KEY_SERPNET)) {
            repositories.maven(repository -> {
                if (project.getVersion().toString().endsWith("SNAPSHOT")) {
                    repository.setUrl(REPO_MAVEN_SNAPSHOTS);
                } else {
                    repository.setUrl(REPO_MAVEN_RELEASES);
                }
                repository.credentials(passwordCredentials -> {
                    passwordCredentials.setUsername(
                            requireProjectProperty(PROP_KEY_SERPNET_USERNAME, Object::toString)
                    );
                    passwordCredentials.setPassword(
                            requireProjectProperty(PROP_KEY_SERPNET_PASSWORD, Object::toString)
                    );
                });
            });
        } else {
            repositories.mavenLocal();
        }
    }

    private void configurePublications(PublicationContainer publications) {
        String publicationName;
        Action<MavenPublication> publicationAction;
        if (usesSpringBoot()) {
            if (isApplication()) {
                publicationName = PUBLICATION_NAME_BOOT_APP;
                publicationAction = pub -> pub.artifact(requireTask(BOOT_JAR_TASK_NAME));
            } else {
                publicationName = PUBLICATION_NAME_BOOT_LIB;
                publicationAction = pub -> pub.from(requireComponent("java"));
            }
        } else {
            publicationName = PUBLICATION_NAME_LIB;
            publicationAction = pub -> pub.from(requireComponent("java"));
        }
        publications.create(publicationName, MavenPublication.class, publicationAction);
    }

    private boolean usesSpringBoot() {
        return layers.contains(SpringBootLayer.class);
    }

    private boolean isApplication() {
        return layers.asUnmodifiableLinkedSet()
                .stream()
                .filter(p -> p instanceof SpringBootLayer)
                .anyMatch(p -> ((SpringBootLayer) p).isApplication());
    }
}
