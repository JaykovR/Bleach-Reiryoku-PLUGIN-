package com.bleachreiryoku.effects;

import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;

public final class BleachStatTypes {

    public static final int NOT_FOUND = Integer.MIN_VALUE;
    private static int REIRYOKU = NOT_FOUND;

    private BleachStatTypes() {}

    public static void update() {
        var assetMap = EntityStatType.getAssetMap();
        REIRYOKU = assetMap.getIndex("Reiryoku");

        if (REIRYOKU == NOT_FOUND) {
            System.err.println("[Bleach Reiryoku] WARNING: Reiryoku stat not found! " +
                    "Make sure Server/Entity/Stats/Reiryoku.json exists in your asset pack.");
        }
    }

    public static int getReiryoku() {
        return REIRYOKU;
    }

    public static float getValue(Ref<EntityStore> ref, Store<EntityStore> store) {
        EntityStatMap stats = store.getComponent(ref, EntityStatMap.getComponentType());
        return stats != null ? stats.get(REIRYOKU).get() : -1f;
    }

    public static void setValue(Ref<EntityStore> ref, Store<EntityStore> store, float value) {
        EntityStatMap stats = store.getComponent(ref, EntityStatMap.getComponentType());
        if (stats != null) stats.setStatValue(REIRYOKU, value);
    }

    public static void modify(Ref<EntityStore> ref, Store<EntityStore> store, float delta) {
        EntityStatMap stats = store.getComponent(ref, EntityStatMap.getComponentType());
        if (stats != null) stats.setStatValue(REIRYOKU, stats.get(REIRYOKU).get() + delta);
    }

    public static boolean hasEnough(Ref<EntityStore> ref, Store<EntityStore> store, float amount) {
        return getValue(ref, store) >= amount;
    }

    public static void addMaxReiryoku(Ref<EntityStore> ref, Store<EntityStore> store, float amount) {
        EntityStatMap stats = store.getComponent(ref, EntityStatMap.getComponentType());
        if (stats == null) return;

        // Read existing bonus from the single permanent key
        float currentBonus = 0f;
        var statValue = stats.get(REIRYOKU);
        if (statValue != null) {
            var existing = statValue.getModifier("BR_MaxBonus");
            if (existing instanceof StaticModifier sm) {
                currentBonus = sm.getAmount();
            }
        }

        // Store total accumulated bonus under one key
        stats.putModifier(REIRYOKU, "BR_MaxBonus", new StaticModifier(
                Modifier.ModifierTarget.MAX,
                StaticModifier.CalculationType.ADDITIVE,
                currentBonus + amount
        ));
    }

    public static void removeMaxReiryoku(Ref<EntityStore> ref, Store<EntityStore> store, String key) {
        EntityStatMap stats = store.getComponent(ref, EntityStatMap.getComponentType());
        if (stats == null) return;
        stats.putModifier(REIRYOKU, key, null);
    }
}