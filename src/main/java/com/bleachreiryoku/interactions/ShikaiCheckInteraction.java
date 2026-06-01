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
import com.hypixel.hytale.server.core.universe.PlayerRef;

import java.awt.*;

import static com.hypixel.hytale.protocol.InteractionState.*;

// This class checks if the player has X said Shikai.
public class ShikaiCheckInteraction extends SimpleInstantInteraction {

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
        PlayerRef playerRef = store.getComponent(owningEntity, PlayerRef.getComponentType());
        if (playerRef == null) return;

        World world = store.getExternalData().getWorld();
        if (world == null) return;

        var brType = playerStats.getComponentType();
        playerStats stats = store.getComponent(owningEntity, brType);

        if (stats == null) {
            playerRef.sendMessage(Message.raw("ERROR PLAYER STATS NULL"));
            return;
        }

        if (heldItem.equals("Weapon_Sword_Sealed_Benihime")) {
            if (!stats.getShikaiBenihimeState()) {
                playerRef.sendMessage(Message.translation("shikaiCheckMessages.Failed.check"));
                context.getState().state = InteractionState.Failed;
                return;
            } else {
                playerRef.sendMessage(Message.translation("shikaiCheckMessages.Benihime_Shikai.activation").bold(true).color(Color.RED).italic(true));
                context.getState().state = Finished;
                return;
            }
        }

        if (heldItem.equals("Weapon_Sword_Sealed_Wabisuke")) {
            if (!stats.getShikaiWabisukeState()) {
                playerRef.sendMessage(Message.translation("shikaiCheckMessages.Failed.check"));
                context.getState().state = InteractionState.Failed;
                return;
            } else {
                playerRef.sendMessage(Message.translation("shikaiCheckMessages.Wabisuke_Shikai.activation").bold(true).color(Color.MAGENTA));
                context.getState().state = Finished;
                return;
            }
        }

        if (heldItem.equals("Weapon_Sword_Sealed_Hozukimaru")) {
            if (!stats.getShikaiHozukimaruState()) {
                playerRef.sendMessage(Message.translation("shikaiCheckMessages.Failed.check"));
                context.getState().state = InteractionState.Failed;
                return;
            } else {
                playerRef.sendMessage(Message.translation("shikaiCheckMessages.Hozukimaru_Shikai.activation").bold(true).color(Color.RED));
                context.getState().state = Finished;
                return;
            }
        }

        if (heldItem.equals("Weapon_Sword_Sealed_Sode_No_Shirayuki")) {
            if (!stats.getShikaiSodeNoShirayukiState()) {
                playerRef.sendMessage(Message.translation("shikaiCheckMessages.Failed.check"));
                context.getState().state = InteractionState.Failed;
                return;
            } else {
                playerRef.sendMessage(Message.translation("shikaiCheckMessages.Sode_No_Shirayuki_Shikai.activation").bold(true).color(Color.WHITE).italic(true));
                context.getState().state = Finished;
                return;
            }
        }

        if (heldItem.equals("Weapon_Sword_Sealed_Senbonzakura")) {
            if (!stats.getShikaiSenbonzakuraState()) {
                playerRef.sendMessage(Message.translation("shikaiCheckMessages.Failed.check"));
                context.getState().state = InteractionState.Failed;
                return;
            } else {
                playerRef.sendMessage(Message.translation("shikaiCheckMessages.Senbonzakura_Shikai.activation").bold(true).color(Color.pink).italic(true));
                context.getState().state = Finished;
                return;
            }
        }

        if (heldItem.equals("Weapon_Sword_Sealed_Zangetsu")) {
            if (!stats.getShikaiZangetsuState()) {
                playerRef.sendMessage(Message.translation("shikaiCheckMessages.Failed.check"));
                context.getState().state = InteractionState.Failed;
                return;
            } else {
                playerRef.sendMessage(Message.translation("shikaiCheckMessages.Zangetsu_Shikai.activation").bold(true).color(Color.ORANGE).italic(true));
                context.getState().state = Finished;
                return;
            }
        }

        // While this class is called Shikai Check, I decided to also include this for now.
        if (heldItem.equals("Weapon_Sword_Shikai_Zangetsu")) {
            if (!stats.getBankaiZangetsuState()) {
                playerRef.sendMessage(Message.translation("shikaiCheckMessages.Failed.check"));
                context.getState().state = InteractionState.Failed;
                return;
            } else {
                playerRef.sendMessage(Message.translation("shikaiCheckMessages.Zangetsu_Bankai.activation").bold(true).color(Color.RED));
                playerRef.sendMessage(Message.translation("shikaiCheckMessages.Zangetsu_Bankai.activation2").bold(true).color(Color.BLACK).italic(true));
                context.getState().state = Finished;
                return;
            }
        }

        // I ended up using this as a general stat verifier. Maybe I'll improve it later, for now it works.
        if (heldItem.equals("Weapon_Sword_Bankai_Tensa_Zangetsu")) {
            if (!stats.getHollowMaskState()) {
                playerRef.sendMessage(Message.raw("Hollow Mask has not been unlocked."));
                context.getState().state = InteractionState.Failed;
                return;
            } else {
                context.getState().state = Finished;
                return;
            }
        }

        context.getState().state = Finished;
    }
}
