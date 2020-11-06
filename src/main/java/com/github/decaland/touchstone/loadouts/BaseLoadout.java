package com.github.decaland.touchstone.loadouts;

import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.plugins.PluginManager;

import java.util.Objects;

abstract class BaseLoadout implements Loadout {

    private static final String MSG_MISSING_MANDATORY_EXTENSION
            = "Touchstone plugin failed to find mandatory Gradle plugin extension of class '%s'";

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
}
