package com.bleachreiryoku.systems;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.AnimationSlot;
import com.hypixel.hytale.protocol.packets.entities.PlayAnimation;
import com.hypixel.hytale.server.core.entity.AnimationUtils;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

/** Owns the meditation pose independently of the movement-lock entity effect. */
public final class MeditationAnimations {
    private MeditationAnimations() {}

    public static void start(Ref<EntityStore> ref, Store<EntityStore> store) {
        send(ref, store, AnimationSlot.Status, "SitGround");
    }

    public static void stop(Ref<EntityStore> ref, Store<EntityStore> store) {
        send(ref, store, AnimationSlot.Status, null);
        // Clear the old entity-effect pose too, for players testing a previous build.
        send(ref, store, AnimationSlot.ServerAction, null);
    }

    private static void send(Ref<EntityStore> ref, Store<EntityStore> store,
                             AnimationSlot slot, String animationId) {
        if (!ref.isValid()) return;
        NetworkId networkId = store.getComponent(ref, NetworkId.getComponentType());
        if (networkId == null) return;
        // Viewer broadcasts are not a guarantee that the owning client receives the packet.
        AnimationUtils.playAnimation(ref, slot, animationId, false, store);
        PlayerRef player = store.getComponent(ref, PlayerRef.getComponentType());
        if (player != null) {
            player.getPacketHandler().writeNoCache(new PlayAnimation(networkId.getId(), null, animationId, slot));
        }
    }
}
