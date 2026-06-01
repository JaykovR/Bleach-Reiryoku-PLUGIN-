package com.bleachreiryoku.systems;

import com.bleachreiryoku.effects.BleachStatTypes;
import com.bleachreiryoku.hud.ReiryokuBar;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ReiryokuHudSystem extends EntityTickingSystem<EntityStore> {

    private static final float UPDATE_INTERVAL = 0.07f;
    private float[] timers = new float[64];

    @Override
    public void tick(float dt, int i,
                     @Nonnull ArchetypeChunk<EntityStore> chunk,
                     @Nonnull Store<EntityStore> store,
                     @Nonnull CommandBuffer<EntityStore> commandBuffer) {

        if (i >= timers.length) {
            float[] newTimers = new float[i * 2];
            System.arraycopy(timers, 0, newTimers, 0, timers.length);
            timers = newTimers;
        }
        timers[i] += dt;
        if (timers[i] < UPDATE_INTERVAL) return;
        timers[i] = 0f;

        Player player = chunk.getComponent(i, Player.getComponentType());
        if (player == null) return;

        if (!(player.getHudManager().getCustomHud(ReiryokuBar.KEY) instanceof ReiryokuBar hud)) return;

        EntityStatMap stats = chunk.getComponent(i, EntityStatMap.getComponentType());
        if (stats == null) return;

        int reiryokuIndex = BleachStatTypes.getReiryoku();
        if (reiryokuIndex == BleachStatTypes.NOT_FOUND) return;

        var statValue = stats.get(reiryokuIndex);
        if (statValue == null) return;

        hud.updateBar(statValue.asPercentage(), statValue.get(), statValue.getMax());
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(
                Player.getComponentType(),
                PlayerRef.getComponentType(),
                EntityStatMap.getComponentType()
        );
    }
}