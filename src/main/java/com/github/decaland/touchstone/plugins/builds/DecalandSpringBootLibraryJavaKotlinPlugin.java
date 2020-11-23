package com.github.decaland.touchstone.plugins.builds;

import com.github.decaland.touchstone.loadout.Loadout;
import com.github.decaland.touchstone.loadout.layers.flavors.SpringBootLayer;
import com.github.decaland.touchstone.plugins.DecalandPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Arranges for the consuming project to be built as a non-executable,
 * consumable Spring Boot-based library written in either Java, or Kotlin, or
 * both.
 */
public class DecalandSpringBootLibraryJavaKotlinPlugin extends DecalandLibraryJavaKotlinPlugin {

    @Override
    protected void configurePluginLoadout(Loadout pluginLoadout) {
        super.configurePluginLoadout(pluginLoadout);
        pluginLoadout.addLayer(SpringBootLayer.class);
    }

    @NotNull
    @Override
    protected Class<? extends DecalandPlugin> getPluginType() {
        return DecalandSpringBootLibraryJavaKotlinPlugin.class;
    }
}
