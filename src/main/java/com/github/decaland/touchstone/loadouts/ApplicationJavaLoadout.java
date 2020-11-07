package com.github.decaland.touchstone.loadouts;

import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;

public class ApplicationJavaLoadout extends SharedFeaturesLoadout {

    public ApplicationJavaLoadout(Project project) {
        super(project);
    }

    @Override
    public void apply() {
        applyJavaPlugin();
        configureJavaPlugin();
        configureMavenPublishPluginExtensionPublications();
    }

    private void applyJavaPlugin() {
        pluginManager.apply(JavaPlugin.class);
    }
}
