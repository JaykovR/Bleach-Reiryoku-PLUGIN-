package com.bleachreiryoku.systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import com.bleachreiryoku.playerData.playerStats;

import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class playerJoinSystem extends RefSystem<EntityStore>{
    @Override
    public void onEntityAdded(@NonNullDecl Ref<EntityStore> ref,
                              @NonNullDecl AddReason addReason,
                              @NonNullDecl Store<EntityStore> store,
                              @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        if (addReason != AddReason.LOAD && addReason != AddReason.SPAWN) return;
        var playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef == null) return;

        var brType = playerStats.getComponentType();
        if (brType==null) return;
        var br = store.getComponent(ref, brType);

        if (br == null){
            commandBuffer.addComponent(ref, brType, new playerStats());
        }

    }

    @Override
    public void onEntityRemove(@NonNullDecl Ref<EntityStore> ref,
                               @NonNullDecl RemoveReason removeReason,
                               @NonNullDecl Store<EntityStore> store,
                               @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {

    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        var playerRefType = PlayerRef.getComponentType();
        if (playerRefType == null) return null;
        return Archetype.of(PlayerRef.getComponentType());
    }
}
