package com.github.decaland.touchstone.loadout.layers.releasing.models;

import org.jetbrains.annotations.NotNull;

public class IllegalVersionException extends Exception {

    public IllegalVersionException(@NotNull String message) {
        super(message);
    }

    public IllegalVersionException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
}
