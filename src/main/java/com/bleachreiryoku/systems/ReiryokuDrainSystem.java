package com.bleachreiryoku.systems;

import com.bleachreiryoku.effects.BleachStatTypes;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Reduces Reiryoku regeneration when the player is holding a Shikai or Bankai weapon.
 *
 * Base regen is 5%/sec. This system drains the difference to achieve:
 *   Shikai: 1.5%/sec net regen (drains 3.5%/sec)
 *   Bankai:  0.3%/sec net regen (drains 4.7%/sec)
 *   TODO: HOLLOW MASK: DRAINS, MEANING NO REGEN
 */
public class ReiryokuDrainSystem extends EntityTickingSystem<EntityStore> {

    // Shinigami-Related Regeneration Rates
    private static final float SHIKAI_DRAIN_PER_SECOND = 0.035f;
    private static final float BANKAI_DRAIN_PER_SECOND = 0.047f;

    @Override
    public void tick(float dt, int i,
                     @Nonnull ArchetypeChunk<EntityStore> chunk,
                     @Nonnull Store<EntityStore> store,
                     @Nonnull CommandBuffer<EntityStore> commandBuffer) {

        var hotbarComp = chunk.getComponent(i, InventoryComponent.Hotbar.getComponentType());
        if (hotbarComp == null) return;

        var hotbar = hotbarComp.getInventory();
        float drainPerSecond = 0f;

        for (short s = 0; s < hotbar.getCapacity(); s++) {
            ItemStack item = hotbar.getItemStack(s);
            if (item == null) continue;
            String id = item.getItemId();
            if (id.contains("Bankai")) {
                drainPerSecond = BANKAI_DRAIN_PER_SECOND;
                break;
            }
            if (id.contains("Shikai")) {
                drainPerSecond = SHIKAI_DRAIN_PER_SECOND;
                break;
            }
        }

        if (drainPerSecond == 0f) return;

        EntityStatMap stats = chunk.getComponent(i, EntityStatMap.getComponentType());
        if (stats == null) return;

        int reiryokuIndex = BleachStatTypes.getReiryoku();
        if (reiryokuIndex == BleachStatTypes.NOT_FOUND) return;

        var statValue = stats.get(reiryokuIndex);
        if (statValue == null) return;

        float current = statValue.get();
        float max = statValue.getMax();
        float drain = drainPerSecond * max * dt;

        stats.setStatValue(reiryokuIndex, Math.max(0f, current - drain));
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(
                PlayerRef.getComponentType(),
                InventoryComponent.Hotbar.getComponentType(),
                EntityStatMap.getComponentType()
        );
    }
}