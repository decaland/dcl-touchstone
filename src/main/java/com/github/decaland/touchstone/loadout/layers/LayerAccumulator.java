package com.github.decaland.touchstone.loadout.layers;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Consumer;

public interface LayerAccumulator {

    LayerAccumulator.Finalized pending();

    LayerAccumulator.Finalized finalized();

    void add(Layer layer);

    <T extends Layer> void reconfigureMatching(Class<T> clazz, Consumer<T> reconfigurer);

    void removeMatching(Class<? extends Layer> clazz);

    void swapMatching(Class<? extends Layer> clazz, Layer replacement);

    void swapMatchingOrAdd(Class<? extends Layer> clazz, Layer replacement);

    interface Finalized {

        @NotNull
        Set<Layer> asUnmodifiableLinkedSet();

        boolean contains(Class<? extends Layer> clazz);
    }
}
