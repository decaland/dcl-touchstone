package com.github.decaland.touchstone.loadout.layers.configs;

import com.github.decaland.touchstone.configs.DependencyVersionBom;
import com.github.decaland.touchstone.loadout.layers.Layer;
import com.github.decaland.touchstone.loadout.layers.ProjectAwareLayer;
import io.spring.gradle.dependencymanagement.DependencyManagementPlugin;
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension;
import org.gradle.api.Project;

import java.util.Collection;

public class DependencyManagementLayer extends ProjectAwareLayer {

    private boolean customizePom = false;

    public DependencyManagementLayer(Project project, Collection<Layer> layers) {
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
        extension.generatedPomCustomization(handler -> handler.setEnabled(customizePom));
    }

    public void enablePomCustomization() {
        this.customizePom = true;
    }
}
