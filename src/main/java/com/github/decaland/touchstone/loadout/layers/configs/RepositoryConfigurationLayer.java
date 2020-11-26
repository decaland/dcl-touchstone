package com.github.decaland.touchstone.loadout.layers.configs;

import com.github.decaland.touchstone.loadout.layers.LayerAccumulator;
import com.github.decaland.touchstone.loadout.layers.ProjectAwareLayer;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.MavenRepositoryContentDescriptor;

import static com.github.decaland.touchstone.configs.BuildParametersManifest.REPO_MAVEN_RELEASES;
import static com.github.decaland.touchstone.configs.BuildParametersManifest.REPO_MAVEN_SNAPSHOTS;

public class RepositoryConfigurationLayer extends ProjectAwareLayer {

    public RepositoryConfigurationLayer(Project project, LayerAccumulator.Finalized layers) {
        super(project, layers);
    }

    @Override
    public void configureLayer() {
        RepositoryHandler repositories = project.getRepositories();
        repositories.maven(repository -> {
            repository.setName("serpnetReleases");
            repository.setUrl(REPO_MAVEN_RELEASES);
            repository.mavenContent(MavenRepositoryContentDescriptor::releasesOnly);
        });
        repositories.maven(repository -> {
            repository.setName("serpnetSnapshots");
            repository.setUrl(REPO_MAVEN_SNAPSHOTS);
            repository.mavenContent(MavenRepositoryContentDescriptor::snapshotsOnly);
        });
        repositories.mavenCentral();
        repositories.mavenLocal();
    }
}
