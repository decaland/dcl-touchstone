package com.github.decaland.touchstone.loadouts.libraries.boot;

import com.github.decaland.touchstone.loadouts.libraries.LibraryJavaKotlinLoadout;
import org.gradle.api.Project;
import org.jetbrains.kotlin.allopen.gradle.SpringGradleSubplugin;

public class SpringBootLibraryJavaKotlinLoadout extends LibraryJavaKotlinLoadout {

    public SpringBootLibraryJavaKotlinLoadout(Project project) {
        super(project);
    }

    @Override
    public void putOn() {
        applySpringBootPlugins();
        super.putOn();
        configureSpringBootLibrary();
        applyKotlinSpringPlugin();
    }

    private void applyKotlinSpringPlugin() {
        pluginManager.apply(SpringGradleSubplugin.class);
    }
}
