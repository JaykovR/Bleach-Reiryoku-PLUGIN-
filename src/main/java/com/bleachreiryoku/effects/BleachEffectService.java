package com.bleachreiryoku.effects;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAttachment;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.ArrayList;
import java.util.List;

// This class is where all the attachments are applied and removed
public final class BleachEffectService {

    private BleachEffectService() {}

    // ------------APPLY-----------------------


    // Applies a timed effect that auto-removes after the duration established
    // If it's already active it overrides with the new duration
    public static void applyTimedEffect(
            Ref<EntityStore> ref,
            Store<EntityStore> store,
            CommandBuffer<EntityStore> commandBuffer,
            BleachEffect effect,
            float durationSeconds
    ) {
        applyEffect(ref, store, commandBuffer, effect);
        ensureComponent(ref, store, commandBuffer).setTimer(effect, durationSeconds);
    }


    // Applies permanent toggle effect. Remove them with removeEffect().
    // If the effect was active with a timer the countdown just cancels
    public static void applyToggleEffect(
            Ref<EntityStore> ref,
            Store<EntityStore> store,
            CommandBuffer<EntityStore> commandBuffer,
            BleachEffect effect
    ) {
        applyEffect(ref, store, commandBuffer, effect);
        ensureComponent(ref, store, commandBuffer).setTimer(effect, -1f);
    }


    // Applies the effect if it's off and removes it if it's on
    public static void toggleEffect(
            Ref<EntityStore> ref,
            Store<EntityStore> store,
            CommandBuffer<EntityStore> commandBuffer,
            BleachEffect effect
    ) {
        if (hasEffect(ref, store, effect)) {
            removeEffect(ref, store, commandBuffer, effect);
        } else {
            applyToggleEffect(ref, store, commandBuffer, effect);
        }
    }

    // -------------REMOVE-----------


    // Removes effect. If there's nothing it does nothing
    public static void removeEffect(
            Ref<EntityStore> ref,
            Store<EntityStore> store,
            CommandBuffer<EntityStore> commandBuffer,
            BleachEffect effect
    ) {
        ActiveEffectsComponent component = store.getComponent(ref, ActiveEffectsComponent.getComponentType());
        if (component == null || !component.hasEffect(effect)) return;

        ModelComponent modelComponent = store.getComponent(ref, ModelComponent.getComponentType());
        if (modelComponent == null) return;

        // Collect the exact attachment references we previously added for this effect
        List<ModelAttachment> toRemove = collectTrackedAttachments(component, effect);

        if (!toRemove.isEmpty()) {
            Model updated = ModelRebuildUtil.rebuildWithAttachments(modelComponent.getModel(), null, toRemove);
            commandBuffer.putComponent(ref, ModelComponent.getComponentType(), new ModelComponent(updated));
        }

        component.untrackEffect(effect);
        component.removeTimer(effect);
    }


    // Removes all effects for death, logout, resets, etc.
    public static void removeAllEffects(
            Ref<EntityStore> ref,
            Store<EntityStore> store,
            CommandBuffer<EntityStore> commandBuffer
    ) {
        ActiveEffectsComponent component = store.getComponent(ref, ActiveEffectsComponent.getComponentType());
        if (component == null || component.getTimers().isEmpty()) return;

        ModelComponent modelComponent = store.getComponent(ref, ModelComponent.getComponentType());
        if (modelComponent != null) {
            // Collect every attachment we've ever added across all active effects
            List<ModelAttachment> allToRemove = new ArrayList<>();
            for (BleachEffect effect : component.getTimers().keySet()) {
                allToRemove.addAll(collectTrackedAttachments(component, effect));
            }
            if (!allToRemove.isEmpty()) {
                Model updated = ModelRebuildUtil.rebuildWithAttachments(modelComponent.getModel(), null, allToRemove);
                commandBuffer.putComponent(ref, ModelComponent.getComponentType(), new ModelComponent(updated));
            }
        }

        component.clearAll();
    }

    // ------------------QUERY--------------------

    // Returns true if the player currently has the given effect active
    public static boolean hasEffect(Ref<EntityStore> ref, Store<EntityStore> store, BleachEffect effect) {
        ActiveEffectsComponent component = store.getComponent(ref, ActiveEffectsComponent.getComponentType());
        return component != null && component.hasEffect(effect);
    }

    // Return remaning deconds for an effect or -1 for a permanent. Null if it's not active
    public static Float getRemainingTime(Ref<EntityStore> ref, Store<EntityStore> store, BleachEffect effect) {
        ActiveEffectsComponent component = store.getComponent(ref, ActiveEffectsComponent.getComponentType());
        if (component == null) return null;
        return component.getTimers().get(effect);
    }


    private static void applyEffect(
            Ref<EntityStore> ref,
            Store<EntityStore> store,
            CommandBuffer<EntityStore> commandBuffer,
            BleachEffect effect
    ) {
        ModelComponent modelComponent = store.getComponent(ref, ModelComponent.getComponentType());
        if (modelComponent == null) return;

        ActiveEffectsComponent component = ensureComponent(ref, store, commandBuffer);

        // If already active, collect old attachments to replace them
        List<ModelAttachment> toRemove = collectTrackedAttachments(component, effect);

        // Build the new ModelAttachments for each slot
        EffectSlot[] slots = effect.getSlots();
        List<ModelAttachment> toAdd = new ArrayList<>(slots.length);
        for (int i = 0; i < slots.length; i++) {
            EffectSlot slot = slots[i];
            ModelAttachment attachment = new ModelAttachment(
                    slot.modelAssetId(),
                    slot.textureAssetId(),
                    null,   // gradientSet — not used for most Bleach effects
                    null,   // gradientId
                    1.0     // weight/scale
            );
            toAdd.add(attachment);
            component.trackAttachment(effect, i, attachment); // remember for later removal
        }

        Model updated = ModelRebuildUtil.rebuildWithAttachments(modelComponent.getModel(), toAdd, toRemove);
        commandBuffer.putComponent(ref, ModelComponent.getComponentType(), new ModelComponent(updated));
    }

    // Collect all tracked ModelAttachment references for an effect (it's empty if not yet applied).
    private static List<ModelAttachment> collectTrackedAttachments(
            ActiveEffectsComponent component, BleachEffect effect
    ) {
        EffectSlot[] slots = effect.getSlots();
        List<ModelAttachment> list = new ArrayList<>(slots.length);
        for (int i = 0; i < slots.length; i++) {
            ModelAttachment existing = component.getTrackedAttachment(effect, i);
            if (existing != null) list.add(existing);
        }
        return list;
    }



    private static ActiveEffectsComponent ensureComponent(
            Ref<EntityStore> ref,
            Store<EntityStore> store,
            CommandBuffer<EntityStore> commandBuffer
    ) {
        ActiveEffectsComponent existing = store.getComponent(ref, ActiveEffectsComponent.getComponentType());
        if (existing != null) return existing;

        ActiveEffectsComponent fresh = new ActiveEffectsComponent();
        commandBuffer.addComponent(ref, ActiveEffectsComponent.getComponentType(), fresh);
        return fresh;
    }
}
