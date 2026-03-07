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

        ItemStack heldItem = context.getHeldItem();
        if (heldItem == null) return;
        if (!heldItem.getItem().getId().equalsIgnoreCase("Resonating_Reishi_Core")) return;
        int requiredAmount = 1;

        ItemStackSlotTransaction itemStackSlotTransaction = player.getInventory().getHotbar().removeItemStackFromSlot(context.getHeldItemSlot(),
                requiredAmount, true, false);
        if(!itemStackSlotTransaction.succeeded()) return;


        var brType = playerStats.getComponentType();
        if (brType == null) return;
        playerStats stats = store.getComponent(owningEntity, brType);
        if(stats==null) return;

        stats.setActiveShikai();
        player.sendMessage(Message.raw("You have feel a sudden surge of power within you. From within your soul you hear the name of your zanpakuto resonate with you."));
        player.sendMessage(Message.raw("Trying pressing R while holding your Zanpakuto to utilize Shikai.").bold(true).color(Color.WHITE));


    }
}
