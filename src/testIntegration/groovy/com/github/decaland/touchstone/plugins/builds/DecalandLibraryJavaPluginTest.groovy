package com.github.decaland.touchstone.plugins.builds

import com.github.decaland.touchstone.loadout.layers.configs.MavenPublishLayer
import com.github.decaland.touchstone.loadout.layers.configs.RepositoryConfigurationLayer
import io.spring.gradle.dependencymanagement.DependencyManagementPlugin
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.ArtifactRepository
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.internal.artifacts.repositories.DefaultMavenLocalArtifactRepository
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.Publication
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Shared
import spock.lang.Specification

import static com.github.decaland.touchstone.configs.BuildParametersManifest.VERSION_JAVA
import static com.github.decaland.touchstone.loadout.layers.configs.RepositoryConfigurationLayer.SERPNET_REPOS

class DecalandLibraryJavaPluginTest extends Specification {

    @Shared
    def project = ProjectBuilder.builder().build()

    def setupSpec() {
        project.pluginManager.apply(DecalandLibraryJavaPlugin)
    }

    def "repositories are set up correctly"() {
        expect:
        project.repositories.size() == 4
        projectHasRepos(project.repositories, SERPNET_REPOS)
        project.repositories.any {isMavenCentral(it) }
        project.repositories.any {isMavenLocal(it) }
    }

    def "dependency management is set up correctly"() {
        expect:
        project.plugins.hasPlugin(DependencyManagementPlugin)
    }
    def "publishing is set up correctly"() {
        expect:
        project.plugins.hasPlugin(MavenPublishPlugin)
        publishingConfiguredCorrectly(project.extensions.getByType(PublishingExtension))
    }

    def "java is set up correctly"() {
        expect:
        project.plugins.hasPlugin(JavaLibraryPlugin)
        javaVersionConfiguredCorrectly(project.extensions.getByType(JavaPluginExtension))
    }

    private static boolean projectHasRepos(
            RepositoryHandler repositoryHandler,
            Collection<RepositoryConfigurationLayer.SerpnetRepository> repos
    ) {
        repos.every { projectHasRepo(repositoryHandler, it) }
    }

    private static boolean projectHasRepo(
            RepositoryHandler repositoryHandler,
            RepositoryConfigurationLayer.SerpnetRepository serpnetRepo
    ) {
        repositoryHandler.any {
            it instanceof MavenArtifactRepository &&
                    it.name == serpnetRepo.name &&
                    (it as MavenArtifactRepository).url.toString() == serpnetRepo.url
        }
    }

    private static boolean isMavenCentral(ArtifactRepository repotisory) {
        repotisory instanceof MavenArtifactRepository &&
                (repotisory as MavenArtifactRepository).url.toString() == 'https://repo.maven.apache.org/maven2/'
    }

    private static boolean isMavenLocal(ArtifactRepository repository) {
        repository instanceof DefaultMavenLocalArtifactRepository
    }

    private static void publishingConfiguredCorrectly(PublishingExtension publishingExtension) {
        publishingExtension.repositories.size() == 1
        publishingExtension.repositories.any { isMavenLocal(it) }
        publishingExtension.publications.size() == 1
        publishingExtension.publications.any { isCorrectPublication(it) }
    }

    private static boolean isCorrectPublication(Publication publication) {
        publication.name == MavenPublishLayer.PUBLICATION_NAME_LIB
    }

    private static boolean javaVersionConfiguredCorrectly(JavaPluginExtension javaPluginExtension) {
        javaPluginExtension.getSourceCompatibility().toString() == VERSION_JAVA &&
                javaPluginExtension.getTargetCompatibility().toString() == VERSION_JAVA
    }
}
