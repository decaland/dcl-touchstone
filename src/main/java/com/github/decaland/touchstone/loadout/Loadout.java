package com.github.decaland.touchstone.loadout;

import com.github.decaland.touchstone.loadout.layers.Layer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 * A stateless, immutable aggregation of {@link Layer}s capable of sequentially
 * applying them to a given Gradle {@link Project}, according to the contract
 * of the {@link #apply(Project, String)} method.
 */
public interface Loadout {

    /**
     * Shortcut to default {@link Builder} implementation.
     *
     * @return default {@link Builder} implementation
     */
    static Builder builder() {
        return BaseLoadout.builder();
    }

    /**
     * Applies the {@link Layers} to a given Gradle {@link Project}, using the
     * given plugin ID in potential error output.
     * <p/>
     * The implementations should follow the following contract:
     * <ul>
     * <li>Repeatedly scan through internal collection of {@link Layer}s,
     * applying those that are ready and have not yet been applied.
     * <li>Stop further scans whenever a scan ends with zero {@link Layer}s
     * applied.
     * <li>Remember the actual order of layers applied.
     * <li>If at least one layer is still not applied at this point, halt
     * with an error.
     * <li>After all layers are applied, proceed to sequentially configure each
     * layer, in the same order as they have been applied.
     * </ul>
     *
     * @param project  the Gradle {@link Project} to apply the layers to
     * @param pluginId the textual identifier of the Gradle {@link Plugin}, as
     *                 part of which this loadout is applied
     */
    void apply(Project project, String pluginId);

    /**
     * Returns the holder of the stream of layers that this loadout contains.
     *
     * @return the holder of the stream of layers that this loadout contains
     */
    Layers layers();

    /**
     * Mutable builder that assembles the immutable {@link Loadout}.
     */
    interface Builder {

        Builder add(Layer layer);

        @NotNull
        Loadout build();
    }

    /**
     * The holder of the stream of layers that a loadout contains.
     */
    interface Layers {

        Stream<Layer> stream();
    }
}
