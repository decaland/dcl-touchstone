package com.github.decaland.touchstone.loadout.layers.releasing;

import com.github.decaland.touchstone.loadout.Loadout;
import com.github.decaland.touchstone.loadout.layers.ProjectAwareLayer;
import org.gradle.api.Project;

public class ReleaseFlowLayer extends ProjectAwareLayer {

    @Override
    public void apply(Project project, Loadout.Layers layers) {
        project.getTasks().register("release", taskConfig -> {
            taskConfig.doLast(task -> {
                task.getProject().getLogger().lifecycle("Releasing this version (not really)");
            });
        });
    }
}
