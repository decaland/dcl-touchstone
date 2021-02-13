package com.github.decaland.touchstone.plugins.builds;

import com.github.decaland.touchstone.loadout.Loadout;
import com.github.decaland.touchstone.loadout.layers.configs.DependencyManagementLayer;
import com.github.decaland.touchstone.loadout.layers.configs.GradleWrapperLayer;
import com.github.decaland.touchstone.loadout.layers.configs.MavenPublishLayer;
import com.github.decaland.touchstone.loadout.layers.configs.RepositoryConfigurationLayer;
import com.github.decaland.touchstone.loadout.layers.flavors.JavaLibraryLayer;
import com.github.decaland.touchstone.loadout.layers.flavors.KotlinLayer;
import com.github.decaland.touchstone.loadout.layers.flavors.SpringBootLayer;
import com.github.decaland.touchstone.plugins.DecalandBuildConfigPlugin;
import com.github.decaland.touchstone.plugins.DecalandPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Arranges for the consuming project to be built as a non-executable,
 * consumable Spring Boot-based library written in either Java, or Kotlin, or
 * both.
 */
public class DecalandSpringBootLibraryJavaKotlinPlugin extends DecalandBuildConfigPlugin {

    @NotNull
    @Override
    public Loadout supplyLoadout() {
        return Loadout.builder()
                .add(new GradleWrapperLayer())
                .add(new RepositoryConfigurationLayer())
                .add(new DependencyManagementLayer())
                .add(new MavenPublishLayer())
                .add(new JavaLibraryLayer())
                .add(new KotlinLayer())
                .add(new SpringBootLayer(false))
                .build();
    }

    @NotNull
    @Override
    protected Class<? extends DecalandPlugin> getPluginType() {
        return DecalandSpringBootLibraryJavaKotlinPlugin.class;
    }
}
