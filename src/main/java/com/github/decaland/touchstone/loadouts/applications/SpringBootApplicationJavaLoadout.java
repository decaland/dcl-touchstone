package com.github.decaland.touchstone.loadouts.applications;

import com.github.decaland.touchstone.loadouts.libraries.LibraryJavaLoadout;
import org.gradle.api.Project;

public class SpringBootApplicationJavaLoadout extends LibraryJavaLoadout {

    public SpringBootApplicationJavaLoadout(Project project) {
        super(project);
        makeApplication();
    }

    @Override
    public void putOn() {
        applySpringBootPlugins();
        super.putOn();
    }
}
