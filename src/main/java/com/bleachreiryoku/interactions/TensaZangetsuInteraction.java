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
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import com.bleachreiryoku.effects.ModelRebuildUtil;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import java.util.Arrays;

/**
  Bankai activation interaction for Tensa Zangetsu.

  When triggered with "Weapon_Sword_Shikai_Zangetsu" in hand:
    1. Swaps it to "Weapon_Sword_Bankai_Tensa_Zangetsu" in the same hotbar slot.
    2. Applies the TENSA_ZANGETSU_CHEST visual effect for the configured duration. // (wip)

  When the duration expires (handled automatically by EffectTickSystem):
    - The chest-piece model attachment is removed from the player's model. (Testing, not definitive yet since it shows jank.)
    - The bankai weapon is swapped back to shikai, but only if the player
 *     hasn't already moved the item out of that slot manually.
 *
  Does nothing if:
    - The held item is not "Weapon_Sword_Shikai_Zangetsu".
    - Bankai is already active (prevents double-activation).
 */
public class TensaZangetsuInteraction extends SimpleInteraction {

    private static final String SHIKAI_ID = "Weapon_Sword_Shikai_Zangetsu";
    private static final String BANKAI_ID = "Weapon_Sword_Bankai_Tensa_Zangetsu";

    /*
      JSON usage (Server/Item/Interactions/):
      {
        "Type": "TensaZangetsu",
        "Duration": 30.0
      }

     */
    public static final BuilderCodec<TensaZangetsuInteraction> CODEC =
            BuilderCodec.builder(TensaZangetsuInteraction.class, TensaZangetsuInteraction::new, SimpleInteraction.CODEC)
                    .append(
                            new KeyedCodec<>("Duration", Codec.FLOAT),
                            (t, v) -> t.duration = v,
                            t -> t.duration
                    ).add()
                    .build();

    // How long the bankai state lasts in seconds. Default 30.

    private float duration = 30.0f;

    @Override
    protected void tick0(boolean firstRun, float time,
                         @NonNullDecl InteractionType type,
                         @NonNullDecl InteractionContext context,
                         @NonNullDecl CooldownHandler cooldownHandler) {

        if (!firstRun) return;

        Ref<EntityStore> ref = context.getOwningEntity();
        Store<EntityStore> store = ref.getStore();

        if (store.getComponent(ref, Player.getComponentType()) == null) return;

        // Guard: don't activate if already in bankai
        if (BleachEffectService.hasEffect(ref, store, BleachEffect.TENSA_ZANGETSU_CHEST)) return;

        // Validate held item is the shikai
        ItemStack heldItem = context.getHeldItem();
        if (heldItem == null || !heldItem.getItemId().equals(SHIKAI_ID)) return;

        byte heldSlot = context.getHeldItemSlot();
        var hotbar = context.getHeldItemContainer();
        if (hotbar == null) return;

        // Swap Zangetsu -> Tensa Zangetsu in the same slot
        var swapTransaction = hotbar.setItemStackForSlot(heldSlot, new ItemStack(BANKAI_ID, 1));
        if (!swapTransaction.succeeded()) return;

        // Apply the chest-piece visual effect for the configured duration
        var commandBuffer = context.getCommandBuffer();
        BleachEffectService.applyTimedEffect(ref, store, commandBuffer, BleachEffect.TENSA_ZANGETSU_CHEST, duration);

        // Register the swap-back so EffectTickSystem can restore the shikai when the effect expires
        ActiveEffectsComponent effects = store.getComponent(ref, ActiveEffectsComponent.getComponentType());
        if (effects != null) {
            effects.registerSwapBack(BleachEffect.TENSA_ZANGETSU_CHEST, heldSlot, heldItem);
        }
    }
}
