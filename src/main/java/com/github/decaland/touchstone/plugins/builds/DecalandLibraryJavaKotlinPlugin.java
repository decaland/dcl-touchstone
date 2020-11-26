package com.github.decaland.touchstone.plugins.builds;

import com.github.decaland.touchstone.loadout.Loadout;
import com.github.decaland.touchstone.loadout.layers.flavors.KotlinLayer;
import com.github.decaland.touchstone.plugins.DecalandPlugin;
import org.jetbrains.annotations.NotNull;

public class DecalandLibraryJavaKotlinPlugin extends DecalandLibraryJavaPlugin {

    @Override
    protected Loadout.Builder configurePluginLoadout(Loadout.Builder loadoutBuilder) {
        return super.configurePluginLoadout(loadoutBuilder)
                .addLayer(KotlinLayer.class);
    }

    @NotNull
    @Override
    protected Class<? extends DecalandPlugin> getPluginType() {
        return DecalandLibraryJavaKotlinPlugin.class;
    }
}
