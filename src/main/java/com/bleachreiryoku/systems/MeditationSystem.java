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
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.AnimationSlot;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.AnimationUtils;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.PendingTeleport;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

/** Tracks meditation, then finishes outside entity iteration so animation cleanup is safe. */
public class MeditationSystem extends EntityTickingSystem<EntityStore> {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final double MOVE_CANCEL_DISTANCE_SQ = 0.35 * 0.35;

    @Override
    public void tick(float dt, int i, @Nonnull ArchetypeChunk<EntityStore> chunk,
                     @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commands) {
        ActiveMeditation med = chunk.getComponent(i, ActiveMeditation.getComponentType());
        if (med == null || med.finishing) return;
        Ref<EntityStore> ref = chunk.getReferenceTo(i);
        if (!ref.isValid()) return;

        // An unacknowledged seat teleport must not count as walking away from the seat.
        if (chunk.getComponent(i, Teleport.getComponentType()) != null
                || chunk.getComponent(i, PendingTeleport.getComponentType()) != null) return;

        TransformComponent transform = chunk.getComponent(i, TransformComponent.getComponentType());
        if (transform == null) {
            finish(ref, med, false, commands);
            return;
        }
        double dx = transform.getPosition().x() - med.lockX;
        double dz = transform.getPosition().z() - med.lockZ;
        if (dx * dx + dz * dz > MOVE_CANCEL_DISTANCE_SQ) {
            finish(ref, med, false, commands);
            return;
        }
        med.elapsed += dt;
        if (med.elapsed >= MeditationService.DURATION_SECONDS) finish(ref, med, true, commands);
    }

    private void finish(Ref<EntityStore> ref, ActiveMeditation med, boolean completed,
                        CommandBuffer<EntityStore> commands) {
        med.finishing = true;
        commands.run(store -> {
            if (!ref.isValid() || store.getComponent(ref, ActiveMeditation.getComponentType()) != med) return;
            PlayerRef player = store.getComponent(ref, PlayerRef.getComponentType());
            try {
                if (completed) {
                    playerStats stats = store.getComponent(ref, playerStats.getComponentType());
                    EntityStatMap statMap = store.getComponent(ref, EntityStatMap.getComponentType());
                    int reiryokuIndex = BleachStatTypes.getReiryoku();
                    if (stats == null || statMap == null || reiryokuIndex < 0 || statMap.get(reiryokuIndex) == null) {
                        if (player != null) player.sendMessage(Message.raw("Meditation could not grant its reward: player stats are unavailable. Please try again."));
                        LOGGER.atWarning().log("[Bleach Reiryoku] Meditation reward skipped: player stats unavailable.");
                        return;
                    }
                    BleachStatTypes.addMaxReiryoku(ref, store, MeditationService.REWARD_REIRYOKU);
                    long now = MeditationService.currentGameSeconds(store);
                    if (now != Long.MIN_VALUE) stats.lastMeditationGameTime = now;
                    if (player != null) player.sendMessage(Message.raw("Meditation complete: +15 maximum Reiryoku."));
                    LOGGER.atInfo().log("[Bleach Reiryoku] Meditation completed; granted +15 maximum Reiryoku.");
                } else {
                    if (player != null) player.sendMessage(Message.raw("Meditation cancelled because you moved away from the seat. No daily cooldown was used."));
                    LOGGER.atInfo().log("[Bleach Reiryoku] Meditation cancelled after %.2f seconds.", med.elapsed);
                }
            } finally {
                // Clear runtime state even if reward processing fails.
                store.removeComponent(ref, ActiveMeditation.getComponentType());
                EffectControllerComponent effects = store.getComponent(ref, EffectControllerComponent.getComponentType());
                if (effects != null && med.lockEffectIndex != Integer.MIN_VALUE) {
                    effects.removeEffect(ref, med.lockEffectIndex, store);
                }

                MeditationAnimations.stop(ref, store);
            }
        });
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(PlayerRef.getComponentType(), ActiveMeditation.getComponentType());
    }
}
