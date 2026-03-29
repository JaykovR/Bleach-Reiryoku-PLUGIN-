package com.bleachreiryoku.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackSlotTransaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import com.bleachreiryoku.playerData.playerStats;

import java.awt.*;

// This class unlocks x said Shikai to a player depending on which item they use to do so.
public class UnlockShikaiInteraction extends SimpleInteraction {

    //Builder codec in order for it to be an intereaction available.
    public static final BuilderCodec<UnlockShikaiInteraction> CODEC =
            BuilderCodec.builder(UnlockShikaiInteraction.class, UnlockShikaiInteraction::new,
                    SimpleInteraction.CODEC).build();

    @Override
    protected void tick0(boolean firstRun, float time, @NonNullDecl InteractionType type, @NonNullDecl InteractionContext context,
                         @NonNullDecl CooldownHandler cooldownHandler) {
        // References player as owning Entity, gets their inventory.
        Ref<EntityStore> owningEntity = context.getOwningEntity();
        Store<EntityStore> store = owningEntity.getStore();

        Player player = store.getComponent(owningEntity, Player.getComponentType());
        if (player == null) return;


        World world = player.getWorld();
        if (world == null) return;

        // Gets their held item ID.
        String heldItem = context.getHeldItem().getItemId();
        int requiredAmount = 1;

        if (heldItem == null) return;

        var brType = playerStats.getComponentType();
        if (brType == null) {
            player.sendMessage(Message.raw("ERROR PLAYER STATS NULL")); // Error prevention
            return;}
        playerStats stats = store.getComponent(owningEntity, brType); // Gets player stats

        // IF they have X item then they get the messages for unlock. After that the boolean for the shikai becomes true. Unlocking shikai for the player.
        if(heldItem.equals("Benihime_Spirit_Fragment")){
            player.sendMessage(Message.translation("shikaiUnlockMessages.Benihime.unlock"));
            player.sendMessage(Message.translation("shikaiUnlockMessages.Benihime2.unlock").bold(true).color(Color.RED).italic(true));
            player.sendMessage(Message.translation("shikaiUnlockMessages.Usage_cue.unlock").bold(true).color(Color.WHITE));
            ItemStackSlotTransaction itemStackSlotTransaction = player.getInventory().getHotbar().removeItemStackFromSlot(context.getHeldItemSlot(),
                    requiredAmount, true, false);
            if(!itemStackSlotTransaction.succeeded()) return;
            if (stats != null) {
                stats.setActiveShikaiBenihime();
            }
        }

        if(heldItem.equals("Wabisuke_Spirit_Fragment")){
            player.sendMessage(Message.translation("shikaiUnlockMessages.Wabisuke.unlock"));
            player.sendMessage(Message.translation("shikaiUnlockMessages.Wabisuke2.unlock").bold(true).color(Color.MAGENTA).italic(true));
            player.sendMessage(Message.translation("shikaiUnlockMessages.Usage_cue.unlock").bold(true).color(Color.WHITE));
            ItemStackSlotTransaction itemStackSlotTransaction = player.getInventory().getHotbar().removeItemStackFromSlot(context.getHeldItemSlot(),
                    requiredAmount, true, false);
            if(!itemStackSlotTransaction.succeeded()) return;
            stats.setActiveShikaiWabisuke();
        }

        if(heldItem.equals("SodeNoShirayuki_Spirit_Fragment")){
           player.sendMessage(Message.translation("shikaiUnlockMessages.Sode_No_Shirayuki.unlock"));
           player.sendMessage(Message.translation("shikaiUnlockMessages.Sode_No_Shirayuki2.unlock").bold(true).color(Color.WHITE).italic(true));
           player.sendMessage(Message.translation("shikaiUnlockMessages.Usage_cue.unlock").bold(true).color(Color.WHITE));
            ItemStackSlotTransaction itemStackSlotTransaction = player.getInventory().getHotbar().removeItemStackFromSlot(context.getHeldItemSlot(),
                    requiredAmount, true, false);
            if(!itemStackSlotTransaction.succeeded()) return;
            if (stats != null) {
                stats.setActiveShikaiSodeNoShirayuki();
            }
        }

        if(heldItem.equals("Hozukimaru_Spirit_Fragment")){
            player.sendMessage(Message.translation("shikaiUnlockMessages.Hozukimaru.unlock"));
            player.sendMessage(Message.translation("shikaiUnlockMessages.Hozukimaru2.unlock").bold(true).color(Color.RED).italic(true));
            player.sendMessage(Message.translation("shikaiUnlockMessages.Usage_cue.unlock").bold(true).color(Color.WHITE));
            ItemStackSlotTransaction itemStackSlotTransaction = player.getInventory().getHotbar().removeItemStackFromSlot(context.getHeldItemSlot(),
                    requiredAmount, true, false);
            if(!itemStackSlotTransaction.succeeded()) return;
            if (stats != null) {
                stats.setActiveShikaiHozukimaru();
            }
        }

        if(heldItem.equals("Senbonzakura_Spirit_Fragment")){
            player.sendMessage(Message.translation("shikaiUnlockMessages.Senbonzakura.unlock"));
            player.sendMessage(Message.translation("shikaiUnlockMessages.Senbonzakura2.unlock").bold(true).color(Color.RED).italic(true));
            player.sendMessage(Message.translation("shikaiUnlockMessages.Usage_cue.unlock").bold(true).color(Color.WHITE));
            ItemStackSlotTransaction itemStackSlotTransaction = player.getInventory().getHotbar().removeItemStackFromSlot(context.getHeldItemSlot(),
                    requiredAmount, true, false);
            if(!itemStackSlotTransaction.succeeded()) return;
            if (stats != null) {
                stats.setActiveShikaiSenbonzakura();
            }
        }

        if(stats==null) return;


    }
}
