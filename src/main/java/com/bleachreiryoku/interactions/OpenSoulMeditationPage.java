package com.bleachreiryoku.interactions;

import com.bleachreiryoku.ui.SoulMeditationPage;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

/**
 * Opens the Soul Meditation menu. Reads the interacted block's position so the menu's Meditate button
 * can teleport the player onto the seat.
 */
public class OpenSoulMeditationPage extends SimpleInstantInteraction {

    public static final BuilderCodec<OpenSoulMeditationPage> CODEC =
            BuilderCodec.builder(OpenSoulMeditationPage.class, OpenSoulMeditationPage::new, SimpleInteraction.CODEC)
                    .build();

    @Override
    protected void firstRun(@NonNullDecl InteractionType type,
                            @NonNullDecl InteractionContext context,
                            @NonNullDecl CooldownHandler cooldownHandler) {

        Ref<EntityStore> ref = context.getOwningEntity();
        Store<EntityStore> store = ref.getStore();

        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        Player player = store.getComponent(ref, Player.getComponentType());
        if (playerRef == null || player == null) {
            context.getState().state = InteractionState.Failed;
            return;
        }

        // Seat block position (the block the player used).
        double sx, sy, sz;
        BlockPosition block = context.getTargetBlock();
        if (block != null) {
            sx = block.x;
            sy = block.y;
            sz = block.z;
        }
        // Fall back to the player's own position if for some reason the block isn't in context.
        else {
            var transform = store.getComponent(ref,
                    com.hypixel.hytale.server.core.modules.entity.component.TransformComponent.getComponentType());
            if (transform == null) {
                context.getState().state = InteractionState.Failed;
                return;
            }
            var p = transform.getPosition();
            sx = Math.floor(p.x);
            sy = Math.floor(p.y) - 1;
            sz = Math.floor(p.z);
        }

        player.getPageManager().openCustomPage(ref, store, new SoulMeditationPage(playerRef, sx, sy, sz));
        context.getState().state = InteractionState.Finished;
    }
}
