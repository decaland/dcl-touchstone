package com.github.decaland.touchstone.loadouts;

import groovy.lang.MissingPropertyException;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.plugins.PluginManager;

import java.util.Objects;
import java.util.function.Function;

abstract class BaseLoadout implements Loadout {

    private static final String MSG_MISSING_MANDATORY_EXTENSION
            = "Touchstone plugin failed to find mandatory Gradle plugin extension of class '%s'";
    private static final String MSG_MISSING_MANDATORY_PROPERTY
            = "Touchstone plugin failed to find mandatory Gradle property '%s'";
    private static final String MSG_ILLEGAL_MANDATORY_PROPERTY
            = "Touchstone plugin failed to parse mandatory Gradle property '%s', illegal value '%s'";

    protected final Project project;
    protected final Logger logger;
    protected final PluginManager pluginManager;

    public BaseLoadout(Project project) {
        this.project = project;
        this.logger = project.getLogger();
        this.pluginManager = project.getPluginManager();
    }

    protected <T> T requireExtension(Class<T> clazz) {
        try {
            return Objects.requireNonNull(project.getExtensions().findByType(clazz));
        } catch (NullPointerException e) {
            throw new GradleException(String.format(MSG_MISSING_MANDATORY_EXTENSION, clazz.getName()));
        }
    }

    protected Object requireProjectProperty(String propertyName) {
        try {
            return Objects.requireNonNull(project.property(propertyName));
        } catch (MissingPropertyException | NullPointerException ignored) {
            throw new GradleException(String.format(MSG_MISSING_MANDATORY_PROPERTY, propertyName));
        }
    }

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
