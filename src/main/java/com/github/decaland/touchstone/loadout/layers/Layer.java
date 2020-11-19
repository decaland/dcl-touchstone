package com.github.decaland.touchstone.loadout.layers;

import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.plugins.PluginManager;

import java.util.Collection;

public abstract class Layer {

    protected Project project;
    protected Collection<Layer> layers;
    protected Logger logger;
    protected PluginManager pluginManager;
    protected PluginContainer pluginContainer;
    private boolean layerApplied = false;

    public Layer(Project project, Collection<Layer> layers) {
        this.project = project;
        this.layers = layers;
        this.logger = project.getLogger();
        this.pluginManager = project.getPluginManager();
        this.pluginContainer = project.getPlugins();
    }

    public boolean readyForApplication(Collection<Layer> layers) {
        return true;
    }

    public void applyLayer() {
    }

    public void configureLayer() {
    }

    public final void markApplied() {
        layerApplied = true;
    }

    public final boolean isApplied() {
        return this.layerApplied;
    }
}
