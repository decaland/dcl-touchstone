package com.github.decaland.touchstone.loadout.layers.releasing.services.steps;

import com.github.decaland.touchstone.utils.scm.git.GitAdapter;
import org.jetbrains.annotations.NotNull;

public abstract class ReversibleReleaseStep extends ReleaseStep {

    public ReversibleReleaseStep(@NotNull GitAdapter git) {
        super(git);
    }

    synchronized public final void revert() {
        try {
            doRevert();
        } catch (Exception exception) {
            handleException(exception, false);
        }
    }

    protected abstract void doRevert();

    public abstract @NotNull String getStepReversalDescription();
}
