package com.github.decaland.touchstone.loadouts.libraries;

import com.github.decaland.touchstone.loadouts.SharedFeaturesLoadout;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;

public class LibraryJavaLoadout extends SharedFeaturesLoadout {

    public LibraryJavaLoadout(Project project) {
        super(project);
    }

    @Override
    public void putOn() {
        applyJavaPlugin();
        configureJavaPlugin();
        configureMavenPublishPluginExtensionPublications();
    }

    private void applyJavaPlugin() {
        pluginManager.apply(JavaPlugin.class);
    }
}
