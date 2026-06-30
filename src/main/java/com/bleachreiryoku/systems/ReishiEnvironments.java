package com.bleachreiryoku.systems;

import com.hypixel.hytale.server.core.asset.type.environment.config.Environment;

import java.util.HashSet;
import java.util.Set;

/**
 * Classifies environments into reishi density tiers for Quincy regeneration.
 * In Hytale plants and stuff like that give life essence, which I am considering same as Reishi.
 *
 * RICH enviroments, meaning they are rich on Reishi Density. Magical Places, temples, mage stuff, tons of
 * vegetation. Excluding extremely common places for balance.

 * STANDARD enviroments, like ordinary lush surface biomes (forests, plains, shores,
 * mountains, swamps, jungles, villages). Baseline reishi.
 *
 * POOR enviroments, barren / lifeless places (caves, deserts, wastes, scrub,
 * savanna, plateaus, tundra, sewers, mineshafts, dungeons, the void,
 * underground, poisoned land). Little flora, little reishi.
 *
 * Environment name -> index resolution is done once, via
 * {@link Environment#getAssetMap()} (the same mechanism the engine's
 * EnvironmentCondition uses). Indices are cached in int sets for fast per-tick lookup.
 */
public final class ReishiEnvironments {

    public enum Reishi {
        POOR,
        STANDARD,
        RICH
    }

    // ---- Environment name lists -------------------------------------------------

    private static final String[] RICH_NAMES = {
            "Env_Zone1_Azure",
            // Volcanic depths (all tiers, all zones)
            "Env_Zone1_Caves_Volcanic_T1", "Env_Zone1_Caves_Volcanic_T2", "Env_Zone1_Caves_Volcanic_T3",
            "Env_Zone2_Caves_Volcanic_T1", "Env_Zone2_Caves_Volcanic_T2", "Env_Zone2_Caves_Volcanic_T3",
            "Env_Zone3_Caves_Volcanic_T1", "Env_Zone3_Caves_Volcanic_T2", "Env_Zone3_Caves_Volcanic_T3",
            "Env_Zone4_Caves_Volcanic", "Env_Zone4_Volcanoes",
            // Glacial
            "Env_Zone3_Caves_Glacial", "Env_Zone3_Glacial", "Env_Zone3_Glacial_Henges",
            // Temples / sacred sites
            "Env_Forgotten_Temple_Base", "Env_Forgotten_Temple_Exterior", "Env_Forgotten_Temple_Heart",
            "Env_Forgotten_Temple_Interior_Grand", "Env_Forgotten_Temple_Interior_Small",
            "Env_Forgotten_Temple_Interior_Tent", "Env_Temple_of_Gaia",
            // Portals / rifts
            "Env_Portals_Hedera", "Env_Portals_Oasis", "Env_Zone3_Hedera",
            // Mage towers (arcane saturation)
            "Env_Zone1_Mage_Towers", "Env_Zone2_Mage_Towers", "Env_Zone3_Mage_Towers", "Env_Zone4_Mage_Towers",
            // Special flora / spirit-touched groves
            "Env_Zone1_Kweebec", "Env_Zone2_Feran",
            // Graveyard (spiritually dense)
            "Env_Zone1_Graveyard",
            "Env_Zone4_Crucible"
    };

    private static final String[] POOR_NAMES = {
            // Goblin lair dungeons
            "Dungeon_GoblinLair_Duke", "Dungeon_GoblinLair_Ogre",
            "Dungeon_GoblinLair_RatCave", "Dungeon_GoblinLair_SpiderCave",
            // Void
            "Env_Default_Void", "Env_Void",
            // Zone 1 caves & barren
            "Env_Zone1_Caves", "Env_Zone1_Caves_Forests", "Env_Zone1_Caves_Goblins",
            "Env_Zone1_Caves_Mountains", "Env_Zone1_Caves_Plains", "Env_Zone1_Caves_Rats",
            "Env_Zone1_Caves_Spiders", "Env_Zone1_Caves_Swamps",
            "Env_Zone1_Dungeons", "Env_Zone1_Mineshafts",
            // Zone 2 caves, deserts & barren
            "Env_Zone2_Caves", "Env_Zone2_Caves_Deserts", "Env_Zone2_Caves_Goblins",
            "Env_Zone2_Caves_Plateaus", "Env_Zone2_Caves_Rats", "Env_Zone2_Caves_Savanna",
            "Env_Zone2_Caves_Scarak", "Env_Zone2_Caves_Scrub",
            "Env_Zone2_Deserts", "Env_Zone2_Dungeons", "Env_Zone2_Mineshafts",
            "Env_Zone2_Plateaus", "Env_Zone2_Savanna", "Env_Zone2_Scrub",
            // Zone 3 caves & barren
            "Env_Zone3_Caves", "Env_Zone3_Caves_Forests", "Env_Zone3_Caves_Mountains",
            "Env_Zone3_Caves_Spider", "Env_Zone3_Caves_Tundra",
            "Env_Zone3_Dungeons", "Env_Zone3_Mineshafts", "Env_Zone3_Tundra",
            "Env_Zone3_Overground_Poisoned",
            // Zone 4 caves & barren
            "Env_Zone4_Caves", "Env_Zone4_Dungeons", "Env_Zone4_Sewers", "Env_Zone4_Wastes",
            // Generic underground (no flora)
            "Zone1_Underground", "Zone2_Underground", "Zone3_Underground", "Zone4_Underground"
    };

    // ---- Resolved index caches --------------------------------------------------

    private static int[] richIndices;
    private static int[] poorIndices;
    private static boolean initialized = false;

    private ReishiEnvironments() {}


    private static synchronized void ensureInitialized() {
        if (initialized) return;
        richIndices = resolve(RICH_NAMES);
        poorIndices = resolve(POOR_NAMES);
        initialized = true;
    }

    private static int[] resolve(String[] names) {
        Set<Integer> out = new HashSet<>();
        for (String name : names) {
            int idx = Environment.getAssetMap().getIndex(name);
            // getIndex returns a sentinel (negative) for unknown names
            if (idx >= 0) out.add(idx);
        }
        int[] arr = new int[out.size()];
        int i = 0;
        for (int v : out) arr[i++] = v;
        return arr;
    }


    // If stated above then it clasifies into rich or poor. If not it goes into standard.
    public static Reishi classify(int environmentIndex) {
        ensureInitialized();
        if (contains(richIndices, environmentIndex)) return Reishi.RICH;
        if (contains(poorIndices, environmentIndex)) return Reishi.POOR;
        return Reishi.STANDARD;
    }

    private static boolean contains(int[] arr, int value) {
        for (int v : arr) {
            if (v == value) return true;
        }
        return false;
    }
}