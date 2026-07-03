package com.bleachreiryoku.systems;

import com.bleachreiryoku.playerData.ActiveMeditation;
import com.bleachreiryoku.playerData.playerStats;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import org.joml.Vector3d;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;

/**
 * Starts a Soul Meditation for a player. Handles the once-per-in-game-day cooldown check,
 * teleports the player onto the seat, applying the movement-lock effect, and attaching
 * ActiveMeditation so MeditationSystem can run the 30s timer.
 *
 * The cooldown is only stamped on SUCCESSFUL completion (in MeditationSystem).
 */
public final class MeditationService {

    public static final float DURATION_SECONDS = 30f;
    public static final float REWARD_REIRYOKU = 15f;
    public static final String LOCK_EFFECT_ID = "Meditation_Lock";

    public enum StartResult { STARTED, ON_COOLDOWN, ALREADY_MEDITATING, ERROR }

    private MeditationService() {}

    // Seconds of the in-game clock in one full day.
    private static long secondsPerDay() {
        return WorldTimeResource.SECONDS_PER_DAY;
    }

    // Current in-game clock as epoch seconds. Returns Long.MIN_VALUE if unavailable.
    public static long currentGameSeconds(Store<EntityStore> store) {
        WorldTimeResource time = store.getResource(WorldTimeResource.getResourceType());
        if (time == null) return Long.MIN_VALUE;
        return time.getGameTime().getEpochSecond();
    }

    private static boolean timeUnavailable(long nowSeconds) {
        return nowSeconds == Long.MIN_VALUE;
    }

    // True if the player is allowed to meditate right now (cooldown elapsed).
    public static boolean isOffCooldown(playerStats stats, long nowSeconds) {
        if (stats == null) return false;
        if (timeUnavailable(nowSeconds)) return true; // can't read time -> don't block the player
        if (stats.lastMeditationGameTime == 0L) return true; // never meditated (0 = sentinel)
        long delta = nowSeconds - stats.lastMeditationGameTime;
        if (delta < 0) return true; // clock moved backwards (reset) -> allow rather than lock out
        return delta >= secondsPerDay();
    }

    // In-game seconds remaining on the cooldown (0 = ready)
    public static long cooldownRemaining(playerStats stats, long nowSeconds) {
        if (stats == null || timeUnavailable(nowSeconds) || stats.lastMeditationGameTime == 0L) return 0;
        long elapsed = nowSeconds - stats.lastMeditationGameTime;
        if (elapsed < 0) return 0;
        long remain = secondsPerDay() - elapsed;
        return Math.max(0, remain);
    }

    /**
     * Begins meditation. The seat position (block the player interacted with) is passed
     * in so we can center the player on top of it. Returns a StartResult for messaging.
     */
    public static StartResult start(Ref<EntityStore> ref, Store<EntityStore> store,
                                    double seatX, double seatY, double seatZ) {
        if (ref == null || !ref.isValid()) return StartResult.ERROR;

        // Already meditating?
        if (store.getComponent(ref, ActiveMeditation.getComponentType()) != null) {
            return StartResult.ALREADY_MEDITATING;
        }

        playerStats stats = store.getComponent(ref, playerStats.getComponentType());
        long now = currentGameSeconds(store);
        if (!isOffCooldown(stats, now)) {
            return StartResult.ON_COOLDOWN;
        }

        TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
        if (transform == null) return StartResult.ERROR;

        // Center on the seat block, sitting on top of it.
        double px = seatX + 0.5;
        double py = seatY + 0.25; // 0.25 because of the blocks height
        double pz = seatZ + 0.5;

        transform.teleportPosition(new Vector3d(px, py, pz));


        // Teleports the player onto the seat
        com.hypixel.hytale.math.vector.Rotation3f bodyRotation = transform.getRotation();
        com.hypixel.hytale.server.core.modules.entity.teleport.Teleport tp =
                com.hypixel.hytale.server.core.modules.entity.teleport.Teleport.createForPlayer(
                        new Vector3d(px, py, pz), bodyRotation);
        store.addComponent(ref,
                com.hypixel.hytale.server.core.modules.entity.teleport.Teleport.getComponentType(), tp);

        // Apply the movement-lock effect.
        int effectIndex = Integer.MIN_VALUE;
        EffectControllerComponent effects = store.getComponent(ref, EffectControllerComponent.getComponentType());
        if (effects != null) {
            EntityEffect lock = EntityEffect.getAssetMap().getAsset(LOCK_EFFECT_ID);
            if (lock != null) {
                effects.addEffect(ref, lock, store);
                effectIndex = EntityEffect.getAssetMap().getIndex(LOCK_EFFECT_ID);
            }
        }

        // Attach runtime meditation state.
        ActiveMeditation med = new ActiveMeditation();
        med.elapsed = 0f;
        med.lockX = px; med.lockY = py; med.lockZ = pz;
        med.lockEffectIndex = effectIndex;
        store.addComponent(ref, ActiveMeditation.getComponentType(), med);

        return StartResult.STARTED;
    }
}
