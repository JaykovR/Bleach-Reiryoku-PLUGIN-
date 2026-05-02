package com.bleachreiryoku.effects;


public enum BleachEffect {


    // No model attachment, uses head armor slot swap directly.
    HOLLOW_MASK(),

    SWAP_INTERACTION(),

    // No model attachment for this one. An item is used now.
    TENSA_ZANGETSU_CHEST();

    private final EffectSlot[] slots;

    BleachEffect(EffectSlot... slots) {
        this.slots = slots;
    }

    public EffectSlot[] getSlots() {
        return slots;
    }
}
