package com.github.decaland.touchstone.loadout.layers;

import com.github.decaland.touchstone.loadout.Loadout;
import org.gradle.api.Project;

public abstract class BaseLayer implements Layer {

    @Override
    public boolean isReady(Project project, Loadout.Layers layers) {
        return true;
    }

    @Override
    public void apply(Project project, Loadout.Layers layers) {
    }

    @Override
    public void configure(Project project, Loadout.Layers layers) {
    }
}
