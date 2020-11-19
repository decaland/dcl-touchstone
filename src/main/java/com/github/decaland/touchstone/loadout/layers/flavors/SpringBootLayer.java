package com.github.decaland.touchstone.loadout.layers.flavors;

import com.github.decaland.touchstone.loadout.layers.Layer;
import com.github.decaland.touchstone.loadout.layers.ProjectAwareLayer;
import io.spring.gradle.dependencymanagement.DependencyManagementPlugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.attributes.*;
import org.springframework.boot.gradle.plugin.SpringBootPlugin;

import java.util.Collection;

import static org.gradle.api.plugins.JavaPlugin.JAR_TASK_NAME;
import static org.springframework.boot.gradle.plugin.SpringBootPlugin.BOOT_JAR_TASK_NAME;

public class SpringBootLayer extends ProjectAwareLayer {

    private boolean isApplication = false;

    public SpringBootLayer(Project project, Collection<Layer> layers) {
        super(project, layers);
    }

    @Override
    public void applyLayer() {
        if (!pluginContainer.hasPlugin(DependencyManagementPlugin.class)) {
            pluginManager.apply(DependencyManagementPlugin.class);
        }
        pluginManager.apply(SpringBootPlugin.class);
    }

    @Override
    public void configureLayer() {
        if (!isApplication()) {
            configureSpringBootLibrary();
        }
    }

    private void addBootComponent() {
        Configuration bootArchives = requireConfiguration("bootArchives");
        bootArchives.attributes(attributes -> {
            attributes.attribute(
                    Attribute.of("org.gradle.usage", Usage.class),
                    project.getObjects().named(Usage.class, Usage.JAVA_RUNTIME)
            );
            attributes.attribute(
                    Attribute.of("org.gradle.category", Category.class),
                    project.getObjects().named(Category.class, Category.LIBRARY)
            );
            attributes.attribute(
                    Attribute.of("org.gradle.libraryelements", LibraryElements.class),
                    project.getObjects().named(LibraryElements.class, LibraryElements.JAR)
            );
            attributes.attribute(
                    Attribute.of("org.gradle.dependency.bundling", Bundling.class),
                    project.getObjects().named(Bundling.class, Bundling.EMBEDDED)
            );
        });
//        AdhocComponentWithVariants bootComponent = componentFactory.adhoc("boot");
//        project.getComponents().add(bootComponent);
//        bootComponent.addVariantsFromConfiguration(
//                requireConfiguration("bootArchives"),
//                variantDetails -> variantDetails.mapToMavenScope("runtime")
//        );
    }

    protected void configureSpringBootLibrary() {
        requireTask(BOOT_JAR_TASK_NAME).setEnabled(false);
        requireTask(JAR_TASK_NAME).setEnabled(true);
    }

    public boolean isApplication() {
        return isApplication;
    }

    public void markApplication() {
        this.isApplication = true;
    }
}
