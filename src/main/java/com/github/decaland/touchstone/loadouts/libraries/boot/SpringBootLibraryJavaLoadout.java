package com.github.decaland.touchstone.loadouts.libraries.boot;

import com.github.decaland.touchstone.loadouts.libraries.LibraryJavaLoadout;
import org.gradle.api.Project;

public class SpringBootLibraryJavaLoadout extends LibraryJavaLoadout {

    public SpringBootLibraryJavaLoadout(Project project) {
        super(project);
    }

    @Override
    public void putOn() {
        applySpringBootPlugins();
        super.putOn();
        configureSpringBootLibrary();
    }
}
