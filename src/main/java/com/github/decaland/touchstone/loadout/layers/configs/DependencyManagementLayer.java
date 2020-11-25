package com.github.decaland.touchstone.loadout.layers.configs;

import com.github.decaland.touchstone.configs.DependencyVersionBom;
import com.github.decaland.touchstone.loadout.layers.FinalizedLayers;
import com.github.decaland.touchstone.loadout.layers.ProjectAwareLayer;
import io.spring.gradle.dependencymanagement.DependencyManagementPlugin;
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension;
import org.gradle.api.Project;

public class DependencyManagementLayer extends ProjectAwareLayer {

    private boolean isCustomizingPom = false;

    public DependencyManagementLayer(Project project, FinalizedLayers layers) {
        super(project, layers);
    }

    @Override
    public void applyLayer() {
        pluginManager.apply(DependencyManagementPlugin.class);
    }

    @Override
    public void configureLayer() {
        DependencyManagementExtension extension = requireExtension(DependencyManagementExtension.class);
        extension.dependencies(DependencyVersionBom::applyDependencyVersionConstraints);
        extension.generatedPomCustomization(handler -> handler.setEnabled(isCustomizingPom));
    }

    public void enablePomCustomization() {
        this.isCustomizingPom = true;
    }
}
