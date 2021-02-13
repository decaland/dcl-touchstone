package com.github.decaland.touchstone.loadout.layers.configs;

import com.github.decaland.touchstone.loadout.Loadout;
import com.github.decaland.touchstone.loadout.layers.ProjectAwareLayer;
import org.gradle.api.Project;
import org.gradle.api.tasks.wrapper.Wrapper;

import static com.github.decaland.touchstone.configs.BuildParametersManifest.VERSION_GRADLE;

public class GradleWrapperLayer extends ProjectAwareLayer {

    public GradleWrapperLayer() {
    }

    @Override
    public void apply(Project project, Loadout.Layers layers) {
        Wrapper wrapperTask = (Wrapper) requireTask(project, "wrapper");
        wrapperTask.setGradleVersion(VERSION_GRADLE);
        wrapperTask.setDistributionType(Wrapper.DistributionType.ALL);
    }
}
