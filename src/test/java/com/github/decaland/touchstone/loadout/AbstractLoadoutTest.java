package com.github.decaland.touchstone.loadout;

import com.github.decaland.touchstone.loadout.layers.Layer;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractLoadoutTest<T extends Loadout> {

    protected static final String PLUGIN_ID = "test-plugin-id";

    protected abstract Loadout.Builder supplyBuilder();

    protected abstract Class<T> supplyLoadoutClass();

    @Test
    public void givenLoadoutBuilder_whenLoadoutIsBuilt_thenItIsOfExpectedType() {
        assertThat(supplyBuilder().build().getClass()).isSameAs(supplyLoadoutClass());
    }

    @Test
    public void givenLoadoutBuilder_whenLoadoutBuiltWithNoLayers_thenItContainsNoLayers() {
        // When
        Loadout loadout = supplyBuilder().build();

        // Then
        assertThat(loadout.layers().stream().collect(Collectors.toList())).isEmpty();
    }

    @Test
    public void givenLoadoutBuilder_whenLoadoutBuiltWithOneLayer_thenItContainsOnlyThatLayer() {
        // Given
        Layer layer = mock(Layer.class);

        // When
        Loadout loadout = supplyBuilder().add(layer).build();

        // Then
        assertThat(loadout.layers().stream().collect(Collectors.toList()))
                .containsExactlyInAnyOrder(layer);
    }

    @Test
    public void givenLoadoutBuilder_whenLoadoutBuiltWithMultipleLayers_thenItContainsOnlyThoseLayers() {
        // Given
        Layer layerA = mock(Layer.class);
        Layer layerB = mock(Layer.class);

        // When
        Loadout loadout = supplyBuilder().add(layerA).add(layerB).build();

        // Then
        assertThat(loadout.layers().stream().collect(Collectors.toList()))
                .containsExactlyInAnyOrder(layerA, layerB);
    }

    @Test
    public void givenLoadoutBuiltWithOneLayer_whenLoadoutIsApplied_thenOnlyThatLayerIsApplied() {
        // Given
        Layer layer = mock(Layer.class);
        Loadout loadout = supplyBuilder().add(layer).build();
        Project project = ProjectBuilder.builder().build();
        when(layer.isReady(eq(project), eq(loadout.layers()))).thenReturn(true);

        // When
        loadout.apply(project, PLUGIN_ID);

        // Then
        verify(layer).apply(eq(project), eq(loadout.layers()));
        verify(layer).configure(eq(project), eq(loadout.layers()));
        verifyNoMoreInteractions(layer);
    }

    @Test
    public void givenLoadoutBuiltWithMultipleLayers_whenLoadoutIsApplied_thenOnlyThoseLayersAreApplied() {
        // Given
        Layer layerA = mock(Layer.class);
        Layer layerB = mock(Layer.class);
        Loadout loadout = supplyBuilder().add(layerA).add(layerB).build();
        Project project = ProjectBuilder.builder().build();
        when(layerA.isReady(eq(project), eq(loadout.layers()))).thenReturn(true);
        when(layerB.isReady(eq(project), eq(loadout.layers()))).thenReturn(true);

        // When
        loadout.apply(project, PLUGIN_ID);

        // Then
        verify(layerA).apply(eq(project), eq(loadout.layers()));
        verify(layerA).configure(eq(project), eq(loadout.layers()));
        verifyNoMoreInteractions(layerA);

        verify(layerB).apply(eq(project), eq(loadout.layers()));
        verify(layerB).configure(eq(project), eq(loadout.layers()));
        verifyNoMoreInteractions(layerB);
    }

    @Test
    public void givenLoadoutWithMultipleReadilyApplicableLayers_whenLoadoutIsApplied_thenLayersAreAppliedInOrderAdded() {
        // Given
        Layer layerA = mock(Layer.class);
        Layer layerB = mock(Layer.class);
        Layer layerC = mock(Layer.class);
        Loadout loadout = supplyBuilder().add(layerB).add(layerC).add(layerA).build();  // note the order
        Project project = ProjectBuilder.builder().build();
        when(layerA.isReady(eq(project), eq(loadout.layers()))).thenReturn(true);
        when(layerB.isReady(eq(project), eq(loadout.layers()))).thenReturn(true);
        when(layerC.isReady(eq(project), eq(loadout.layers()))).thenReturn(true);
        InOrder inOrder = inOrder(layerA, layerB, layerC);

        // When
        loadout.apply(project, PLUGIN_ID);

        // Then
        inOrder.verify(layerB).isReady(eq(project), eq(loadout.layers()));
        inOrder.verify(layerB).apply(eq(project), eq(loadout.layers()));

        inOrder.verify(layerC).isReady(eq(project), eq(loadout.layers()));
        inOrder.verify(layerC).apply(eq(project), eq(loadout.layers()));

        inOrder.verify(layerA).isReady(eq(project), eq(loadout.layers()));
        inOrder.verify(layerA).apply(eq(project), eq(loadout.layers()));

        inOrder.verify(layerB).configure(eq(project), eq(loadout.layers()));
        inOrder.verify(layerC).configure(eq(project), eq(loadout.layers()));
        inOrder.verify(layerA).configure(eq(project), eq(loadout.layers()));

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void givenLoadoutWithTwoLayersWithInterdependency_whenLoadoutIsApplied_thenLayersAreAppliedInCorrectOrder() {
        // Given
        Layer layerA = mock(Layer.class);
        Layer layerB = mock(Layer.class);
        Loadout loadout = supplyBuilder().add(layerA).add(layerB).build();  // note the order
        Project project = ProjectBuilder.builder().build();
        when(layerA.isReady(eq(project), eq(loadout.layers()))).thenReturn(false).thenReturn(true);
        when(layerB.isReady(eq(project), eq(loadout.layers()))).thenReturn(true);
        InOrder inOrder = inOrder(layerA, layerB);

        // When
        loadout.apply(project, PLUGIN_ID);

        // Then
        inOrder.verify(layerB).isReady(eq(project), eq(loadout.layers()));
        inOrder.verify(layerB).apply(eq(project), eq(loadout.layers()));

        inOrder.verify(layerA).isReady(eq(project), eq(loadout.layers()));
        inOrder.verify(layerA).apply(eq(project), eq(loadout.layers()));

        inOrder.verify(layerB).configure(eq(project), eq(loadout.layers()));
        inOrder.verify(layerA).configure(eq(project), eq(loadout.layers()));

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void givenLoadoutWithAnInapplicableLayer_whenLoadoutIsApplied_thenExceptionIsThrown() {
        // Given
        Layer layerA = mock(Layer.class);
        Layer layerB = mock(Layer.class);
        Layer layerC = mock(Layer.class);
        Loadout loadout = supplyBuilder().add(layerB).add(layerC).add(layerA).build();  // note the order
        Project project = ProjectBuilder.builder().build();
        when(layerA.isReady(eq(project), eq(loadout.layers()))).thenReturn(true);
        when(layerB.isReady(eq(project), eq(loadout.layers()))).thenReturn(false);
        when(layerC.isReady(eq(project), eq(loadout.layers()))).thenReturn(true);
        InOrder inOrder = inOrder(layerA, layerB, layerC);
        Exception savedException = null;

        // When
        try {
            loadout.apply(project, PLUGIN_ID);
        } catch (Exception caughtException) {
            savedException = caughtException;
        }

        // Then
        assertThat(savedException).isNotNull();
        assertThat(savedException.getClass()).isSameAs(GradleException.class);

        inOrder.verify(layerB).isReady(eq(project), eq(loadout.layers()));

        inOrder.verify(layerC).isReady(eq(project), eq(loadout.layers()));
        inOrder.verify(layerC).apply(eq(project), eq(loadout.layers()));

        inOrder.verify(layerA).isReady(eq(project), eq(loadout.layers()));
        inOrder.verify(layerA).apply(eq(project), eq(loadout.layers()));

        inOrder.verify(layerB).isReady(eq(project), eq(loadout.layers()));

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void givenLoadout_whenItIsAppliedMultipleTimes_thenLayersAreAppliedThatNumberOfTimes() {
        // Given
        Layer layerA = mock(Layer.class);
        Layer layerB = mock(Layer.class);

        Loadout loadout = supplyBuilder().add(layerA).add(layerB).build();
        Project project = ProjectBuilder.builder().build();

        when(layerA.isReady(eq(project), eq(loadout.layers()))).thenReturn(true);
        when(layerB.isReady(eq(project), eq(loadout.layers()))).thenReturn(true);

        InOrder inOrder = inOrder(layerA, layerB);

        // When
        loadout.apply(project, PLUGIN_ID);
        loadout.apply(project, PLUGIN_ID);

        // Then
        inOrder.verify(layerA).isReady(eq(project), eq(loadout.layers()));
        inOrder.verify(layerA).apply(eq(project), eq(loadout.layers()));
        inOrder.verify(layerB).isReady(eq(project), eq(loadout.layers()));
        inOrder.verify(layerB).apply(eq(project), eq(loadout.layers()));
        inOrder.verify(layerA).configure(eq(project), eq(loadout.layers()));
        inOrder.verify(layerB).configure(eq(project), eq(loadout.layers()));

        inOrder.verify(layerA).isReady(eq(project), eq(loadout.layers()));
        inOrder.verify(layerA).apply(eq(project), eq(loadout.layers()));
        inOrder.verify(layerB).isReady(eq(project), eq(loadout.layers()));
        inOrder.verify(layerB).apply(eq(project), eq(loadout.layers()));
        inOrder.verify(layerA).configure(eq(project), eq(loadout.layers()));
        inOrder.verify(layerB).configure(eq(project), eq(loadout.layers()));

        inOrder.verifyNoMoreInteractions();
    }
}
