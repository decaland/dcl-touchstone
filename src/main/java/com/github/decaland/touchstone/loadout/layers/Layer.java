package com.github.decaland.touchstone.loadout.layers;

import com.github.decaland.touchstone.loadout.Loadout;
import org.gradle.api.Project;

/**
 * A stateless, immutable container for logic that configures a given Gradle
 * {@link Project} for a singular well-defined purpose. Although nothing
 * prevents the user from calling these methods indiscriminately, the correct
 * application of layers is guaranteed by a {@link Loadout}.
 */
public interface Layer {

    /**
     * Analyzes current state of the provided Gradle {@link Project}, as well
     * as the stream of layers included in the current {@link Loadout}, and
     * returns a judgement on whether the current layer can be applied at this
     * point in time.
     * <p>
     * The goal is to allow ordering of layers while they are being applied to
     * a {@link Project}. The {@link Loadout} does not provide means to tell
     * whether a particular {@link Layer} has been applied or not: instead
     * judgement has to be made based on the side-effects the other layers have
     * on the {@link Project}.
     *
     * @param project the Gradle {@link Project} to analyze
     * @param layers  the list of layers in current {@link Loadout}
     * @return whether this layer can be applied at this point in time
     */
    boolean isReady(Project project, Loadout.Layers layers);

    /**
     * Applies the logic of this layer to the given Gradle {@link Project},
     * while the stream of layers included in the current {@link Loadout} can
     * be analyzed to appropriately modify that logic.
     *
     * @param project the Gradle {@link Project} to apply the layer to
     * @param layers  the list of layers in current {@link Loadout}
     */
    void apply(Project project, Loadout.Layers layers);

    /**
     * Applies additional configuration logic to the given Gradle
     * {@link Project}, after all {@link #apply(Project, Loadout.Layers)}
     * methods of all layers in the current {@link Loadout} have been called,
     * and in the same order.
     *
     * @param project the Gradle {@link Project} to configure the layer with
     * @param layers  the list of layers in current {@link Loadout}
     */
    void configure(Project project, Loadout.Layers layers);
}
