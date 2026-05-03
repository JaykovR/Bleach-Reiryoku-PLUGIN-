package com.bleachreiryoku.effects;

import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public final class BleachStatTypes {

    public static final int NOT_FOUND = Integer.MIN_VALUE;
    private static int REIRYOKU = NOT_FOUND;

    private BleachStatTypes() {}

    public static void update() {
        var assetMap = EntityStatType.getAssetMap();
        REIRYOKU = assetMap.getIndex("Reiryoku");

        if (REIRYOKU == NOT_FOUND) {
            throw new IllegalStateException(
                    "[Bleach Reiryoku] Reiryoku stat not found! " +
                            "Make sure Server/Entity/Stats/Reiryoku.json exists in your asset pack."
            );
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
}