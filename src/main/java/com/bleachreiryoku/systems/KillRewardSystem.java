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
 *
 * Enemies are classified in tiers judging by their difficulty, total HP and damage that they make.
 * The harder the more Reiyoku they give.
 */
public class KillRewardSystem extends DeathSystems.OnDeathSystem {

    private static final Map<String, Float> KILL_REWARDS = Map.ofEntries(
            // ===== BR Hollow Enemies (Mod) =====
            Map.entry("Ghoul_Hollow_Elite", 10.0f), //  HP 500, DMG 50
            Map.entry("Ghoul_Hollow_Big", 10.0f), //  HP 300, DMG 48
            Map.entry("Ghoul_Hollow", 7.0f), // HP 230, DMG 40
            Map.entry("Ghoul_Hollow_Small", 3.0f), // HP 85, DMG 25
            Map.entry("Scorpion_Hollow_Big", 7.0f), // HP 310, DMG 45
            Map.entry("Scorpion_Hollow", 7.0f), // HP 210, DMG 35
            Map.entry("Scarak_Fighter_Hollow_Big", 7.0f), // HP 320, DMG 35
            Map.entry("Scarak_Defender_Hollow", 10.0f), // HP 300
            Map.entry("Scarak_Fighter_Hollow", 7.0f), // HP 255, DMG 35
            Map.entry("Scarak_Louse_Hollow", 3.0f), // HP 80
            Map.entry("Crawler_Hollow_Big", 7.0f), // HP 320, DMG 40
            Map.entry("Crawler_Hollow_Void", 5.0f), // HP 155, DMG 35
            Map.entry("Crawler_Hollow_Small", 3.0f), // HP 80, DMG 20
            Map.entry("Prangus_Hollow", 7.0f), // HP 200

            // ===== Vanilla Creatures =====
            // -- Creature --
            Map.entry("Rex_Cave", 10.0f), // HP 400, DMG 68
            Map.entry("Yeti", 7.0f), // HP 226
            Map.entry("Emberwulf", 5.0f), // HP 193, DMG 64
            Map.entry("Crocodile", 5.0f), //HP 145, DMG 48
            Map.entry("Bear_Grizzly", 3.0f), // HP 124, DMG 38
            Map.entry("Tiger_Sabertooth", 3.0f), // HP 124, DMG 46
            Map.entry("Toad_Rhino", 3.0f), // HP 124, DMG 35
            Map.entry("Toad_Rhino_Magma", 3.0f), // HP 124, DMG 35
            Map.entry("Scorpion", 3.0f), // HP 124, DMG 35
            Map.entry("Bear_Polar", 5.0f), // HP 103, DMG 48
            Map.entry("Hyena", 5.0f), // HP 103, DMG 27
            Map.entry("Leopard_Snow", 5.0f), // HP 103, DMG 36
            Map.entry("Wolf_Black", 5.0f), // HP 103, DMG 27
            Map.entry("Wolf_White", 5.0f), // HP 103, DMG 27
            Map.entry("Snapdragon", 5.0f), // HP 103
            Map.entry("Raptor_Cave", 5.0f), // HP 103, DMG 27
            Map.entry("Slug_Magma", 5.0f), // HP 103
            Map.entry("Fen_Stalker", 3.0f), // HP 74, DMG 29
            Map.entry("Snake_Cobra", 3.0f), // HP 74, DMG 29
            Map.entry("Spider_Cave", 3.0f), // HP 74
            Map.entry("Molerat", 3.0f), // HP 61, DMG 23
            Map.entry("Snake_Rattle", 3.0f), // HP 61, DMG 23
            Map.entry("Spider", 3.0f), // HP 61
            Map.entry("Fox", 2.0f), // HP 38, DMG 17
            Map.entry("Snake_Marsh", 2.0f), // HP 36, DMG 14
            Map.entry("Larva_Silk", 2.0f), // HP 25, DMG 12
            Map.entry("Rat", 2.0f), // HP 21, DMG 9

            // -- Elemental --
            Map.entry("Golem_Guardian_Void", 10.0f), // HP 400
            Map.entry("Golem_Crystal_Flame", 10.0f), // HP 283, DMG 10
            Map.entry("Spirit_Thunder", 7.0f), // HP 249
            Map.entry("Golem_Crystal_Frost", 7.0f), // HP 224, DMG 10
            Map.entry("Golem_Firesteel", 7.0f), // HP 224, DMG 10
            Map.entry("Golem_Crystal_Sand", 5.0f), // HP 193, DMG 47
            Map.entry("Golem_Crystal_Thunder", 5.0f), // HP 193, DMG 10
            Map.entry("Golem_Crystal_Earth", 5.0f), // HP 160, DMG 10
            Map.entry("Spirit_Ember", 3.0f), // HP 126
            Map.entry("Spirit_Frost", 3.0f), // HP 81
            Map.entry("Spirit_Root", 2.0f), // HP 49

            // -- Undead --
            Map.entry("Shadow_Knight", 10.0f), // HP 400, DMG 119
            Map.entry("Zombie_Aberrant", 10.0f), // HP 400, DMG 119
            Map.entry("Zombie_Aberrant_Big", 7.0f), // HP 341, DMG 86
            Map.entry("Werewolf", 10.0f), // HP 283, DMG 66
            Map.entry("Skeleton_Burnt_Praetorian", 7.0f), // HP 226
            Map.entry("Ghoul", 5.0f), // HP 193, DMG 48
            Map.entry("Wraith", 5.0f), // HP 193, DMG 40
            Map.entry("Hound_Bleached", 3.0f), // HP 126, DMG 30
            Map.entry("Zombie_Aberrant_Small", 3.0f), // HP 126, DMG 30
            Map.entry("Zombie_Burnt", 3.0f), // HP 126, DMG 30
            Map.entry("Cow_Undead", 3.0f), // HP 124, DMG 35
            Map.entry("Skeleton_Burnt_Soldier", 3.0f), // HP 124
            Map.entry("Pig_Undead", 5.0f), // HP 103, DMG 27
            Map.entry("Skeleton_Burnt_Alchemist", 5.0f), // HP 103
            Map.entry("Skeleton_Burnt_Knight", 5.0f), // HP 103
            Map.entry("Skeleton_Burnt_Lancer", 5.0f), // HP 103
            Map.entry("Skeleton_Incandescent_Fighter", 5.0f), // HP 103
            Map.entry("Skeleton_Incandescent_Footman", 5.0f), // HP 103
            Map.entry("Skeleton_Incandescent_Mage", 5.0f), // HP 103
            Map.entry("Skeleton_Pirate_Striker", 5.0f), // HP 103
            Map.entry("Skeleton", 5.0f), // HP 92
            Map.entry("Skeleton_Archmage", 3.0f), // HP 88
            Map.entry("Risen_Gunner", 3.0f), // HP 81
            Map.entry("Skeleton_Burnt_Gunner", 3.0f), // HP 81
            Map.entry("Skeleton_Burnt_Wizard", 3.0f), // HP 81
            Map.entry("Skeleton_Incandescent_Head", 3.0f), // HP 81
            Map.entry("Zombie_Frost", 3.0f), // HP 81, DMG 21
            Map.entry("Risen_Knight", 3.0f), // HP 74
            Map.entry("Skeleton_Knight", 3.0f), // HP 74
            Map.entry("Skeleton_Burnt_Archer", 3.0f), // HP 74
            Map.entry("Skeleton_Frost_Fighter", 3.0f), // HP 74
            Map.entry("Skeleton_Frost_Knight", 3.0f), // HP 74
            Map.entry("Skeleton_Frost_Soldier", 3.0f), // HP 74
            Map.entry("Chicken_Undead", 3.0f), // HP 61, DMG 23
            Map.entry("Skeleton_Scout", 3.0f), // HP 61
            Map.entry("Skeleton_Soldier", 3.0f), // HP 61
            Map.entry("Skeleton_Frost_Archer", 3.0f), // HP 61
            Map.entry("Skeleton_Frost_Archmage", 3.0f), // HP 61
            Map.entry("Skeleton_Frost_Mage", 3.0f), // HP 61
            Map.entry("Skeleton_Frost_Ranger", 3.0f), // HP 61
            Map.entry("Skeleton_Pirate_Captain", 3.0f), // HP 61
            Map.entry("Skeleton_Pirate_Gunner", 3.0f), // HP 61
            Map.entry("Dungeon_Skeleton_Sand_Archer", 3.0f), // HP 61
            Map.entry("Dungeon_Skeleton_Sand_Assassin", 3.0f), // HP 61
            Map.entry("Dungeon_Skeleton_Sand_Soldier", 3.0f), // HP 61
            Map.entry("Skeleton_Sand_Archer", 3.0f), // HP 61
            Map.entry("Skeleton_Sand_Assassin", 3.0f), // HP 61
            Map.entry("Skeleton_Sand_Guard", 3.0f), // HP 61
            Map.entry("Skeleton_Sand_Soldier", 3.0f), // HP 61
            Map.entry("Skeleton_Mage", 2.0f), // HP 49
            Map.entry("Skeleton_Ranger", 2.0f), // HP 49
            Map.entry("Skeleton_Frost_Scout", 2.0f), // HP 49
            Map.entry("Dungeon_Skeleton_Sand_Mage", 2.0f), // HP 49
            Map.entry("Skeleton_Sand_Mage", 2.0f), // HP 49
            Map.entry("Zombie", 2.0f), // HP 49, DMG 18
            Map.entry("Zombie_Sand", 2.0f), // HP 49, DMG 18
            Map.entry("Skeleton_Sand_Archmage", 2.0f), // HP 38
            Map.entry("Skeleton_Archer", 2.0f), // HP 36
            Map.entry("Skeleton_Fighter", 2.0f), // HP 36
            Map.entry("Wraith_Lantern", 2.0f), // HP 30, DMG 10
            Map.entry("Skeleton_Sand_Ranger", 2.0f), // HP 29
            Map.entry("Skeleton_Fighter_Wander", 2.0f), // HP 29
            Map.entry("Skeleton_Sand_Scout", 2.0f), // HP 29

            // -- Void --
            Map.entry("Spawn_Void", 5.0f), // HP 193, DMG 48
            Map.entry("Spectre_Void", 5.0f), // HP 103
            Map.entry("Crawler_Void", 3.0f), // HP 74
            Map.entry("Eye_Void", 3.0f), // HP 61
            Map.entry("Larva_Void", 2.0f), // HP 36

            // -- Intelligent --
            Map.entry("Goblin_Duke_Phase_2", 7.0f), // HP 320
            Map.entry("Goblin_Duke", 7.0f), // HP 226
            Map.entry("Hedera", 7.0f), // HP 226
            Map.entry("Goblin_Duke_Phase_3_Fast", 7.0f), // HP 200
            Map.entry("Goblin_Duke_Phase_3_Slow", 7.0f), // HP 200
            Map.entry("Dungeon_Scarak_Broodmother", 5.0f), // HP 145
            Map.entry("Scarak_Broodmother", 5.0f), // HP 145
            Map.entry("Goblin_Ogre", 3.0f), // HP 124
            Map.entry("Goblin_Ogre_Tutorial", 3.0f), // HP 124, DMG 20
            Map.entry("Outlander_Brute", 3.0f), // HP 124, DMG 35
            Map.entry("Dungeon_Scarak_Broodmother_Young", 3.0f), // HP 124
            Map.entry("Trork_Chieftain", 3.0f), // HP 124, DMG 35
            Map.entry("Wolf_Outlander_Sorcerer", 3.0f), // HP 118, DMG 5
            Map.entry("Wolf_Trork_Shaman", 3.0f), // HP 118, DMG 5
            Map.entry("Wolf_Outlander_Priest", 3.0f), // HP 107, DMG 5
            Map.entry("Outlander_Berserker", 5.0f), // HP 103, DMG 27
            Map.entry("Outlander_Marauder", 5.0f), // HP 103, DMG 27
            Map.entry("Outlander_Priest", 5.0f), // HP 103
            Map.entry("Outlander_Sorcerer", 5.0f), // HP 103
            Map.entry("Dungeon_Scarak_Defender", 5.0f), // HP 103
            Map.entry("Scarak_Defender", 5.0f), // HP 103
            Map.entry("Outlander_Cultist", 3.0f), // HP 81, DMG 21
            Map.entry("Outlander_Peon", 3.0f), // HP 81, DMG 21
            Map.entry("Dungeon_Scarak_Fighter", 3.0f), // HP 81
            Map.entry("Scarak_Fighter", 3.0f), // HP 81
            Map.entry("Scarak_Fighter_Royal_Guard", 3.0f), // HP 81
            Map.entry("Trork_Doctor_Witch", 3.0f), // HP 74
            Map.entry("Outlander_Hunter", 3.0f), // HP 61, DMG 23
            Map.entry("Outlander_Stalker", 3.0f), // HP 61, DMG 23
            Map.entry("Dungeon_Scarak_Seeker", 3.0f), // HP 61
            Map.entry("Scarak_Seeker", 3.0f), // HP 61
            Map.entry("Trork_Brawler", 3.0f), // HP 61, DMG 23
            Map.entry("Trork_Guard", 3.0f), // HP 61, DMG 23
            Map.entry("Trork_Hunter", 3.0f), // HP 61, DMG 23
            Map.entry("Trork_Mauler", 3.0f), // HP 61, DMG 23
            Map.entry("Trork_Sentry", 3.0f), // HP 61, DMG 23
            Map.entry("Trork_Shaman", 3.0f), // HP 61
            Map.entry("Trork_Warrior", 3.0f), // HP 61, DMG 23
            Map.entry("Goblin_Scavenger", 3.0f), // HP 54
            Map.entry("Goblin_Scavenger_Battleaxe", 3.0f), // HP 54
            Map.entry("Goblin_Scavenger_Sword", 3.0f), // HP 54
            Map.entry("Goblin_Lobber", 2.0f), // HP 48
            Map.entry("Trork_Unarmed", 2.0f), // HP 42
            Map.entry("Goblin_Hermit", 2.0f), // HP 38
            Map.entry("Goblin_Miner", 2.0f), // HP 38
            Map.entry("Goblin_Scrapper", 2.0f), // HP 38
            Map.entry("Goblin_Thief", 2.0f), // HP 38
            Map.entry("Wolf_Trork_Hunter", 2.0f), // HP 38, DMG 6
            Map.entry("Dungeon_Scarak_Louse", 2.0f), // HP 21
            Map.entry("Scarak_Louse", 2.0f), // HP 21

            // -- Boss --
            Map.entry("Dragon_Fire", 10.0f), // HP 400
            Map.entry("Dragon_Frost", 10.0f), // HP 400

            // -- Aquatic --
            Map.entry("Piranha_Black", 2.0f), // HP 45
            Map.entry("Piranha", 2.0f) // HP 38
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
