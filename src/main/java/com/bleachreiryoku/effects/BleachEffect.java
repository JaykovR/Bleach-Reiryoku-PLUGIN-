package com.bleachreiryoku.effects;


public enum BleachEffect {


    HOLLOW_MASK(
        new EffectSlot("Items/Armors/Mask/Hollow_Mask.blockymodel", "Items/Armors/Mask/Hollow_Mask.png")
    ),

    // For Tensa Zangetsu Outfit
    // TODO: PUT ACTUAL LOCATION OF TENSA ZANGETSU ARMOR
    TENSA_ZANGETSU_OUTFIT(
        new EffectSlot("Bleach/TensaZangetsu/Overtop",  "Bleach/TensaZangetsu/Overtop")
    );

    private final EffectSlot[] slots;

    BleachEffect(EffectSlot... slots) {
        this.slots = slots;
    }

    public EffectSlot[] getSlots() {
        return slots;
    }
}
