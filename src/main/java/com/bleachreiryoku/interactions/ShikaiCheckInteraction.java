package com.bleachreiryoku.interactions;

import com.bleachreiryoku.playerData.playerStats;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import static com.hypixel.hytale.protocol.InteractionState.*;

public class ShikaiCheckInteraction extends SimpleInstantInteraction{

    public static final BuilderCodec<ShikaiCheckInteraction> CODEC =
            BuilderCodec.builder(ShikaiCheckInteraction.class, ShikaiCheckInteraction::new,
                    SimpleInteraction.CODEC).build();


    @Override
    protected void firstRun(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext context, @NonNullDecl CooldownHandler cooldownHandler) {
        Ref<EntityStore> owningEntity = context.getOwningEntity();
        Store<EntityStore> store = owningEntity.getStore();
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();

        Player player = store.getComponent(owningEntity, Player.getComponentType());
        if (player == null) return;

        World world = player.getWorld();
        if (world == null) return;

        var brType = playerStats.getComponentType();
        playerStats stats = store.getComponent(owningEntity, brType);

        if(stats == null || stats.getShikaiState() == 0){
            player.sendMessage(Message.raw("You are unable to transform into Shikai yet."));
            context.getState().state = InteractionState.Failed;
            return;
        }

        context.getState().state = Finished;
    }
}
