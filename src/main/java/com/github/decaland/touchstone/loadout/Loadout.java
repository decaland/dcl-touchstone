package com.github.decaland.touchstone.loadout;

import com.github.decaland.touchstone.loadout.layers.Layer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface Loadout {

    void putOn();

    interface Builder {

        <T extends Layer> Builder addLayer(Class<T> clazz);

        <T extends Layer> Builder addLayer(Class<T> clazz, @NotNull Consumer<T> configurer);

        <T extends Layer> Builder reconfigureLayer(Class<T> clazz, @NotNull Consumer<T> reconfigurer);

        <T extends Layer> Builder removeLayer(Class<T> clazz);

        <O extends Layer, I extends Layer> Builder swapLayer(Class<O> outgoing, Class<I> incoming);

        <O extends Layer, I extends Layer> Builder swapLayer(
                Class<O> outgoing, Class<I> incoming, @NotNull Consumer<I> configurer
        );

        <O extends Layer, I extends Layer> Builder swapOrAddLayer(
                Class<O> outgoing, Class<I> incoming
        );

        <O extends Layer, I extends Layer> Builder swapOrAddLayer(
                Class<O> outgoing, Class<I> incoming, @NotNull Consumer<I> configurer
        );

        @NotNull
        Loadout build();
    }
}
