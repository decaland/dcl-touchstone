package com.github.decaland.touchstone.plugins;

import com.github.decaland.touchstone.loadout.Loadout;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.util.GradleVersion;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public abstract class DecalandPluginTest<T extends DecalandPlugin> {

    @NotNull
    public abstract Class<T> supplyPluginClass();

    @NotNull
    public abstract T supplyDefaultPluginInstance();

    @Test
    public void givenPluginTestClass_whenDefaultPluginInstanceIsSupplied_thenItIsOfExpectedType() {
        assertThat(supplyDefaultPluginInstance().getClass()).isSameAs(supplyPluginClass());
    }

    @Test
    public void givenPluginWithMinimumGradleVersionHigherThanActual_whenPluginIsApplied_thenExceptionIsThrown() {
        // Given
        T plugin = spy(supplyDefaultPluginInstance());
        GradleVersion actualVersion = GradleVersion.current();
        GradleVersion minimumVersion = actualVersion.getNextMajor();

        when(plugin.getAnyRequiredPlugins()).thenReturn(List.of());
        when(plugin.getAllRequiredPlugins()).thenReturn(List.of());
        when(plugin.getIncompatiblePlugins()).thenReturn(List.of());

        when(plugin.getMinimumGradleVersion()).thenReturn(minimumVersion);
        Project project = mock(Project.class, RETURNS_MOCKS);

        Loadout loadout = mock(Loadout.class);
        when(plugin.supplyLoadout()).thenReturn(loadout);

        Exception savedException = null;

        // When
        try {
            plugin.apply(project);
        } catch (Exception caughtException) {
            savedException = caughtException;
        }

        // Then
        verifyNoInteractions(loadout);

        assertThat(savedException).isNotNull();
        assertThat(savedException.getClass()).isSameAs(GradleException.class);
    }

    @Test
    public void givenPluginWithMinimumGradleVersionEqualToActual_whenPluginIsApplied_thenExceptionIsNotThrown() {
        // Given
        T plugin = spy(supplyDefaultPluginInstance());
        GradleVersion actualVersion = GradleVersion.current();

        when(plugin.getAnyRequiredPlugins()).thenReturn(List.of());
        when(plugin.getAllRequiredPlugins()).thenReturn(List.of());
        when(plugin.getIncompatiblePlugins()).thenReturn(List.of());

        when(plugin.getMinimumGradleVersion()).thenReturn(actualVersion);
        Project project = mock(Project.class, RETURNS_MOCKS);

        Loadout loadout = mock(Loadout.class);
        when(plugin.supplyLoadout()).thenReturn(loadout);

        Exception savedException = null;

        // When
        try {
            plugin.apply(project);
        } catch (Exception caughtException) {
            savedException = caughtException;
        }

        // Then
        verify(loadout).apply(eq(project), anyString());
        verifyNoMoreInteractions(loadout);

        assertThat(savedException).isNull();
    }

    @Test
    public void givenPluginWithMinimumGradleVersionLessThanOrEqualToActual_whenPluginIsApplied_thenExceptionIsNotThrown() {
        // Given
        T plugin = spy(supplyDefaultPluginInstance());
        GradleVersion actualVersion = GradleVersion.current();
        GradleVersion minimumVersion = actualVersion.getBaseVersion();

        when(plugin.getAnyRequiredPlugins()).thenReturn(List.of());
        when(plugin.getAllRequiredPlugins()).thenReturn(List.of());
        when(plugin.getIncompatiblePlugins()).thenReturn(List.of());

        when(plugin.getMinimumGradleVersion()).thenReturn(minimumVersion);
        Project project = mock(Project.class, RETURNS_MOCKS);

        Loadout loadout = mock(Loadout.class);
        when(plugin.supplyLoadout()).thenReturn(loadout);

        Exception savedException = null;

        // When
        try {
            plugin.apply(project);
        } catch (Exception caughtException) {
            savedException = caughtException;
        }

        // Then
        verify(loadout).apply(eq(project), anyString());
        verifyNoMoreInteractions(loadout);

        assertThat(savedException).isNull();
    }

    @Test
    public void givenPluginWithAnyRequiredUnsatisfied_whenPluginIsApplied_thenExceptionIsThrown() {
        // Given
        T plugin = spy(supplyDefaultPluginInstance());
        when(plugin.getMinimumGradleVersion()).thenReturn(GradleVersion.current());

        when(plugin.getAnyRequiredPlugins()).thenReturn(List.of(PluginA.class, PluginB.class));
        when(plugin.getAllRequiredPlugins()).thenReturn(List.of());
        when(plugin.getIncompatiblePlugins()).thenReturn(List.of());

        Project project = mock(Project.class, RETURNS_MOCKS);
        PluginContainer plugins = mock(PluginContainer.class);
        when(project.getPlugins()).thenReturn(plugins);
        when(plugins.hasPlugin(PluginA.class)).thenReturn(false);
        when(plugins.hasPlugin(PluginB.class)).thenReturn(false);

        Loadout loadout = mock(Loadout.class);
        when(plugin.supplyLoadout()).thenReturn(loadout);

        Exception savedException = null;

        // When
        try {
            plugin.apply(project);
        } catch (Exception caughtException) {
            savedException = caughtException;
        }

        // Then
        verifyNoInteractions(loadout);

        assertThat(savedException).isNotNull();
        assertThat(savedException.getClass()).isSameAs(GradleException.class);
    }

    @Test
    public void givenPluginWithAnyRequiredSatisfied_whenPluginIsApplied_thenExceptionIsNotThrown() {
        // Given
        T plugin = spy(supplyDefaultPluginInstance());
        when(plugin.getMinimumGradleVersion()).thenReturn(GradleVersion.current());

        when(plugin.getAnyRequiredPlugins()).thenReturn(List.of(PluginA.class, PluginB.class));
        when(plugin.getAllRequiredPlugins()).thenReturn(List.of());
        when(plugin.getIncompatiblePlugins()).thenReturn(List.of());

        Project project = mock(Project.class, RETURNS_MOCKS);
        PluginContainer plugins = mock(PluginContainer.class);
        when(project.getPlugins()).thenReturn(plugins);
        when(plugins.hasPlugin(PluginA.class)).thenReturn(false);
        when(plugins.hasPlugin(PluginB.class)).thenReturn(true);

        Loadout loadout = mock(Loadout.class);
        when(plugin.supplyLoadout()).thenReturn(loadout);

        Exception savedException = null;

        // When
        try {
            plugin.apply(project);
        } catch (Exception caughtException) {
            savedException = caughtException;
        }

        // Then
        verify(loadout).apply(eq(project), anyString());
        verifyNoMoreInteractions(loadout);

        assertThat(savedException).isNull();
    }

    @Test
    public void givenPluginWithAllRequiredUnsatisfied_whenPluginIsApplied_thenExceptionIsThrown() {
        // Given
        T plugin = spy(supplyDefaultPluginInstance());
        when(plugin.getMinimumGradleVersion()).thenReturn(GradleVersion.current());

        when(plugin.getAnyRequiredPlugins()).thenReturn(List.of());
        when(plugin.getAllRequiredPlugins()).thenReturn(List.of(PluginA.class, PluginB.class));
        when(plugin.getIncompatiblePlugins()).thenReturn(List.of());

        Project project = mock(Project.class, RETURNS_MOCKS);
        PluginContainer plugins = mock(PluginContainer.class);
        when(project.getPlugins()).thenReturn(plugins);
        when(plugins.hasPlugin(PluginA.class)).thenReturn(true);
        when(plugins.hasPlugin(PluginB.class)).thenReturn(false);

        Loadout loadout = mock(Loadout.class);
        when(plugin.supplyLoadout()).thenReturn(loadout);

        Exception savedException = null;

        // When
        try {
            plugin.apply(project);
        } catch (Exception caughtException) {
            savedException = caughtException;
        }

        // Then
        verifyNoInteractions(loadout);

        assertThat(savedException).isNotNull();
        assertThat(savedException.getClass()).isSameAs(GradleException.class);
    }

    @Test
    public void givenPluginWithAllRequiredSatisfied_whenPluginIsApplied_thenExceptionIsNotThrown() {
        // Given
        T plugin = spy(supplyDefaultPluginInstance());
        when(plugin.getMinimumGradleVersion()).thenReturn(GradleVersion.current());

        when(plugin.getAnyRequiredPlugins()).thenReturn(List.of());
        when(plugin.getAllRequiredPlugins()).thenReturn(List.of(PluginA.class, PluginB.class));
        when(plugin.getIncompatiblePlugins()).thenReturn(List.of());

        Project project = mock(Project.class, RETURNS_MOCKS);
        PluginContainer plugins = mock(PluginContainer.class);
        when(project.getPlugins()).thenReturn(plugins);
        when(plugins.hasPlugin(PluginA.class)).thenReturn(true);
        when(plugins.hasPlugin(PluginB.class)).thenReturn(true);

        Loadout loadout = mock(Loadout.class);
        when(plugin.supplyLoadout()).thenReturn(loadout);

        Exception savedException = null;

        // When
        try {
            plugin.apply(project);
        } catch (Exception caughtException) {
            savedException = caughtException;
        }

        // Then
        verify(loadout).apply(eq(project), anyString());
        verifyNoMoreInteractions(loadout);

        assertThat(savedException).isNull();
    }

    @Test
    public void givenPluginWithIncompatibleUnsatisfied_whenPluginIsApplied_thenExceptionIsThrown() {
        // Given
        T plugin = spy(supplyDefaultPluginInstance());
        when(plugin.getMinimumGradleVersion()).thenReturn(GradleVersion.current());

        when(plugin.getAnyRequiredPlugins()).thenReturn(List.of());
        when(plugin.getAllRequiredPlugins()).thenReturn(List.of());
        when(plugin.getIncompatiblePlugins()).thenReturn(List.of(PluginA.class, PluginB.class));

        Project project = mock(Project.class, RETURNS_MOCKS);
        PluginContainer plugins = mock(PluginContainer.class);
        when(project.getPlugins()).thenReturn(plugins);
        when(plugins.hasPlugin(PluginA.class)).thenReturn(true);
        when(plugins.hasPlugin(PluginB.class)).thenReturn(false);

        Loadout loadout = mock(Loadout.class);
        when(plugin.supplyLoadout()).thenReturn(loadout);

        Exception savedException = null;

        // When
        try {
            plugin.apply(project);
        } catch (Exception caughtException) {
            savedException = caughtException;
        }

        // Then
        verifyNoInteractions(loadout);

        assertThat(savedException).isNotNull();
        assertThat(savedException.getClass()).isSameAs(GradleException.class);
    }

    @Test
    public void givenPluginWithIncompatibleSatisfied_whenPluginIsApplied_thenExceptionIsNotThrown() {
        // Given
        T plugin = spy(supplyDefaultPluginInstance());
        when(plugin.getMinimumGradleVersion()).thenReturn(GradleVersion.current());

        when(plugin.getAnyRequiredPlugins()).thenReturn(List.of());
        when(plugin.getAllRequiredPlugins()).thenReturn(List.of());
        when(plugin.getIncompatiblePlugins()).thenReturn(List.of(PluginA.class, PluginB.class));

        Project project = mock(Project.class, RETURNS_MOCKS);
        PluginContainer plugins = mock(PluginContainer.class);
        when(project.getPlugins()).thenReturn(plugins);
        when(plugins.hasPlugin(PluginA.class)).thenReturn(false);
        when(plugins.hasPlugin(PluginB.class)).thenReturn(false);

        Loadout loadout = mock(Loadout.class);
        when(plugin.supplyLoadout()).thenReturn(loadout);

        Exception savedException = null;

        // When
        try {
            plugin.apply(project);
        } catch (Exception caughtException) {
            savedException = caughtException;
        }

        // Then
        verify(loadout).apply(eq(project), anyString());
        verifyNoMoreInteractions(loadout);

        assertThat(savedException).isNull();
    }

    @Test
    public void givenApplicablePluginWithLoadout_whenPluginIsApplied_thenThatLoadoutIsApplied() {
        // Given
        T plugin = spy(supplyDefaultPluginInstance());

        when(plugin.getMinimumGradleVersion()).thenReturn(GradleVersion.current());
        when(plugin.getAnyRequiredPlugins()).thenReturn(List.of());
        when(plugin.getAllRequiredPlugins()).thenReturn(List.of());
        when(plugin.getIncompatiblePlugins()).thenReturn(List.of());

        Project project = mock(Project.class, RETURNS_MOCKS);

        Loadout loadout = mock(Loadout.class);
        when(plugin.supplyLoadout()).thenReturn(loadout);

        // When
        plugin.apply(project);

        // Then
        verify(loadout).apply(eq(project), anyString());
        verifyNoMoreInteractions(loadout);
    }

    public static class PluginA implements DecalandPlugin {
        @Override
        public void apply(@NotNull Project target) {
        }

        @NotNull
        @Override
        public Loadout supplyLoadout() {
            return Loadout.builder().build();
        }

        @NotNull
        @Override
        public String getPluginId() {
            return "plugin-a";
        }

        @NotNull
        @Override
        public GradleVersion getMinimumGradleVersion() {
            return GradleVersion.current();
        }

        @NotNull
        @Override
        public Collection<Class<? extends DecalandPlugin>> getAnyRequiredPlugins() {
            return List.of();
        }

        @NotNull
        @Override
        public Collection<Class<? extends DecalandPlugin>> getAllRequiredPlugins() {
            return List.of();
        }

        @NotNull
        @Override
        public Collection<Class<? extends DecalandPlugin>> getIncompatiblePlugins() {
            return List.of();
        }
    }

    public static class PluginB implements DecalandPlugin {
        @Override
        public void apply(@NotNull Project target) {
        }

        @NotNull
        @Override
        public Loadout supplyLoadout() {
            return Loadout.builder().build();
        }

        @NotNull
        @Override
        public String getPluginId() {
            return "plugin-b";
        }

        @NotNull
        @Override
        public GradleVersion getMinimumGradleVersion() {
            return GradleVersion.current();
        }

        @NotNull
        @Override
        public Collection<Class<? extends DecalandPlugin>> getAnyRequiredPlugins() {
            return List.of();
        }

        @NotNull
        @Override
        public Collection<Class<? extends DecalandPlugin>> getAllRequiredPlugins() {
            return List.of();
        }

        @NotNull
        @Override
        public Collection<Class<? extends DecalandPlugin>> getIncompatiblePlugins() {
            return List.of();
        }
    }
}
