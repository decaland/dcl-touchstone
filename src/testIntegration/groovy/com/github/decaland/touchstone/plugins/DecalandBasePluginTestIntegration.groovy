package com.github.decaland.touchstone.plugins


import com.github.decaland.touchstone.loadout.Loadout
import com.github.decaland.touchstone.loadout.layers.Layer
import com.github.decaland.touchstone.plugins.DecalandBasePlugin
import com.github.decaland.touchstone.plugins.DecalandPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.util.GradleVersion
import spock.lang.Specification

import static com.github.decaland.touchstone.configs.BuildParametersManifest.MIN_VERSION_GRADLE

class DecalandBasePluginTestIntegration extends Specification {

    def "current version of Gradle satisfies requirement for consumers"() {
        given:
        GradleVersion currentVersion = GradleVersion.current()
        GradleVersion minimumVersion = GradleVersion.version(MIN_VERSION_GRADLE)

        expect:
        currentVersion >= minimumVersion
    }

    def "when plugin is applied, methods are called on its layers in correct order"() {
        given:
        def project = ProjectBuilder.builder().build()
        def layerA = Mock(Layer) {
            isReady(project, _ as Loadout.Layers) >>> [false, true]
        }
        def layerB = Mock(Layer) {
            isReady(project, _ as Loadout.Layers) >> true
        }
        def plugin = new DecalandBasePluginTestImpl(Loadout.builder().add(layerA).add(layerB).build())

        when:
        plugin.apply(project)

        then:
        1 * layerB.apply(project, _ as Loadout.Layers)
        then:
        1 * layerA.apply(project, _ as Loadout.Layers)
        then:
        1 * layerB.configure(project, _ as Loadout.Layers)
        then:
        1 * layerA.configure(project, _ as Loadout.Layers)
    }

    private static class DecalandBasePluginTestImpl extends DecalandBasePlugin {

        private final Loadout innerLoadout

        DecalandBasePluginTestImpl(Loadout innerLoadout) {
            this.innerLoadout = innerLoadout
        }

        @Override
        protected Loadout supplyLoadout() {
            return innerLoadout
        }

        @Override
        protected Class<? extends DecalandPlugin> getPluginType() {
            return DecalandBasePluginTestImpl
        }
    }
}
