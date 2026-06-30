package com.bleachreiryoku.systems;

import com.bleachreiryoku.effects.BleachStatTypes;
import com.bleachreiryoku.playerData.playerStats;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.joml.Vector3d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * Reduces base Reiryoku regeneration to accomodate for different conditions depending on their race.
 * Base regen is 5%/sec. This system drains the difference to achieve the effects.
 *
 *   For Shinigami:
 *   Shikai: 1.5%/sec net regen (drains 3.5%/sec)
 *   Bankai:  0.3%/sec net regen (drains 4.7%/sec)
 *   TODO: HOLLOW MASK: DRAINS, MEANING NO REGEN
 *
 *   For Quincy:
 *   Reishi Poor Env: 1%/sec net regen (drains 4%)
 *   Reishi Standard Env: 3%/sec net regen (drains 2%)
 *   Reishi Rich Env: 7%/sec net regen (adds 2%)
 */
public class ReiryokuDrainSystem extends EntityTickingSystem<EntityStore> {

    // Shinigami-Related Regeneration Rates
    private static final float SHIKAI_DRAIN_PER_SECOND = 0.035f;
    private static final float BANKAI_DRAIN_PER_SECOND = 0.047f;
    // Quincy-Related Regeneration Rates
    private static final float REISHI_POOR_REGEN     = 0.04f; // 1%/sec
    private static final float REISHI_STANDARD_REGEN = 0.02f; // 3%/sec
    private static final float REISHI_RICH_REGEN     = -0.02f; // 7%/sec


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

        // Quincy Reiryoku Regen Part.
        // Check if player is a Quincy
        playerStats stats = chunk.getComponent(i, playerStats.getComponentType());
        if (playerStats.RACE_QUINCY.equals(stats.playerPrimaryRace)) {

            // Find the player's current environment
            TransformComponent transform = chunk.getComponent(i, TransformComponent.getComponentType());
            if (transform == null) return;
            Vector3d position = transform.getPosition();

            World world = store.getExternalData().getWorld();
            ChunkStore chunkStore = world.getChunkStore();
            long chunkIndex = ChunkUtil.indexChunkFromBlock(position.x, position.z());
            Ref<ChunkStore> chunkRef = chunkStore.getChunkReference(chunkIndex);
            if (chunkRef == null || !chunkRef.isValid()) return;

            BlockChunk blockChunk = chunkStore.getStore().getComponent(chunkRef, BlockChunk.getComponentType());
            if (blockChunk == null) return;

            int environmentId = blockChunk.getEnvironment(position);

            // Depending on what enviroment they're in the drain per second is different.
            switch (ReishiEnvironments.classify(environmentId)) {
                case RICH:
                    drainPerSecond = REISHI_RICH_REGEN;
                    break;
                case POOR:
                    drainPerSecond = REISHI_POOR_REGEN;
                    break;
                case STANDARD:
                default:
                    drainPerSecond = REISHI_STANDARD_REGEN;
                    break;
            }
        }

        if (drainPerSecond == 0f) return;

        EntityStatMap statsMap = chunk.getComponent(i, EntityStatMap.getComponentType());
        if (statsMap == null) return;

        int reiryokuIndex = BleachStatTypes.getReiryoku();
        if (reiryokuIndex == BleachStatTypes.NOT_FOUND) return;

        var statValue = statsMap.get(reiryokuIndex);
        if (statValue == null) return;

        float current = statValue.get();
        float max = statValue.getMax();
        float drain = drainPerSecond * max * dt;

        statsMap.setStatValue(reiryokuIndex, Math.max(0f, current - drain));

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