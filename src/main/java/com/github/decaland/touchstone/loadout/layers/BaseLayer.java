package com.github.decaland.touchstone.loadout.layers;

import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.plugins.PluginManager;
import org.jetbrains.annotations.NotNull;

public abstract class BaseLayer implements Layer {

    protected Project project;
    protected LayerAccumulator.Finalized layers;
    protected Logger logger;
    protected PluginManager pluginManager;
    protected PluginContainer pluginContainer;
    private boolean isApplied = false;

    public BaseLayer(@NotNull Project project, LayerAccumulator.Finalized layers) {
        this.project = project;
        this.layers = layers;
        this.logger = project.getLogger();
        this.pluginManager = project.getPluginManager();
        this.pluginContainer = project.getPlugins();
    }

    public boolean readyForApplication() {
        return true;
    }

    public void applyLayer() {
    }

    public void configureLayer() {
    }

    public final void markAsApplied() {
        isApplied = true;
    }

    public final boolean isApplied() {
        return this.isApplied;
    }
}