package com.bleachreiryoku.effects;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAttachment;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Runs every server tick. Counts down timed effect timers and removes
 * expired effects by stripping their attachments from the player's ModelComponent.
 *
 * Permanent toggle effects (timer == -1) are untouched here — they only
 * disappear when BleachEffectService.removeEffect() is called explicitly.
 *
 * Only runs for entities that have ActiveEffectsComponent, so there is
 * zero per-tick cost for players who have no active Bleach effects.
 */
public class EffectTickSystem extends EntityTickingSystem<EntityStore> {

    @Override
    public void tick(float dt, int i,
                     @Nonnull ArchetypeChunk<EntityStore> chunk,
                     @Nonnull Store<EntityStore> store,
                     @Nonnull CommandBuffer<EntityStore> commandBuffer) {

        ActiveEffectsComponent effects = chunk.getComponent(i, ActiveEffectsComponent.getComponentType());
        if (effects == null || effects.getTimers().isEmpty()) return;

        Map<BleachEffect, Float> timers = effects.getTimers();
        List<BleachEffect> expired = null;

        // Tick down all active timers
        for (Map.Entry<BleachEffect, Float> entry : timers.entrySet()) {
            float remaining = entry.getValue();
            if (remaining < 0) continue; // Permanent toggle = skip

            remaining -= dt;
            if (remaining <= 0) {
                if (expired == null) expired = new ArrayList<>();
                expired.add(entry.getKey());
            } else {
                entry.setValue(remaining);
            }
        }

        if (expired == null) return;

        // Remove all expired effects from the model in one rebuild
        ModelComponent modelComponent = chunk.getComponent(i, ModelComponent.getComponentType());
        if (modelComponent == null) {
            // clean up tracking
            for (BleachEffect effect : expired) {
                effects.untrackEffect(effect);
                effects.removeTimer(effect);
            }
            return;
        }

        // Collect all attachments to strip across every expired effect
        List<ModelAttachment> toRemove = new ArrayList<>();
        for (BleachEffect effect : expired) {
            for (int s = 0; s < effect.getSlots().length; s++) {
                ModelAttachment tracked = effects.getTrackedAttachment(effect, s);
                if (tracked != null) toRemove.add(tracked);
            }
        }

        if (!toRemove.isEmpty()) {
            // One single Model rebuild covers all expired effects this tick
            var updated = ModelRebuildUtil.rebuildWithAttachments(modelComponent.getModel(), null, toRemove);
            commandBuffer.putComponent(
                    chunk.getReferenceTo(i),
                    ModelComponent.getComponentType(),
                    new ModelComponent(updated)
            );
        }

        // Clean up tracking
        for (BleachEffect effect : expired) {
            // Swap the weapon back if this effect registered a swap-back
            ActiveEffectsComponent.PendingSwapBack swapBack = effects.getSwapBack(effect);
            if (swapBack != null) {
                var hotbarType = InventoryComponent.Hotbar.getComponentType();
                var hotbarComp = chunk.getComponent(i, hotbarType);
                if (hotbarComp != null) {
                    ItemContainer hotbar = hotbarComp.getInventory();
                    // Only restore if the bankai weapon is still there, don't clobber a manual swap
                    ItemStack current = hotbar.getItemStack(swapBack.hotbarSlot());
                    // If I end up using this more I will change it so it reads the ID's from other parts.
                    if (current != null && current.getItemId().equals("Weapon_Sword_Bankai_Tensa_Zangetsu")) {
                        hotbar.setItemStackForSlot(swapBack.hotbarSlot(), swapBack.originalItem());
                    }
                }
                effects.clearSwapBack(effect);
            }

            // Restore any armor attachments that were hidden when this effect activated
            ModelAttachment[] hidden = effects.getHiddenAttachments(effect);
            if (hidden != null && hidden.length > 0) {
                // We need the model component again - get the latest after the rebuild above
                ModelComponent latestModel = chunk.getComponent(i, ModelComponent.getComponentType());
                if (latestModel != null) {
                    var restored = ModelRebuildUtil.rebuildWithAttachments(
                            latestModel.getModel(), java.util.Arrays.asList(hidden), null);
                    commandBuffer.putComponent(
                            chunk.getReferenceTo(i),
                            ModelComponent.getComponentType(),
                            new ModelComponent(restored)
                    );
                }
                effects.clearHiddenAttachments(effect);
            }

            effects.untrackEffect(effect);
            effects.removeTimer(effect);
        }
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(
                ActiveEffectsComponent.getComponentType(),
                ModelComponent.getComponentType()
        );
    }
}
