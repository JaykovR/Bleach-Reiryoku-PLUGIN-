package com.bleachreiryoku.interactions;
import com.bleachreiryoku.effects.ActiveEffectsComponent;
import com.bleachreiryoku.effects.BleachEffect;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class SwapItemInteraction extends com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction
{

    /*
       Swaps items for a time or permanently depending on timer, works on the same slot.
     */

    public static final BuilderCodec<SwapItemInteraction> CODEC =
            BuilderCodec.builder(SwapItemInteraction.class, SwapItemInteraction::new, SimpleInteraction.CODEC)
                    .append(
                            new KeyedCodec<>("Duration", Codec.FLOAT),
                            (t, v) -> t.duration = v,
                            t -> t.duration
                    ).add()
                    .append(
                            new KeyedCodec<>("ItemToAdd", ItemStack.CODEC),
                            (interaction, itemStack) -> interaction.itemToAdd = itemStack,
                            interaction -> interaction.itemToAdd
                    ).add()
                    .append(
                            new KeyedCodec<>("ItemToRemove", ItemStack.CODEC),
                            (interaction, itemStack) -> interaction.itemToRemove = itemStack,
                            interaction -> interaction.itemToRemove
                    ).add()
                    .build();

    @Override
    public com.hypixel.hytale.protocol.WaitForDataFrom getWaitForDataFrom() {
        return com.hypixel.hytale.protocol.WaitForDataFrom.Server;
    }

    @Override
    protected void simulateFirstRun(@NonNullDecl InteractionType type,
                                    @NonNullDecl InteractionContext context,
                                    @NonNullDecl CooldownHandler cooldownHandler) {
        // Inventory changes and progression checks run on the authoritative server only.
    }
    private float duration = -1.0f;
    private ItemStack itemToAdd;
    private ItemStack itemToRemove;

    @Override
    protected void firstRun(
                         @NonNullDecl InteractionType type,
                         @NonNullDecl InteractionContext context,
                         @NonNullDecl CooldownHandler cooldownHandler) {

        context.getState().state = com.hypixel.hytale.protocol.InteractionState.Failed;

        Ref<EntityStore> ref = context.getOwningEntity();
        if (ref == null || !ref.isValid() || itemToAdd == null || itemToRemove == null) return;
        Store<EntityStore> store = ref.getStore();

        if (store.getComponent(ref, Player.getComponentType()) == null) return;

        var commandBuffer = context.getCommandBuffer();

        // Validate held item is the item to remove
        ItemStack heldItem = context.getHeldItem();
        if (heldItem == null || !heldItem.getItemId().equals(itemToRemove.getItemId())) return;

        byte heldSlot = context.getHeldItemSlot(); // Gets the Slot
        var hotbar = context.getHeldItemContainer();
        if (hotbar == null) return;

        var swapTransaction = hotbar.setItemStackForSlot(heldSlot, itemToAdd); // Transaction happens
        if (!swapTransaction.succeeded()) return;

        context.setHeldItem(itemToAdd);
        context.getState().state = com.hypixel.hytale.protocol.InteractionState.Finished;

        // Ensure the ActiveEffectsComponent exists
        ActiveEffectsComponent effects = store.getComponent(ref, ActiveEffectsComponent.getComponentType());
        if (effects == null && duration > 0) {
            effects = new ActiveEffectsComponent();
            commandBuffer.addComponent(ref, ActiveEffectsComponent.getComponentType(), effects);
        }

        // If duration is > 0 then the timer activates and swapback is imminent, if not then no.
        if(duration > 0)
        {
            effects.setTimer(BleachEffect.SWAP_INTERACTION, duration);
            effects.registerSwapBack(BleachEffect.SWAP_INTERACTION, heldSlot, heldItem, itemToAdd);
        }
    }
}


