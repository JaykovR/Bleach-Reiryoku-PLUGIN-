package com.bleachreiryoku.playerData;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

/**
 * Active Meditation - Runtime Only. Present on a player while they're meditating. Counts elapsed time,
 * cancels if the changes position or takes damage. When complete grants Reiryoku and stamps the daily cooldown
 * inside PlayerStats
 */
public class ActiveMeditation implements Component<EntityStore> {

    public static ComponentType<EntityStore, ActiveMeditation> TYPE;

    public static void setComponentType(ComponentType<EntityStore, ActiveMeditation> type) {
        TYPE = type;
    }

    public static ComponentType<EntityStore, ActiveMeditation> getComponentType() {
        return TYPE;
    }

    // Seconds meditated so far. Reward at >= DURATION_SECONDS.
    public float elapsed = 0f;

    // The locked position (block center the player was teleported onto).
    public double lockX = 0, lockY = 0, lockZ = 0;

    // Health at start; a drop below this means the player took damage -> cancel.
    public float startHealth = 0f;

    // The index of the movement-lock EntityEffect we applied, so we can remove it on cancel.
    public int lockEffectIndex = Integer.MIN_VALUE;

    //Codec exists only because the component registry requires one.
    public static final BuilderCodec<ActiveMeditation> CODEC =
            BuilderCodec.builder(ActiveMeditation.class, ActiveMeditation::new).build();

    public ActiveMeditation() {}

    @NullableDecl
    @Override
    public ActiveMeditation clone() {
        ActiveMeditation c = new ActiveMeditation();
        c.elapsed = elapsed;
        c.lockX = lockX; c.lockY = lockY; c.lockZ = lockZ;
        c.startHealth = startHealth;
        c.lockEffectIndex = lockEffectIndex;
        return c;
    }
}
