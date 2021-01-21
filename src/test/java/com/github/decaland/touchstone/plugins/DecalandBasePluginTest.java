package com.github.decaland.touchstone.plugins;

import com.github.decaland.touchstone.loadout.Loadout;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.util.GradleVersion;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DecalandBasePluginTest {

    @Test
    public void givenPluginWithMinimumGradleVersionHigherThanActual_whenPluginIsApplied_thenExceptionIsThrown() {
        // Given
        Loadout loadout = mock(Loadout.class);
        DecalandPlugin plugin = new DecalandBasePluginTestImpl(loadout) {
            @NotNull
            @Override
            public GradleVersion getMinimumGradleVersion() {
                return GradleVersion.current().getNextMajor();
            }
        };
        Project project = ProjectBuilder.builder().build();
        Exception caughtException = null;

        // When
        try {
            plugin.apply(project);
        } catch (Exception e) {
            caughtException = e;
        }

        // Then
        verifyNoInteractions(loadout);
        assertThat(caughtException).isNotNull();
        assertThat(caughtException.getClass()).isSameAs(GradleException.class);
    }

    @Test
    public void givenPluginWithMinimumGradleVersionEqualToActual_whenPluginIsApplied_thenExceptionIsNotThrown() {
        // Given
        Loadout loadout = mock(Loadout.class);
        DecalandPlugin plugin = new DecalandBasePluginTestImpl(loadout) {
            @NotNull
            @Override
            public GradleVersion getMinimumGradleVersion() {
                return GradleVersion.current();
            }
        };
        Project project = ProjectBuilder.builder().build();
        Exception caughtException = null;

        // When
        try {
            plugin.apply(project);
        } catch (Exception e) {
            caughtException = e;
        }

        // Then
        verify(loadout).apply(eq(project), anyString());
        verifyNoMoreInteractions(loadout);

        assertThat(caughtException).isNull();
    }

    @Test
    public void givenPluginWithMinimumGradleVersionLessThanOrEqualToActual_whenPluginIsApplied_thenExceptionIsNotThrown() {
        // Given
        Loadout loadout = mock(Loadout.class);
        DecalandPlugin plugin = new DecalandBasePluginTestImpl(loadout) {
            @NotNull
            @Override
            public GradleVersion getMinimumGradleVersion() {
                return GradleVersion.current().getBaseVersion();
            }
        };
        Project project = ProjectBuilder.builder().build();
        Exception caughtException = null;

        // When
        try {
            plugin.apply(project);
        } catch (Exception e) {
            caughtException = e;
        }

        // Then
        verify(loadout).apply(eq(project), anyString());
        verifyNoMoreInteractions(loadout);

        assertThat(caughtException).isNull();
    }

    @Test
    public void givenPlugin_whenLoadoutIsRequestedMultipleTimes_thenTheSameInstanceIsReturned() {
        // Given
        DecalandPlugin plugin = new DecalandBasePluginTestImpl(mock(Loadout.class));

        // When
        Loadout loadoutA = plugin.getLoadout();
        Loadout loadoutB = plugin.getLoadout();
        Loadout loadoutC = plugin.getLoadout();

        // Then
        assertThat(loadoutA).isSameAs(loadoutB);
        assertThat(loadoutB).isSameAs(loadoutC);
        assertThat(loadoutC).isSameAs(loadoutA);
    }

    @Test
    public void givenApplicablePluginWithLoadout_whenPluginIsApplied_thenThatLoadoutIsApplied() {
        // Given
        Loadout loadout = mock(Loadout.class);
        DecalandPlugin plugin = new DecalandBasePluginTestImpl(loadout);
        Project project = ProjectBuilder.builder().build();

        // When
        plugin.apply(project);

        // Then
        verify(loadout).apply(eq(project), anyString());
        verifyNoMoreInteractions(loadout);
    }

    @Test
    public void givenApplicablePluginWithLoadout_whenPluginIsAppliedMultipleTimes_thenThatLoadoutIsAppliedThatNumberOfTimes() {
        // Given
        Loadout loadout = mock(Loadout.class);
        DecalandPlugin plugin = new DecalandBasePluginTestImpl(loadout);
        Project project = ProjectBuilder.builder().build();

        // When
        plugin.apply(project);
        plugin.apply(project);
        plugin.apply(project);

        // Then
        verify(loadout, times(3)).apply(eq(project), anyString());
        verifyNoMoreInteractions(loadout);
    }

    @Test
    public void givenPluginWithAnyRequiredUnsatisfied_whenPluginIsApplied_thenExceptionIsThrown() {
        // Given
        Loadout loadout = mock(Loadout.class);
        DecalandPlugin plugin = new DecalandBasePluginTestImpl(loadout) {
            @NotNull
            @Override
            public Collection<Class<? extends DecalandPlugin>> getAnyRequiredPlugins() {
                return List.of(DecalandBasePluginDummyA.class, DecalandBasePluginDummyB.class);
            }
        };
        Project project = ProjectBuilder.builder().build();
        Exception caughtException = null;

        // When
        try {
            plugin.apply(project);
        } catch (Exception e) {
            caughtException = e;
        }

        // Then
        verifyNoInteractions(loadout);

        assertThat(caughtException).isNotNull();
        assertThat(caughtException.getClass()).isSameAs(GradleException.class);
    }

    @Test
    public void givenPluginWithAnyRequiredSatisfied_whenPluginIsApplied_thenExceptionIsNotThrown() {
        // Given
        Loadout loadout = mock(Loadout.class);
        DecalandPlugin plugin = new DecalandBasePluginTestImpl(loadout) {
            @NotNull
            @Override
            public Collection<Class<? extends DecalandPlugin>> getAnyRequiredPlugins() {
                return List.of(DecalandBasePluginDummyA.class, DecalandBasePluginDummyB.class);
            }
        };
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply(DecalandBasePluginDummyB.class);
        Exception caughtException = null;

        // When
        try {
            plugin.apply(project);
        } catch (Exception e) {
            caughtException = e;
        }

        // Then
        verify(loadout).apply(eq(project), anyString());
        verifyNoMoreInteractions(loadout);

        assertThat(caughtException).isNull();
    }

    @Test
    public void givenPluginWithAllRequiredUnsatisfied_whenPluginIsApplied_thenExceptionIsThrown() {
        // Given
        Loadout loadout = mock(Loadout.class);
        DecalandPlugin plugin = new DecalandBasePluginTestImpl(loadout) {
            @NotNull
            @Override
            public Collection<Class<? extends DecalandPlugin>> getAllRequiredPlugins() {
                return List.of(DecalandBasePluginDummyA.class, DecalandBasePluginDummyB.class);
            }
        };
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply(DecalandBasePluginDummyB.class);
        Exception caughtException = null;

        // When
        try {
            plugin.apply(project);
        } catch (Exception e) {
            caughtException = e;
        }

        // Then
        verifyNoInteractions(loadout);

        assertThat(caughtException).isNotNull();
        assertThat(caughtException.getClass()).isSameAs(GradleException.class);
    }

    @Test
    public void givenPluginWithAllRequiredSatisfied_whenPluginIsApplied_thenExceptionIsNotThrown() {
        // Given
        Loadout loadout = mock(Loadout.class);
        DecalandPlugin plugin = new DecalandBasePluginTestImpl(loadout) {
            @NotNull
            @Override
            public Collection<Class<? extends DecalandPlugin>> getAllRequiredPlugins() {
                return List.of(DecalandBasePluginDummyA.class, DecalandBasePluginDummyB.class);
            }
        };
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply(DecalandBasePluginDummyA.class);
        project.getPluginManager().apply(DecalandBasePluginDummyB.class);
        Exception caughtException = null;

        // When
        try {
            plugin.apply(project);
        } catch (Exception e) {
            caughtException = e;
        }

        // Then
        verify(loadout).apply(eq(project), anyString());
        verifyNoMoreInteractions(loadout);

        assertThat(caughtException).isNull();
    }

    @Test
    public void givenPluginWithIncompatibleUnsatisfied_whenPluginIsApplied_thenExceptionIsThrown() {
        // Given
        Loadout loadout = mock(Loadout.class);
        DecalandPlugin plugin = new DecalandBasePluginTestImpl(loadout) {
            @NotNull
            @Override
            public Collection<Class<? extends DecalandPlugin>> getIncompatiblePlugins() {
                return List.of(DecalandBasePluginDummyA.class, DecalandBasePluginDummyB.class);
            }
        };
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply(DecalandBasePluginDummyB.class);
        Exception caughtException = null;

        // When
        try {
            plugin.apply(project);
        } catch (Exception e) {
            caughtException = e;
        }

        // Then
        verifyNoInteractions(loadout);

        assertThat(caughtException).isNotNull();
        assertThat(caughtException.getClass()).isSameAs(GradleException.class);
    }

    @Test
    public void givenPluginWithIncompatibleSatisfied_whenPluginIsApplied_thenExceptionIsNotThrown() {
        // Given
        Loadout loadout = mock(Loadout.class);
        DecalandPlugin plugin = new DecalandBasePluginTestImpl(loadout) {
            @NotNull
            @Override
            public Collection<Class<? extends DecalandPlugin>> getIncompatiblePlugins() {
                return List.of(DecalandBasePluginDummyA.class, DecalandBasePluginDummyB.class);
            }
        };
        Project project = ProjectBuilder.builder().build();
        Exception caughtException = null;

        // When
        try {
            plugin.apply(project);
        } catch (Exception e) {
            caughtException = e;
        }

        // Then
        verify(loadout).apply(eq(project), anyString());
        verifyNoMoreInteractions(loadout);

        assertThat(caughtException).isNull();
    }

    private static class DecalandBasePluginTestImpl extends DecalandBasePlugin {

        private final Loadout innerLoadout;

        public DecalandBasePluginTestImpl(Loadout innerLoadout) {
            this.innerLoadout = innerLoadout;
        }

        @NotNull
        @Override
        public GradleVersion getMinimumGradleVersion() {
            return GradleVersion.current();
        }

        @NotNull
        @Override
        protected Loadout supplyLoadout() {
            return innerLoadout;
        }

        @NotNull
        @Override
        protected Class<? extends DecalandPlugin> getPluginType() {
            return DecalandBasePluginTestImpl.class;
        }
    }

    private static class DecalandBasePluginDummyA extends DecalandBasePlugin {

        public DecalandBasePluginDummyA() {
        }

        @NotNull
        @Override
        protected Loadout supplyLoadout() {
            return Loadout.builder().build();
        }

        @NotNull
        @Override
        protected Class<? extends DecalandPlugin> getPluginType() {
            return DecalandBasePluginDummyA.class;
        }
    }

    private static class DecalandBasePluginDummyB extends DecalandBasePlugin {

        public DecalandBasePluginDummyB() {
        }

        @NotNull
        @Override
        protected Loadout supplyLoadout() {
            return Loadout.builder().build();
        }

        @NotNull
        @Override
        protected Class<? extends DecalandPlugin> getPluginType() {
            return DecalandBasePluginDummyB.class;
        }
    }
}
