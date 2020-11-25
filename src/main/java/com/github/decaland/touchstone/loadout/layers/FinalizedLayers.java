package com.github.decaland.touchstone.loadout.layers;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface FinalizedLayers {

    @NotNull
    Set<Layer> asUnmodifiableLinkedSet();

    boolean contains(Class<? extends Layer> clazz);
}
