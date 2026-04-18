package com.bleachreiryoku.effects;


public enum BleachEffect {


    HOLLOW_MASK(
        new EffectSlot("Items/Armors/Mask/Hollow_Mask.blockymodel", "Items/Armors/Mask/Hollow_Mask.png")
    ),

    TENSA_ZANGETSU_CHEST(
        new EffectSlot("Items/Armors/Shinigami_Robes_Captain/TensaZangetsu_Clothes.blockymodel", "Items/Armors/Shinigami_Robes_Captain/TensaZangetsu_Clothes_texture.png")
    );

    private final EffectSlot[] slots;

    BleachEffect(EffectSlot... slots) {
        this.slots = slots;
    }

    public EffectSlot[] getSlots() {
        return slots;
    }
}
