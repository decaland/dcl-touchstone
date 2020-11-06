package com.github.decaland.touchstone.loadouts;

import org.gradle.api.Project;
import org.jetbrains.kotlin.allopen.gradle.SpringGradleSubplugin;

public class SpringApplicationJavaKotlinLoadout extends ApplicationJavaKotlinLoadout {

    public SpringApplicationJavaKotlinLoadout(Project project) {
        super(project);
    }

    @Override
    public void apply() {
        applySpringBootPlugins();
        super.apply();
        applyKotlinSpringPlugin();
    }

    private void applyKotlinSpringPlugin() {
        pluginManager.apply(SpringGradleSubplugin.class);
    }
}
