package com.bleachreiryoku.interactions;

import com.bleachreiryoku.playerData.KidoCatalog;
import com.bleachreiryoku.playerData.KidoLoadout;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

/**
 * Runtime router for one Left-Click movement slot.
 *
 *   This is for storing and assigning the Kido Loadout for the player.
 *   This is linked then to a movement condition, and each direction has a separate json to work with,
 *   but using this same interaction.
 *
 *   {
 *     "Type": "MovementCondition",
 *     "Failed":  "RunKidoSlot_None",
 *     "Forward": "RunKidoSlot_Forward",
 *     "Back":    "RunKidoSlot_Back",
 *     "Left":    "RunKidoSlot_Left",
 *     "Right":   "RunKidoSlot_Right"
 *   }
 *
 */
public class RunKidoSlotInteraction extends SimpleInstantInteraction {

    public KidoLoadout.Slot slot = KidoLoadout.Slot.None;

    public static final BuilderCodec<RunKidoSlotInteraction> CODEC =
            BuilderCodec.builder(RunKidoSlotInteraction.class, RunKidoSlotInteraction::new, SimpleInteraction.CODEC)
                    .append(
                            new KeyedCodec<>("Slot", new EnumCodec<>(KidoLoadout.Slot.class)),
                            (o, v) -> o.slot = v,
                            o -> o.slot
                    ).add()
                    .build();

    @Override
    public boolean needsRemoteSync() {
        // Returning false keeps it out of the synced branch tree that crashed the client.
        return false;
    }

    @Override
    protected void firstRun(@NonNullDecl InteractionType type,
                            @NonNullDecl InteractionContext context,
                            @NonNullDecl CooldownHandler cooldownHandler) {

        context.getState().state = InteractionState.Finished;

        Ref<EntityStore> ref = context.getOwningEntity();
        if (ref == null || !ref.isValid()) {
            return;
        }

        KidoLoadout loadout = ref.getStore().getComponent(ref, KidoLoadout.getComponentType());
        if (loadout == null) {
            return;
        }

        String assignedId = loadout.get(slot);
        if (assignedId.isEmpty()) {
            return; // nothing assigned to this slot
        }

        KidoCatalog.Entry entry = KidoCatalog.byId(assignedId);
        if (entry == null) {
            return; // assigned id not in catalog (e.x. removed kido)
        }

        RootInteraction root = RootInteraction.getRootInteractionOrUnknown(entry.rootId());
        if (root == null) {
            return;
        }

        // Fork the chosen kido as its own chain
        context.fork(context.duplicate(), root, false);
    }
}
