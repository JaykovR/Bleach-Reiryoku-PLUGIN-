package com.bleachreiryoku.effects;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAttachment;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;


    // This starts when a player is removed or unloaded so it removes all effects from the player before they save,
    // most of the effect are going to be temporal so there is no need for them to be saves
public class EffectCleanupSystem extends RefSystem<EntityStore> {

    @Override
    public void onEntityAdded(@Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason,
                              @Nonnull Store<EntityStore> store,
                              @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        // Nothing on join
    }

    @Override
    public void onEntityRemove(@Nonnull Ref<EntityStore> ref, @Nonnull RemoveReason reason,
                               @Nonnull Store<EntityStore> store,
                               @Nonnull CommandBuffer<EntityStore> commandBuffer) {

        ActiveEffectsComponent effects = store.getComponent(ref, ActiveEffectsComponent.getComponentType());
        if (effects == null || effects.getTimers().isEmpty()) return;

        ModelComponent modelComponent = store.getComponent(ref, ModelComponent.getComponentType());
        if (modelComponent != null) {
            // Collect every attachment we injected across all active effects
            List<ModelAttachment> toRemove = new ArrayList<>();
            for (BleachEffect effect : effects.getTimers().keySet()) {
                for (int s = 0; s < effect.getSlots().length; s++) {
                    ModelAttachment tracked = effects.getTrackedAttachment(effect, s);
                    if (tracked != null) toRemove.add(tracked);
                }
            }

            if (!toRemove.isEmpty()) {
                var cleaned = ModelRebuildUtil.rebuildWithAttachments(modelComponent.getModel(), null, toRemove);
                commandBuffer.putComponent(ref, ModelComponent.getComponentType(), new ModelComponent(cleaned));
            }
        }

        effects.clearAll();
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
