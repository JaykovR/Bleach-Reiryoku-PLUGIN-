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

public class UnlockShikaiInteraction extends SimpleInteraction {

    public static final BuilderCodec<UnlockShikaiInteraction> CODEC =
            BuilderCodec.builder(UnlockShikaiInteraction.class, UnlockShikaiInteraction::new,
                    SimpleInteraction.CODEC).build();

    @Override
    protected void tick0(boolean firstRun, float time, @NonNullDecl InteractionType type, @NonNullDecl InteractionContext context,
                         @NonNullDecl CooldownHandler cooldownHandler) {
        Ref<EntityStore> owningEntity = context.getOwningEntity();
        Store<EntityStore> store = owningEntity.getStore();

        Player player = store.getComponent(owningEntity, Player.getComponentType());
        if (player == null) return;


        World world = player.getWorld();
        if (world == null) return;

        String heldItem = context.getHeldItem().getItemId();
        int requiredAmount = 1;

        if (heldItem == null) return;

        var brType = playerStats.getComponentType();
        if (brType == null) {
            player.sendMessage(Message.raw("ERROR PLAYER STATS NULL"));
            return;}
        playerStats stats = store.getComponent(owningEntity, brType);

        if(heldItem.equals("Benihime_Spirit_Fragment")){
            player.sendMessage(Message.raw("You feel a sudden surge of power within you. From deep within your soul, a sharp and elegant voice echoes…"));
            player.sendMessage(Message.raw("Benihime").bold(true).color(Color.RED).italic(true));
            player.sendMessage(Message.raw("Trying pressing R while holding your Zanpakuto to utilize Shikai.").bold(true).color(Color.WHITE));
            ItemStackSlotTransaction itemStackSlotTransaction = player.getInventory().getHotbar().removeItemStackFromSlot(context.getHeldItemSlot(),
                    requiredAmount, true, false);
            if(!itemStackSlotTransaction.succeeded()) return;
            if (stats != null) {
                stats.setActiveShikaiBenihime();
            }
        }

        if(heldItem.equals("Wabisuke_Spirit_Fragment")){
            player.sendMessage(Message.raw("A heavy presence settles within your soul. A quiet, solemn voice reveals its name…"));
            player.sendMessage(Message.raw("Wabisuke").bold(true).color(Color.MAGENTA).italic(true));
            player.sendMessage(Message.raw("Trying pressing R while holding your Zanpakuto to utilize Shikai.").bold(true).color(Color.WHITE));
            ItemStackSlotTransaction itemStackSlotTransaction = player.getInventory().getHotbar().removeItemStackFromSlot(context.getHeldItemSlot(),
                    requiredAmount, true, false);
            if(!itemStackSlotTransaction.succeeded()) return;
            stats.setActiveShikaiWabisuke();
        }

        if(heldItem.equals("SodeNoShirayuki_Spirit_Fragment")){
            player.sendMessage(Message.raw("A chilling yet graceful energy flows through your soul. A calm voice whispers its name…"));
            player.sendMessage(Message.raw("Sode no Shirayuki").bold(true).color(Color.WHITE).italic(true));
            player.sendMessage(Message.raw("Trying pressing R while holding your Zanpakuto to utilize Shikai.").bold(true).color(Color.WHITE));
            ItemStackSlotTransaction itemStackSlotTransaction = player.getInventory().getHotbar().removeItemStackFromSlot(context.getHeldItemSlot(),
                    requiredAmount, true, false);
            if(!itemStackSlotTransaction.succeeded()) return;
            if (stats != null) {
                stats.setActiveShikaiSodeNoShirayuki();
            }
        }

        if(heldItem.equals("Hozukimaru_Spirit_Fragment")){
            player.sendMessage(Message.raw("A fierce fighting spirit surges through your soul. A bold voice calls out its name…"));
            player.sendMessage(Message.raw("Hozukimaru!!!").bold(true).color(Color.RED).italic(true));
            player.sendMessage(Message.raw("Trying pressing R while holding your Zanpakuto to utilize Shikai.").bold(true).color(Color.WHITE));
            ItemStackSlotTransaction itemStackSlotTransaction = player.getInventory().getHotbar().removeItemStackFromSlot(context.getHeldItemSlot(),
                    requiredAmount, true, false);
            if(!itemStackSlotTransaction.succeeded()) return;
            if (stats != null) {
                stats.setActiveShikaiHozukimaru();
            }
        }


        if(stats==null) return;


    }
}
