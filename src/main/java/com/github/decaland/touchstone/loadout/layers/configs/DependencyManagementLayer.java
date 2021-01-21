package com.github.decaland.touchstone.loadout.layers.configs;

import com.github.decaland.touchstone.loadout.Loadout;
import com.github.decaland.touchstone.loadout.layers.ProjectAwareLayer;
import io.spring.gradle.dependencymanagement.DependencyManagementPlugin;
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension;
import org.gradle.api.Project;

import static com.github.decaland.touchstone.configs.dependencies.DependencyBom.getDependencyBom;

public class DependencyManagementLayer extends ProjectAwareLayer {

    private boolean isCustomizingPom = false;

    public DependencyManagementLayer() {
    }

    public DependencyManagementLayer(boolean isCustomizingPom) {
        this.isCustomizingPom = isCustomizingPom;
    }

    @Override
    public void apply(Project project, Loadout.Layers layers) {
        project.getPluginManager().apply(DependencyManagementPlugin.class);
    }

    @Override
    public void configure(Project project, Loadout.Layers layers) {
        DependencyManagementExtension extension = requireExtension(project, DependencyManagementExtension.class);
        extension.dependencies(dependenciesHandler ->
                getDependencyBom().forEach(dependency -> dependency.apply(dependenciesHandler))
        );
        extension.generatedPomCustomization(handler -> handler.setEnabled(isCustomizingPom));
    }

    public boolean isCustomizingPom() {
        return isCustomizingPom;
    }
}
