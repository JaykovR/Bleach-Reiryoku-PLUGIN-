package com.bleachreiryoku.interactions;

import com.bleachreiryoku.effects.BleachStatTypes;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

/**
 * Checks if the player MAX Reiryoku Amount is what stated
 * ex:
 *   "Type": "ReiryokuCheck",
 *   "RequiredMax": 600
 *
 * So checks if the player has 600 Reiryoku, otherwise it fails.
 */
public class ReiryokuCheckInteraction extends SimpleInstantInteraction {

    public float requiredMax = 0f;

    public static final BuilderCodec<ReiryokuCheckInteraction> CODEC =
            BuilderCodec.builder(ReiryokuCheckInteraction.class, ReiryokuCheckInteraction::new,
                            SimpleInteraction.CODEC)
                    .append(
                            new KeyedCodec<>("RequiredMax", Codec.FLOAT),
                            (obj, value) -> obj.requiredMax = value,
                            obj -> obj.requiredMax
                    ).add()
                    .build();

    @Override
    protected void firstRun(@NonNullDecl InteractionType interactionType,
                            @NonNullDecl InteractionContext context,
                            @NonNullDecl CooldownHandler cooldownHandler) {

        Ref<EntityStore> owningEntity = context.getOwningEntity();
        Store<EntityStore> store = owningEntity.getStore();

        float playerMax = BleachStatTypes.getMax(owningEntity, store);

        if (playerMax < requiredMax) {
            PlayerRef playerRef = store.getComponent(owningEntity, PlayerRef.getComponentType());
            if (playerRef != null) {
                playerRef.sendMessage(Message.translation("server.interaction.reiryokucheck_fail")
                        .param("required", String.valueOf((int) requiredMax)));
            }
            context.getState().state = InteractionState.Failed;
            return;
        }

        context.getState().state = InteractionState.Finished;
    }
}
