package com.github.decaland.touchstone.plugins.builds;

import com.github.decaland.touchstone.loadout.Loadout;
import com.github.decaland.touchstone.loadout.layers.flavors.JavaLayer;
import com.github.decaland.touchstone.loadout.layers.flavors.JavaLibraryLayer;
import com.github.decaland.touchstone.loadout.layers.flavors.SpringBootLayer;
import com.github.decaland.touchstone.plugins.DecalandPlugin;
import org.jetbrains.annotations.NotNull;

public class DecalandSpringBootApplicationJavaPlugin extends DecalandSpringBootLibraryJavaPlugin {

    @Override
    protected Loadout.Builder configurePluginLoadout(Loadout.Builder loadoutBuilder) {
        return super.configurePluginLoadout(loadoutBuilder)
                .swapLayer(JavaLibraryLayer.class, JavaLayer.class)
                .reconfigureLayer(SpringBootLayer.class, SpringBootLayer::markAsApplication);
    }

    @NotNull
    @Override
    protected Class<? extends DecalandPlugin> getPluginType() {
        return DecalandSpringBootApplicationJavaPlugin.class;
    }
}
