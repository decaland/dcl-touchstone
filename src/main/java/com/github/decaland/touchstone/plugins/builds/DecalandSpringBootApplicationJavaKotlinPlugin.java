package com.github.decaland.touchstone.plugins.builds;

import com.github.decaland.touchstone.loadout.Loadout;
import com.github.decaland.touchstone.loadout.layers.flavors.JavaLayer;
import com.github.decaland.touchstone.loadout.layers.flavors.JavaLibraryLayer;
import com.github.decaland.touchstone.loadout.layers.flavors.SpringBootLayer;
import com.github.decaland.touchstone.plugins.DecalandPlugin;
import org.jetbrains.annotations.NotNull;

public class DecalandSpringBootApplicationJavaKotlinPlugin extends DecalandSpringBootLibraryJavaKotlinPlugin {

    @Override
    protected Loadout.Builder configurePluginLoadout(Loadout.Builder loadoutBuilder) {
        return super.configurePluginLoadout(loadoutBuilder)
                .swapLayer(JavaLibraryLayer.class, JavaLayer.class)
                .swapLayer(SpringBootLayer.class, SpringBootLayer.class, SpringBootLayer::markAsApplication);
    }

    @NotNull
    @Override
    protected Class<? extends DecalandPlugin> getPluginType() {
        return DecalandSpringBootApplicationJavaKotlinPlugin.class;
    }
}
