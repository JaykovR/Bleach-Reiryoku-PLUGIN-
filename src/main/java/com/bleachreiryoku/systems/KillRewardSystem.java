package com.bleachreiryoku.systems;

import com.bleachreiryoku.effects.BleachStatTypes;
import com.bleachreiryoku.playerData.playerStats;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.Map;

/**
 * Fires when any entity gains a DeathComponent.
 * Checks if the killer is a player, reads the NPC role name,
 * updates kill counters and grants Reiryoku max increase.
 */
public class KillRewardSystem extends DeathSystems.OnDeathSystem {

    private static final Map<String, Float> KILL_REWARDS = Map.ofEntries(
            Map.entry("Ghoul_Hollow",           2.0f),
            Map.entry("Scorpion_Hollow",        3.0f),
            Map.entry("Scarak_Louse_Hollow",    3.0f),
            Map.entry("Scarak_Fighter_Hollow",  5.0f),
            Map.entry("Scarak_Defender_Hollow", 7.0f),
            Map.entry("Crawler_Hollow_Void",    4.0f),
            // Vanilla Creatures
            Map.entry("Bear_Grizzly",           2.0f),
            Map.entry("Bear_Polar",             2.0f),
            Map.entry("Fox",                    1.0f),
            Map.entry("Wolf_Black",             2.0f),
            Map.entry("Wolf_White",             2.0f),
            Map.entry("Hyena",                  2.0f),
            Map.entry("Yeti",                   3.0f),
            Map.entry("Snapdragon",             2.0f),
            Map.entry("Raptor_Cave",            2.0f),
            Map.entry("Rex_Cave",               4.0f),
            Map.entry("Toad_Rihno",             2.0f),
            Map.entry("Toad_Rihno_Magma",       2.0f),
            Map.entry("Golem_Crystal_Earth",    2.0f),
            Map.entry("Golem_Crystal_Flame",    3.0f),
            Map.entry("Golem_Crystal_Frost",    3.0f),
            Map.entry("Golem_Crystal_Sand",    2.0f),
            Map.entry("Golem_Crystal_Thunder", 3.0f),
            Map.entry("Golem_Firesteel",       2.0f),
            Map.entry("Golem_Guardian_Void",    4.0f) // more to add.

    );

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Archetype.of(DeathComponent.getComponentType());
    }

    @Override
    public void onComponentAdded(
            @NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl DeathComponent deathComponent,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer
    ) {
        var deathInfo = deathComponent.getDeathInfo();
        if (deathInfo == null) return;

        // Get killer ref. Unwrap ProjectileSource if needed
        Ref<EntityStore> killerRef;
        if (deathInfo.getSource() instanceof Damage.ProjectileSource projectileSource) {
            killerRef = projectileSource.getRef();
        } else if (deathInfo.getSource() instanceof Damage.EntitySource entitySource) {
            killerRef = entitySource.getRef();
        } else {
            return;
        }

        if (!killerRef.isValid()) return;

        // Only reward players
        PlayerRef playerRef = store.getComponent(killerRef, PlayerRef.getComponentType());
        if (playerRef == null) return;

        // Get the killed entity's role name
        NPCEntity npcEntity = store.getComponent(ref, NPCEntity.getComponentType());
        if (npcEntity == null) return;

        String roleName = npcEntity.getRoleName();
        if (roleName == null) return;

        System.out.println("[BR Debug] Player killed: " + roleName);

        // Update kill counters
        playerStats stats = store.getComponent(killerRef, playerStats.getComponentType());
        if (stats != null) {
            if (roleName.contains("Hollow")) {
                stats.incrementHollowKills();
            } else {
                stats.incrementKills();
            }
        }

        Float reward = KILL_REWARDS.get(roleName);
        if (reward == null || reward <= 0) return;

        BleachStatTypes.addMaxReiryoku(killerRef, store, reward);
        System.out.println("[BR Debug] Max Reiryoku increased by " + reward + " for killing " + roleName);
    }
}
