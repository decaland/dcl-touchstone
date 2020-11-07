package com.github.decaland.touchstone.loadouts.applications;

import com.github.decaland.touchstone.loadouts.libraries.LibraryJavaKotlinLoadout;
import org.gradle.api.Project;
import org.jetbrains.kotlin.allopen.gradle.SpringGradleSubplugin;

public class SpringBootApplicationJavaKotlinLoadout extends LibraryJavaKotlinLoadout {

    public SpringBootApplicationJavaKotlinLoadout(Project project) {
        super(project);
        makeApplication();
    }

    @Override
    public void putOn() {
        applySpringBootPlugins();
        super.putOn();
        applyKotlinSpringPlugin();
    }

    private void applyKotlinSpringPlugin() {
        pluginManager.apply(SpringGradleSubplugin.class);
    }
}
