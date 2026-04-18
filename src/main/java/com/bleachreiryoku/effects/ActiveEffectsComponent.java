package com.bleachreiryoku.effects;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAttachment;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;



// Tracks attached components
public class ActiveEffectsComponent implements Component<EntityStore> {

// Saves/loads nothing. CODEC
    public static final BuilderCodec<ActiveEffectsComponent> CODEC =
            BuilderCodec.builder(ActiveEffectsComponent.class, ActiveEffectsComponent::new).build();

    private static ComponentType<EntityStore, ActiveEffectsComponent> TYPE;

    public static void setComponentType(ComponentType<EntityStore, ActiveEffectsComponent> type) {
        TYPE = type;
    }

    public static ComponentType<EntityStore, ActiveEffectsComponent> getComponentType() {
        return TYPE;
    }

    /*
      Countdown timers.
        Positive  -> seconds remaining before the effect expires
        -1.0f     -> permanent toggle
     */
    private final Map<BleachEffect, Float> timers = new EnumMap<>(BleachEffect.class);

    /*
      Ref to ModelAttachment we inject into player's model

      Key format: "EFFECT_NAME:slotIndex" (e.g. "HOLLOW_MASK:0").
      We store the exact object references so we can filter them out of the
      Model's attachment array later using reference equality (==).
     */
    private final Map<String, ModelAttachment> appliedAttachments = new HashMap<>();

    public ActiveEffectsComponent() {}

    // ----------------------TIMERS------------------------------------

    public boolean hasEffect(BleachEffect effect) {
        return timers.containsKey(effect);
    }

    public Map<BleachEffect, Float> getTimers() {
        return timers;
    }

    public void setTimer(BleachEffect effect, float seconds) {
        timers.put(effect, seconds);
    }

    public void removeTimer(BleachEffect effect) {
        timers.remove(effect);
    }

    public void clearAll() {
        timers.clear();
        appliedAttachments.clear();
        pendingSwapBacks.clear();
        hiddenAttachments.clear();
    }

    // --------------------ITEM SWAP-BACK---------------------------------

    // When bankai activation swaps the player's weapon, it remembers which slot
    // it was in and what the original item was, so EffectTickSystem can restore
    // it when the effect expires. This shouldn't matter since the weapons have no durability component.

    public record PendingSwapBack(byte hotbarSlot, ItemStack originalItem) {}

    private final Map<BleachEffect, PendingSwapBack> pendingSwapBacks = new EnumMap<>(BleachEffect.class);

    // Attachments temporarily stripped from the model while an effect is active
    // (e.x. armor hidden during TENSA_ZANGETSU_CHEST). Restored on effect expiry.
    private final Map<BleachEffect, ModelAttachment[]> hiddenAttachments = new EnumMap<>(BleachEffect.class);

    public void storeHiddenAttachments(BleachEffect effect, ModelAttachment[] attachments) {
        hiddenAttachments.put(effect, attachments);
    }

    @Nullable
    public ModelAttachment[] getHiddenAttachments(BleachEffect effect) {
        return hiddenAttachments.get(effect);
    }

    public void clearHiddenAttachments(BleachEffect effect) {
        hiddenAttachments.remove(effect);
    }
    public void registerSwapBack(BleachEffect effect, byte hotbarSlot, ItemStack originalItem) {
        pendingSwapBacks.put(effect, new PendingSwapBack(hotbarSlot, originalItem));
    }

    @Nullable
    public PendingSwapBack getSwapBack(BleachEffect effect) {
        return pendingSwapBacks.get(effect);
    }

    public void clearSwapBack(BleachEffect effect) {
        pendingSwapBacks.remove(effect);
    }

    // --------------------ATTACHMENTS----------------------------------------

    // Stores ModelAttachemnt with a key
    public void trackAttachment(BleachEffect effect, int slotIndex, ModelAttachment attachment) {
        appliedAttachments.put(attachmentKey(effect, slotIndex), attachment);
    }

    // Retrieves ModelAttachment previously stored, returns if not tracked or never applied
    @Nullable
    public ModelAttachment getTrackedAttachment(BleachEffect effect, int slotIndex) {
        return appliedAttachments.get(attachmentKey(effect, slotIndex));
    }

    // Removes tracking for a slot after removal
    public void untrackAttachment(BleachEffect effect, int slotIndex) {
        appliedAttachments.remove(attachmentKey(effect, slotIndex));
    }

    // Remove all tracked attachments for an effect (all its slots at once)
    public void untrackEffect(BleachEffect effect) {
        for (int i = 0; i < effect.getSlots().length; i++) {
            appliedAttachments.remove(attachmentKey(effect, i));
        }
    }

    private static String attachmentKey(BleachEffect effect, int slotIndex) {
        return effect.name() + ":" + slotIndex;
    }

    // ----------------------COMPONENT---------------------------

    @Nullable
    @Override
    public ActiveEffectsComponent clone() {
        return null;
    }
}
