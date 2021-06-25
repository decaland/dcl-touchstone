package com.github.decaland.touchstone.plugins


import com.github.decaland.touchstone.plugins.builds.*
import com.github.decaland.touchstone.plugins.dependencies.testing.DecalandUsesJunitFivePlugin
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.UnexpectedBuildSuccess
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.util.stream.Collectors
import java.util.stream.Stream

import static com.github.decaland.touchstone.loadout.Loadout.LIFECYCLE_LOG_APPLY_LAYER
import static com.github.decaland.touchstone.loadout.Loadout.LIFECYCLE_LOG_CONFIGURE_LAYER
import static com.github.decaland.touchstone.plugins.DecalandPlugin.LIFECYCLE_LOG_APPLY_PLUGIN

class DecalandPluginTestFunctional extends Specification {

    @Rule
    TemporaryFolder testProjectDir = new TemporaryFolder()

    File buildFile

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
    }

    def "when project is built with valid plugin combo, expected output is printed"(
            DecalandPlugin pluginA,
            DecalandPlugin pluginB
    ) {
        given:
        buildFile << composeBuildFile(pluginA, pluginB)
        List<String> expectedOutput = composePluginsOutput(pluginA, pluginB)

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withArguments("--info")
                .build()

        then:
        noExceptionThrown()
        result.output.split('\n').toList().containsAll(expectedOutput)

        where:
        [pluginA, pluginB] << assembleValidPluginPairs()
    }

    def "when project is built with invalid plugin combo, build fails"(
            DecalandPlugin pluginA,
            DecalandPlugin pluginB,
            DecalandPlugin pluginC
    ) {
        given:
        buildFile << composeBuildFile(pluginA, pluginB, pluginC)

        when:
        GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .buildAndFail()

        then:
        notThrown(UnexpectedBuildSuccess)

        where:
        [pluginA, pluginB, pluginC] << assembleInvalidPluginTriplets()
    }

    private static List<List<DecalandPlugin>> assembleValidPluginPairs() {
        [
                [
                        new DecalandLibraryJavaPlugin(),
                        new DecalandLibraryJavaKotlinPlugin(),
                        new DecalandSpringBootLibraryJavaPlugin(),
                        new DecalandSpringBootLibraryJavaKotlinPlugin(),
                        new DecalandSpringBootApplicationJavaPlugin(),
                        new DecalandSpringBootApplicationJavaKotlinPlugin(),
                ],
                [
                        null,
                        new DecalandUsesJunitFivePlugin(),
                ],
        ].combinations() + [[null, null]]
    }

    private static List<List<DecalandPlugin>> assembleInvalidPluginTriplets() {
        [
                [
                        new DecalandLibraryJavaPlugin(),
                        new DecalandLibraryJavaPlugin(),
                        null,
                ],
                [
                        new DecalandLibraryJavaKotlinPlugin(),
                        new DecalandLibraryJavaPlugin(),
                        null,
                ],
                [
                        new DecalandLibraryJavaPlugin(),
                        new DecalandLibraryJavaPlugin(),
                        new DecalandUsesJunitFivePlugin(),
                ],
                [
                        new DecalandLibraryJavaKotlinPlugin(),
                        new DecalandLibraryJavaPlugin(),
                        new DecalandUsesJunitFivePlugin(),
                ],
                [
                        new DecalandUsesJunitFivePlugin(),
                        null,
                        null,
                ],
                [
                        new DecalandUsesJunitFivePlugin(),
                        new DecalandSpringBootLibraryJavaPlugin(),
                        null,
                ],
                [
                        new DecalandSpringBootLibraryJavaPlugin(),
                        new DecalandUsesJunitFivePlugin(),
                        new DecalandUsesJunitFivePlugin(),
                ],
                [
                        null,
                        new DecalandUsesJunitFivePlugin(),
                        new DecalandUsesJunitFivePlugin(),
                ],
        ]
    }

    private static List<String> composePluginsOutput(DecalandPlugin... plugins) {
        Stream.of(plugins)
                .filter { it != null }
                .map { composePluginOutput(it) }
                .flatMap { it }
                .collect(Collectors.toUnmodifiableList())
    }

    private static Stream<String> composePluginOutput(DecalandPlugin plugin) {
        return Stream.of(
                Stream.of(String.format(LIFECYCLE_LOG_APPLY_PLUGIN, plugin.pluginId)),
                composeLayersOutput(plugin, LIFECYCLE_LOG_APPLY_LAYER),
                composeLayersOutput(plugin, LIFECYCLE_LOG_CONFIGURE_LAYER)
        ).flatMap { it }
    }

    private static Stream<String> composeLayersOutput(DecalandPlugin plugin, String messagePattern) {
        plugin.loadout
                .layers()
                .stream()
                .map { layer ->
                    String.format(messagePattern, layer.getClass().getSimpleName())
                }
    }

    private static String composeBuildFile(DecalandPlugin... plugins) {
        return Stream.of(
                Stream.of("plugins {"),
                Stream.of(plugins)
                        .filter { it != null }
                        .map { "    id '${it.pluginId}'" },
                Stream.of("}")
        ).flatMap { it }
                .collect(Collectors.joining('\n'))
    }
}
