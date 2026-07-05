package com.bleachreiryoku.interactions;

import com.bleachreiryoku.playerData.playerStats;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
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
 * Checks if the player's primary race matches the desired race.
 * If the player's race does not match, the interaction fails. Otherwise it passes.
 */
public class PlayerRaceCheckInteraction extends SimpleInstantInteraction {

    // Mirrors playerStats.RACE_SHINIGAMI / RACE_QUINCY. Kept as its own enum so the
    // field renders as dropdown in the asset editor instead of free text.
    public enum DesiredRace {
        Shinigami,
        Quincy,
        Hollow,
        Fullbringer
    }

    public DesiredRace desiredRace = DesiredRace.Shinigami;

    public static final BuilderCodec<PlayerRaceCheckInteraction> CODEC =
            BuilderCodec.builder(PlayerRaceCheckInteraction.class, PlayerRaceCheckInteraction::new,
                            SimpleInteraction.CODEC)
                    .append(
                            new KeyedCodec<>("DesiredRace", new EnumCodec<>(DesiredRace.class)),
                            (obj, value) -> obj.desiredRace = value,
                            obj -> obj.desiredRace
                    ).add()
                    .build();

    @Override
    protected void firstRun(@NonNullDecl InteractionType interactionType,
                            @NonNullDecl InteractionContext context,
                            @NonNullDecl CooldownHandler cooldownHandler) {

        Ref<EntityStore> owningEntity = context.getOwningEntity();
        Store<EntityStore> store = owningEntity.getStore();

        playerStats stats = store.getComponent(owningEntity, playerStats.getComponentType());

        PlayerRef playerRef = store.getComponent(owningEntity, PlayerRef.getComponentType());

        if (stats == null) {
            if (playerRef != null) {
                playerRef.sendMessage(Message.raw("ERROR PLAYER STATS NULL"));
            }
            context.getState().state = InteractionState.Failed;
            return;
        }

        String requiredRace = switch (desiredRace) {
            case Quincy      -> playerStats.RACE_QUINCY;
            case Hollow      -> playerStats.RACE_HOLLOW;
            case Fullbringer -> playerStats.RACE_FULLBRINGER;
            default           -> playerStats.RACE_SHINIGAMI;
        };

        if (!requiredRace.equals(stats.playerPrimaryRace)) {
            if (playerRef != null) {
                playerRef.sendMessage(
                        Message.translation("server.interaction.racecheck_fail")
                                .param("race", requiredRace)
                );
            }
            context.getState().state = InteractionState.Failed;
            return;
        }

        context.getState().state = InteractionState.Finished;
    }
}
