package com.github.decaland.touchstone.plugins.builds;

import com.github.decaland.touchstone.loadout.Loadout;
import com.github.decaland.touchstone.loadout.layers.configs.DependencyManagementLayer;
import com.github.decaland.touchstone.loadout.layers.configs.MavenPublishLayer;
import com.github.decaland.touchstone.loadout.layers.configs.RepositoryConfigurationLayer;
import com.github.decaland.touchstone.loadout.layers.flavors.JavaLayer;
import com.github.decaland.touchstone.loadout.layers.flavors.SpringBootLayer;
import com.github.decaland.touchstone.plugins.DecalandBuildConfigPlugin;
import com.github.decaland.touchstone.plugins.DecalandPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Arranges for the consuming project to be built as an executable Spring Boot
 * application written in Java.
 */
public class DecalandSpringBootApplicationJavaPlugin extends DecalandBuildConfigPlugin {

    @NotNull
    @Override
    public Loadout supplyLoadout() {
        return Loadout.builder()
                .add(new RepositoryConfigurationLayer())
                .add(new DependencyManagementLayer())
                .add(new MavenPublishLayer())
                .add(new JavaLayer())
                .add(new SpringBootLayer(true))
                .build();
    }

    @NotNull
    @Override
    protected Class<? extends DecalandPlugin> getPluginType() {
        return DecalandSpringBootApplicationJavaPlugin.class;
    }
}
