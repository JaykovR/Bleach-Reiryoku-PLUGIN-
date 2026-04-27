package com.bleachreiryoku.interactions;

import com.bleachreiryoku.effects.ActiveEffectsComponent;
import com.bleachreiryoku.effects.BleachEffect;
import com.bleachreiryoku.effects.BleachEffectService;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.ItemArmorSlot;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

// Applies and removes the Hollow Mask by swapping the head armor slot directly.
// Same as tensazangetsu interaction
// I'll make this a one interaction later, making them separate interactions it's kinda dumb.
public class HollowMaskInteraction extends SimpleInteraction {

    private static final String HOLLOW_MASK_ID = "Armor_Hollow_Mask";

    public static final BuilderCodec<HollowMaskInteraction> CODEC =
            BuilderCodec.builder(HollowMaskInteraction.class, HollowMaskInteraction::new, SimpleInteraction.CODEC)
                    .append(
                            new KeyedCodec<>("Duration", Codec.FLOAT),
                            (t, v) -> t.duration = v,
                            t -> t.duration
                    ).add()
                    .build();

    // Positive = timed. -1 = permanent toggle (default).
    private float duration = -1.0f;

    @Override
    protected void tick0(boolean firstRun, float time,
                         @NonNullDecl InteractionType type,
                         @NonNullDecl InteractionContext context,
                         @NonNullDecl CooldownHandler cooldownHandler) {

        if (!firstRun) return;

        Ref<EntityStore> ref = context.getOwningEntity();
        Store<EntityStore> store = ref.getStore();

        if (store.getComponent(ref, Player.getComponentType()) == null) return;

        var commandBuffer = context.getCommandBuffer();

        // Toggle off if already active
        if (BleachEffectService.hasEffect(ref, store, BleachEffect.HOLLOW_MASK)) {
            // EffectTickSystem will handle cleanup when the timer fires,
            // but for a toggle-off we trigger removal immediately via service.
            BleachEffectService.removeEffect(ref, store, commandBuffer, BleachEffect.HOLLOW_MASK);
            return;
        }

        // Get the head armor slot
        var armorComp = store.getComponent(ref, InventoryComponent.Armor.getComponentType());
        if (armorComp == null) return;
        var armorInv = armorComp.getInventory();

        short headSlot = (short) ItemArmorSlot.Head.getValue(); // = 0
        ItemStack currentHead = armorInv.getItemStack(headSlot); // may be null if nothing worn

        // Swap head slot to hollow mask item
        var transaction = armorInv.setItemStackForSlot(headSlot, new ItemStack(HOLLOW_MASK_ID, 1));
        if (!transaction.succeeded()) return;

        // Ensure the ActiveEffectsComponent exists
        ActiveEffectsComponent effects = store.getComponent(ref, ActiveEffectsComponent.getComponentType());
        if (effects == null) {
            effects = new ActiveEffectsComponent();
            commandBuffer.addComponent(ref, ActiveEffectsComponent.getComponentType(), effects);
        }

        // Register timer and armor swap-back
        effects.setTimer(BleachEffect.HOLLOW_MASK, duration);
        effects.registerArmorSwapBack(BleachEffect.HOLLOW_MASK, headSlot, currentHead);
    }
}
