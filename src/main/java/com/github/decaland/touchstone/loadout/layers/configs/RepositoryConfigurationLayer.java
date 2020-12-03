package com.github.decaland.touchstone.loadout.layers.configs;

import com.github.decaland.touchstone.loadout.layers.LayerAccumulator;
import com.github.decaland.touchstone.loadout.layers.ProjectAwareLayer;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.MavenRepositoryContentDescriptor;
import org.gradle.internal.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.github.decaland.touchstone.configs.BuildParametersManifest.REPO_MAVEN_RELEASES;
import static com.github.decaland.touchstone.configs.BuildParametersManifest.REPO_MAVEN_SNAPSHOTS;
import static org.gradle.api.artifacts.ArtifactRepositoryContainer.*;

public class RepositoryConfigurationLayer extends ProjectAwareLayer {

    public static final Collection<SerpnetRepository> SERPNET_REPOS = List.of(
            new SerpnetRepository("serpnetReleases", REPO_MAVEN_RELEASES, true),
            new SerpnetRepository("serpnetSnapshots", REPO_MAVEN_SNAPSHOTS, false)
    );

    public RepositoryConfigurationLayer(Project project, LayerAccumulator.Finalized layers) {
        super(project, layers);
    }

    @Override
    public void configureLayer() {
        RepositoryHandler repositories = project.getRepositories();
        for (SerpnetRepository serpnetRepo : SERPNET_REPOS) {
            repositories.maven(repository -> {
                repository.setName(serpnetRepo.getName());
                repository.setUrl(serpnetRepo.getUrl());
                if (serpnetRepo.isForReleases) {
                    repository.mavenContent(MavenRepositoryContentDescriptor::releasesOnly);
                } else {
                    repository.mavenContent(MavenRepositoryContentDescriptor::snapshotsOnly);
                }
            });
        }
        repositories.mavenCentral();
        repositories.mavenLocal();
    }

    public static class SerpnetRepository {
        private final String name;
        private final String url;
        private final boolean isForReleases;

        public SerpnetRepository(@NotNull String name, @NotNull String url, boolean isForReleases) {
            this.name = name;
            this.url = url;
            this.isForReleases = isForReleases;
        }

        @NotNull
        public String getName() {
            return name;
        }

        @NotNull
        public String getUrl() {
            return url;
        }

        public boolean isForReleases() {
            return isForReleases;
        }
    }
}
