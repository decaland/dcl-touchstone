package com.github.decaland.touchstone.loadout.layers.releasing.services.steps;

import com.github.decaland.touchstone.utils.scm.git.GitAdapter;
import org.gradle.api.GradleException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public abstract class ReleaseStep {

    protected final GitAdapter git;

    @Contract(pure = true)
    public ReleaseStep(@NotNull GitAdapter git) {
        this.git = git;
    }

    synchronized public final void perform() {
        try {
            doPerform();
        } catch (Exception exception) {
            handleException(exception, true);
        }
    }

    protected abstract void doPerform();

    protected abstract @NotNull Class<? extends ReleaseStep> getCurrentClass();

    public abstract @NotNull String getStepDescription();

    protected final void handleException(@NotNull Exception exception, boolean whilePerforming) {
        boolean isGradleException = (exception instanceof GradleException);
        StringBuilder message = new StringBuilder();
        if (isGradleException) {
            message.append(whilePerforming ? "Failed to perform" : "Failed to revert");
        } else {
            message.append(String.format(
                    "Caught %s while %s",
                    exception.getClass().getSimpleName(),
                    whilePerforming ? "performing" : "reverting"
            ));
        }
        message.append(String.format(
                " release step %s: %s",
                getCurrentClass().getSimpleName(),
                exception.getMessage()
        ));
        if (isGradleException) {
            throw new GradleException(message.toString(), exception);
        } else {
            throw new RuntimeException(message.toString(), exception);
        }
    }
}
