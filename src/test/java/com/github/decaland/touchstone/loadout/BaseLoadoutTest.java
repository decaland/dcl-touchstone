package com.github.decaland.touchstone.loadout;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseLoadoutTest extends AbstractLoadoutTest<BaseLoadout> {

    @Override
    public Loadout.Builder supplyBuilder() {
        return BaseLoadout.builder();
    }

    @Override
    public Class<BaseLoadout> supplyLoadoutClass() {
        return BaseLoadout.class;
    }

    @Test
    public void givenLoadoutInterface_whenDefaultBuilderImplementationIsRequested_thenThisImplementationIsSupplied() {
        assertThat(Loadout.builder().getClass()).isSameAs(supplyBuilder().getClass());
    }
}
