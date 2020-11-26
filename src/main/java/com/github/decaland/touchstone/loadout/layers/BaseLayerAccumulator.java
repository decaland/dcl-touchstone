package com.github.decaland.touchstone.loadout.layers;

import org.gradle.api.GradleException;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

public final class BaseLayerAccumulator implements LayerAccumulator {

    private static final String MSG_REQUESTED_PENDING_AFTER_FINALIZED
            = "Touchstone plugin requested pending list of layers after loadout is already built";
    private static final String MSG_REQUESTED_FINALIZED_AFTER_FINALIZED
            = "Touchstone plugin requested to finalize list of layers after loadout is already built";

    private final Set<Layer> layerSet = new HashSet<>();
    private final Finalized finalized = new BaseFinalized();
    private boolean isFinalized = false;
    private LayerNode firstLayer = null;
    private LayerNode lastLayer = null;

    public Finalized pending() {
        if (isFinalized) {
            throw new GradleException(MSG_REQUESTED_PENDING_AFTER_FINALIZED);
        }
        return finalized;
    }

    public Finalized finalized() {
        if (isFinalized) {
            throw new GradleException(MSG_REQUESTED_FINALIZED_AFTER_FINALIZED);
        }
        markAsFinalized();
        return finalized;
    }

    public void add(Layer layer) {
        if (layerSet.contains(layer)) return;
        if (lastLayer == null) {
            firstLayer = lastLayer = new LayerNode(layer);
        } else {
            LayerNode newNode = new LayerNode(layer, lastLayer, null);
            lastLayer.right = newNode;
            lastLayer = newNode;
        }
    }

    public <T extends Layer> void reconfigureMatching(Class<T> clazz, Consumer<T> reconfigurer) {
        LayerNode current = firstLayer;
        while (current != null) {
            if (clazz.isAssignableFrom(current.layer.getClass())) {
                reconfigurer.accept(clazz.cast(current.layer));
            }
            current = current.right;
        }
    }

    public void removeMatching(Class<? extends Layer> clazz) {
        LayerNode current = firstLayer;
        while (current != null) {
            if (clazz.isAssignableFrom(current.layer.getClass())) {
                layerSet.remove(current.layer);
                if (current.left != null) current.left.right = current.right;
                if (current.right != null) current.right.left = current.left;
                if (current == firstLayer) firstLayer = current.right;
                if (current == lastLayer) lastLayer = current.left;
            }
            current = current.right;
        }
    }

    public void swapMatching(Class<? extends Layer> clazz, Layer replacement) {
        swapMatchingInternal(clazz, replacement);
    }

    public void swapMatchingOrAdd(Class<? extends Layer> clazz, Layer replacement) {
        if (!swapMatchingInternal(clazz, replacement)) {
            add(replacement);
        }
    }

    private void markAsFinalized() {
        isFinalized = true;
    }

    private boolean swapMatchingInternal(Class<? extends Layer> clazz, Layer replacement) {
        boolean swapped = false;
        LayerNode current = firstLayer;
        while (current != null) {
            if (clazz.isAssignableFrom(current.layer.getClass())) {
                current.layer = replacement;
                swapped = true;
            }
            current = current.right;
        }
        return swapped;
    }

    private static final class LayerNode {

        private Layer layer;
        private LayerNode left = null;
        private LayerNode right = null;

        public LayerNode(Layer layer) {
            this.layer = layer;
        }

        public LayerNode(Layer layer, LayerNode left, LayerNode right) {
            this.layer = layer;
            this.left = left;
            this.right = right;
        }
    }

    private final class BaseFinalized implements LayerAccumulator.Finalized {

        private static final String MSG_UNFINALIZED_LAYERS_ACCESSED
                = "Touchstone plugin attempted to analyze loadout layers before they are finalized";

        private Set<Layer> finalizedLayerSet = null;

        @Override
        @NotNull
        public Set<Layer> asUnmodifiableLinkedSet() {
            ensureFinalizedSetIsPopulated();
            return finalizedLayerSet;
        }

        @Override
        public boolean contains(Class<? extends Layer> clazz) {
            ensureFinalizedSetIsPopulated();
            return finalizedLayerSet.stream().anyMatch(layer -> clazz.isAssignableFrom(layer.getClass()));
        }

        private void ensureFinalizedSetIsPopulated() {
            if (!isFinalized) {
                throw new GradleException(MSG_UNFINALIZED_LAYERS_ACCESSED);
            }
            if (finalizedLayerSet == null) {
                Set<Layer> linkedLayerSet = new LinkedHashSet<>();
                LayerNode current = firstLayer;
                while (current != null) {
                    linkedLayerSet.add(current.layer);
                    current = current.right;
                }
                finalizedLayerSet = Collections.unmodifiableSet(linkedLayerSet);
            }
        }
    }
}
