package com.github.decaland.touchstone.loadouts;

import org.gradle.api.Project;

public class SpringApplicationJavaLoadout extends ApplicationJavaLoadout {

    public SpringApplicationJavaLoadout(Project project) {
        super(project);
    }

    @Override
    public void apply() {
        applySpringBootPlugins();
        super.apply();
    }
}
