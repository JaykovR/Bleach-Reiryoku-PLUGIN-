package com.bleachreiryoku.effects;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAttachment;
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
            if (remaining < 0) continue; // Permanent toggle — skip

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
            // Model is gone (shouldn't normally happen) — just clean up tracking
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
