package com.gregtechceu.gtceu.api.machine.feature;

import com.gregtechceu.gtceu.api.GTValues;

/**
 * @author KilaBash
 * @date 2023/2/18
 * @implNote ITieredMachine
 */
public interface ITieredMachine extends IMachineFeature {

    /**
     * Tier of machine determines it's input voltage, storage and generation rate
     *
     * @return tier of this machine
     */
    default int getTier() {
        return self().getDefinition().getTier();
    }

    default long getMaxVoltage() {
        return GTValues.V[getTier()];
    }
}
