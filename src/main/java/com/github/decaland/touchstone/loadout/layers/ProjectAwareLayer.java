package com.github.decaland.touchstone.loadout.layers;

import groovy.lang.MissingPropertyException;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.component.SoftwareComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

public abstract class ProjectAwareLayer extends BaseLayer {

    private static final String MSG_MISSING_MANDATORY_EXTENSION
            = "Touchstone plugin failed to find mandatory Gradle plugin extension of class '%s'";
    private static final String MSG_MISSING_MANDATORY_PROPERTY
            = "Touchstone plugin failed to find mandatory Gradle property '%s'";
    private static final String MSG_ILLEGAL_MANDATORY_PROPERTY
            = "Touchstone plugin failed to parse mandatory Gradle property '%s', illegal value '%s'";
    private static final String MSG_MISSING_MANDATORY_TASK
            = "Touchstone plugin failed to find mandatory Gradle task '%s'";
    private static final String MSG_MISSING_MANDATORY_COMPONENT
            = "Touchstone plugin failed to find mandatory Gradle component '%s'";
    private static final String MSG_MISSING_MANDATORY_CONFIGURATION
            = "Touchstone plugin failed to find mandatory Gradle configuration '%s'";

    public ProjectAwareLayer(Project project, LayerAccumulator.Finalized layers) {
        super(project, layers);
    }

    @NotNull
    protected Task requireTask(String taskName) {
        try {
            return Objects.requireNonNull(project.getTasks().findByName(taskName));
        } catch (NullPointerException ignored) {
            throw new GradleException(String.format(MSG_MISSING_MANDATORY_TASK, taskName));
        }
    }

    @NotNull
    protected SoftwareComponent requireComponent(String componentName) {
        try {
            return Objects.requireNonNull(project.getComponents().findByName(componentName));
        } catch (NullPointerException ignored) {
            throw new GradleException(String.format(MSG_MISSING_MANDATORY_COMPONENT, componentName));
        }
    }

    @NotNull
    protected Configuration requireConfiguration(String configurationName) {
        try {
            return Objects.requireNonNull(project.getConfigurations().findByName(configurationName));
        } catch (NullPointerException ignored) {
            throw new GradleException(String.format(MSG_MISSING_MANDATORY_CONFIGURATION, configurationName));
        }
    }

    @NotNull
    protected <T> T requireExtension(Class<T> clazz) {
        try {
            return Objects.requireNonNull(project.getExtensions().findByType(clazz));
        } catch (NullPointerException ignored) {
            throw new GradleException(String.format(MSG_MISSING_MANDATORY_EXTENSION, clazz.getSimpleName()));
        }
    }

    @NotNull
    protected Object requireProjectProperty(String propertyName) {
        try {
            return Objects.requireNonNull(project.property(propertyName));
        } catch (MissingPropertyException | NullPointerException ignored) {
            throw new GradleException(String.format(MSG_MISSING_MANDATORY_PROPERTY, propertyName));
        }
    }

    @NotNull
    protected <T> T requireProjectProperty(String propertyName, Function<Object, T> function) {
        Object propertyObject = requireProjectProperty(propertyName);
        try {
            return function.apply(propertyObject);
        } catch (RuntimeException ignored) {
            throw new GradleException(
                    String.format(MSG_ILLEGAL_MANDATORY_PROPERTY, propertyName, propertyObject.toString())
            );
        }
    }
}
