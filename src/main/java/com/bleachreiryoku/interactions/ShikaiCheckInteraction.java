package com.bleachreiryoku.interactions;

import com.bleachreiryoku.playerData.playerStats;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;

import java.awt.*;

import static com.hypixel.hytale.protocol.InteractionState.*;

// This class checks if the player has X said Shikai.
public class    ShikaiCheckInteraction extends SimpleInstantInteraction{

    public static final BuilderCodec<ShikaiCheckInteraction> CODEC =
            BuilderCodec.builder(ShikaiCheckInteraction.class, ShikaiCheckInteraction::new,
                    SimpleInteraction.CODEC).build();


    @Override
    protected void firstRun(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext context, @NonNullDecl CooldownHandler cooldownHandler) {
        // References player, gets item in hand
        Ref<EntityStore> owningEntity = context.getOwningEntity();
        Store<EntityStore> store = owningEntity.getStore();
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        String heldItem = context.getHeldItem().getItemId();
        int requiredAmount = 1;


        // Prevention for errors
        Player player = store.getComponent(owningEntity, Player.getComponentType());
        if (player == null) return;


        World world = player.getWorld();
        if (world == null) return;

        var brType = playerStats.getComponentType();
        playerStats stats = store.getComponent(owningEntity, brType);

        // We use the stats from the player, in this case if they have the boolean of the respective shikai on or off.
        if(stats == null){
            player.sendMessage(Message.raw("ERROR PLAYER STATS NULL"));
            return;
        }

        // Checks if they have x said item but they dont have the shikai, then they get the Unable to transform message. PENDING TRANSLATION
        // else (meaning if they have the shikai available) then the interaction succeeds. After that in game the player changes item to the Shikai Weapon
        if(heldItem.equals("Sealed_Benihime")){
            if (!stats.getShikaiBenihimeState()) {
                player.sendMessage(Message.raw("You are unable to transform into Shikai yet."));
                context.getState().state = InteractionState.Failed;
                return;
            } else {
                player.sendMessage(Message.raw("Awake, Benihime").bold(true).color(Color.RED).italic(true));
                context.getState().state = Finished;
                return;
            }
        }

        if(heldItem.equals("Sealed_Wabisuke")){
            if (!stats.getShikaiWabisukeState()) {
                player.sendMessage(Message.raw("You are unable to transform into Shikai yet."));
                context.getState().state = InteractionState.Failed;
                return;
            } else {
                player.sendMessage(Message.raw("Raise Your Hear, Wabisuke!").bold(true).color(Color.MAGENTA));
                context.getState().state = Finished;
                return;
            }
        }

        if(heldItem.equals("Sealed_Hozukimaru")){
            if (!stats.getShikaiHozukimaruState()) {
                player.sendMessage(Message.raw("You are unable to transform into Shikai yet."));
                context.getState().state = InteractionState.Failed;
                return;
            } else {
                player.sendMessage(Message.raw("GROW, HOZUKIMARU!!").bold(true).color(Color.RED));
                context.getState().state = Finished;
                return;
            }
        }

        if(heldItem.equals("Sealed_Sode_No_Shirayuki")){
            if (!stats.getShikaiSodeNoShirayukiState()) {
                player.sendMessage(Message.raw("You are unable to transform into Shikai yet."));
                context.getState().state = InteractionState.Failed;
                return;
            } else {
                player.sendMessage(Message.raw("Dance, Sode no Shirayuki").bold(true).color(Color.WHITE).italic(true));
                context.getState().state = Finished;
                return;
            }
        }

        // TO DO
        if(heldItem.equals("Sealed_Senbonzakura")){
            if (!stats.getShikaiSenbonzakuraState()) {
                player.sendMessage(Message.raw("You are unable to transform into Shikai yet."));
                context.getState().state = InteractionState.Failed;
                return;
            } else {
                player.sendMessage(Message.raw("Scatter, Senbonzakura.").bold(true).color(Color.pink).italic(true));
                context.getState().state = Finished;
                return;
            }
        }
        context.getState().state = Finished;
    }
}
