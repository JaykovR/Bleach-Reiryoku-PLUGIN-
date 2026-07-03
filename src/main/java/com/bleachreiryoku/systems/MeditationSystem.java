package com.bleachreiryoku.systems;

import com.bleachreiryoku.effects.BleachStatTypes;
import com.bleachreiryoku.playerData.ActiveMeditation;
import com.bleachreiryoku.playerData.playerStats;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import org.joml.Vector3d;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

/**
 * Ticks in-progress Soul Meditations. For each player with ActiveMeditation:
 *   - advances the elapsed timer
 *   - CANCELS if the player has moved away from the locked spot (covers knockback and any forced movement)
 *   - on reaching DURATION_SECONDS: grants +REWARD_REIRYOKU, stamps the once-per-day
 *     cooldown on playerStats, and clears the meditation.
 */
public class MeditationSystem extends EntityTickingSystem<EntityStore> {

    // If the player is more than this far (blocks) from the lock point, they moved.
    private static final double MOVE_CANCEL_DISTANCE_SQ = 0.35 * 0.35;

    @Override
    public void tick(float dt, int i,
                     @Nonnull ArchetypeChunk<EntityStore> chunk,
                     @Nonnull Store<EntityStore> store,
                     @Nonnull CommandBuffer<EntityStore> commandBuffer) {

        ActiveMeditation med = chunk.getComponent(i, ActiveMeditation.getComponentType());
        if (med == null) return;

        Ref<EntityStore> ref = chunk.getReferenceTo(i);
        if (ref == null || !ref.isValid()) return;

        // cancel if the player moved (knockback, forced walk, etc.)
        TransformComponent transform = chunk.getComponent(i, TransformComponent.getComponentType());
        if (transform != null) {
            Vector3d pos = transform.getPosition();
            // Cancel only on HORIZONTAL movement (walked off / knocked back). Y is deliberately ignored here.
            double dx = pos.x - med.lockX;
            double dz = pos.z - med.lockZ;
            if ((dx * dx + dz * dz) > MOVE_CANCEL_DISTANCE_SQ) {
                cancel(ref, store, commandBuffer, med);
                return;
            }
        }

        // Cdvance timer
        med.elapsed += dt;
        if (med.elapsed < MeditationService.DURATION_SECONDS) {
            return; // still meditating
        }

        // Completion rewards (+15 permanent MAX Reiryoku) + stamp cooldown
        BleachStatTypes.addMaxReiryoku(ref, store, MeditationService.REWARD_REIRYOKU);

        playerStats stats = chunk.getComponent(i, playerStats.getComponentType());
        if (stats != null) {
            long now = MeditationService.currentGameSeconds(store);
            if (now != Long.MIN_VALUE) { // MIN_VALUE = time unavailable; negative is a valid year-1 clock
                stats.lastMeditationGameTime = now;
            }
        }

        clear(ref, store, commandBuffer, med);
    }

    private void cancel(Ref<EntityStore> ref, Store<EntityStore> store,
                        CommandBuffer<EntityStore> cmd, ActiveMeditation med) {
        // No reward so cooldown untouched.
        clear(ref, store, cmd, med);
    }

    // Remove the lock effect and the ActiveMeditation component.
    private void clear(Ref<EntityStore> ref, Store<EntityStore> store,
                       CommandBuffer<EntityStore> cmd, ActiveMeditation med) {
        if (med.lockEffectIndex != Integer.MIN_VALUE) {
            EffectControllerComponent effects = store.getComponent(ref, EffectControllerComponent.getComponentType());
            if (effects != null) {
                effects.removeEffect(ref, med.lockEffectIndex, cmd);
            }
        }

        // Unstuck player from EntityEffect animation. (animationId=null clears the slot).
        clearSlot(ref, store, com.hypixel.hytale.protocol.AnimationSlot.Movement);
        clearSlot(ref, store, com.hypixel.hytale.protocol.AnimationSlot.Status);
        clearSlot(ref, store, com.hypixel.hytale.protocol.AnimationSlot.Action);

        cmd.removeComponent(ref, ActiveMeditation.getComponentType());
    }

    private void clearSlot(Ref<EntityStore> ref, Store<EntityStore> store,
                           com.hypixel.hytale.protocol.AnimationSlot slot) {
        com.hypixel.hytale.server.core.entity.AnimationUtils.playAnimation(ref, slot, null, true, store);
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(
                PlayerRef.getComponentType(),
                ActiveMeditation.getComponentType()
        );
    }
}
