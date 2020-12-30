package com.github.decaland.touchstone.loadout.layers.configs;

import com.github.decaland.touchstone.loadout.layers.LayerAccumulator;
import org.gradle.api.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DependencyManagementLayerTest {

    @Mock
    private Project project;

    @Mock
    private LayerAccumulator.Finalized finalizedLayers;

    private DependencyManagementLayer layer;

    @BeforeEach
    void setUp() {
        layer = new DependencyManagementLayer(project, finalizedLayers);
    }

    @Test
    void givenLayer_whenMarkAppliedIsUsed_thenItWorksCorrectly() {
        assertThat(layer.isApplied()).isFalse();
        layer.markAsApplied();
        assertThat(layer.isApplied()).isTrue();
        layer.markAsApplied();
        assertThat(layer.isApplied()).isTrue();
    }
    
}
