package com.github.decaland.touchstone.loadout.layers.releasing.steps;

import org.gradle.api.GradleException;

public abstract class ReleaseStep {

    public final void revert() {
        try {
            doRevert();
        } catch (Exception exception) {
            handleException(exception, false);
        }
    }

    protected abstract void doRevert();

    protected abstract Class<? extends ReleaseStep> getCurrentClass();

    public abstract String getStepDescription();

    protected final void handleException(Exception exception, boolean whilePerforming) {
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
                " release step %s ('%s'): %s",
                getCurrentClass().getSimpleName(),
                getStepDescription(),
                exception.getMessage()
        ));
        if (isGradleException) {
            throw new GradleException(message.toString(), exception);
        } else {
            throw new RuntimeException(message.toString(), exception);
        }
    }
}
