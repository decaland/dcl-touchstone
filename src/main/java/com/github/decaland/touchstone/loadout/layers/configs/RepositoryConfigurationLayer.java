package com.github.decaland.touchstone.loadout.layers.configs;

import com.github.decaland.touchstone.loadout.Loadout;
import com.github.decaland.touchstone.loadout.layers.ProjectAwareLayer;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.MavenRepositoryContentDescriptor;

import static com.github.decaland.touchstone.configs.BuildParametersManifest.*;

public class RepositoryConfigurationLayer extends ProjectAwareLayer {

    public RepositoryConfigurationLayer() {
    }

    @Override
    public void configure(Project project, Loadout.Layers layers) {
        RepositoryHandler repositories = project.getRepositories();
        repositories.maven(repository -> {
            repository.setName(REPO_MAVEN_RELEASES_NAME);
            repository.setUrl(REPO_MAVEN_RELEASES_URL);
            repository.mavenContent(MavenRepositoryContentDescriptor::releasesOnly);
        });
        repositories.maven(repository -> {
            repository.setName(REPO_MAVEN_SNAPSHOTS_NAME);
            repository.setUrl(REPO_MAVEN_SNAPSHOTS_URL);
            repository.mavenContent(MavenRepositoryContentDescriptor::snapshotsOnly);
        });
        repositories.mavenCentral();
        repositories.mavenLocal();
    }
}
