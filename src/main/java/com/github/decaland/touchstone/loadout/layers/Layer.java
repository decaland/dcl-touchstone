package com.github.decaland.touchstone.loadout.layers;

public interface Layer {

    boolean readyForApplication();

    void applyLayer();

    void configureLayer();

    void markAsApplied();

    boolean isApplied();
}
