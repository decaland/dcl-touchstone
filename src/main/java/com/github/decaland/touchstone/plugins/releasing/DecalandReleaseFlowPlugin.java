package com.github.decaland.touchstone.plugins.releasing;

import com.github.decaland.touchstone.loadout.Loadout;
import com.github.decaland.touchstone.loadout.layers.releasing.ReleaseFlowLayer;
import com.github.decaland.touchstone.plugins.DecalandBasePlugin;
import com.github.decaland.touchstone.plugins.DecalandPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Plugin containing solely the {@link ReleaseFlowLayer}; not to be distributed
 * to consumers, but instead used by the Touchstone project itself via the
 * hack-y {@code buildSrc} directory. Consumers use {@link ReleaseFlowLayer} as
 * part of other plugins.
 */
public class DecalandReleaseFlowPlugin extends DecalandBasePlugin {

    @NotNull
    @Override
    protected Loadout supplyLoadout() {
        return Loadout.builder()
                .add(new ReleaseFlowLayer())
                .build();
    }

    @NotNull
    @Override
    protected Class<? extends DecalandPlugin> getPluginType() {
        return DecalandReleaseFlowPlugin.class;
    }
}
