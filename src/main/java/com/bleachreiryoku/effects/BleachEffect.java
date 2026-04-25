package com.bleachreiryoku.effects;


public enum BleachEffect {


    HOLLOW_MASK(
        new EffectSlot("Items/Armors/Mask/Hollow_Mask.blockymodel", "Items/Armors/Mask/Hollow_Mask.png")
    ),

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
