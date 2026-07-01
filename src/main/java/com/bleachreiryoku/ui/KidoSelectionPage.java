package com.bleachreiryoku.ui;

import com.bleachreiryoku.playerData.KidoCatalog;
import com.bleachreiryoku.playerData.KidoLoadout;
import com.bleachreiryoku.playerData.KidoUnlocks;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

/**
 * Kido Selection UI.
 * Left column has the five inputs and the right one is a scrollable.
 *
 * Flow:
 *   1. Player clicks a slot button  -> that slot becomes "active".
 *   2. Player clicks a kido in the list -> it's written to the active slot in
 *      KidoLoadout, and the page re-renders in place via sendUpdate.
 *
 * The .ui file (KidoSelection/KidoSelectionPage.ui) provides the static shell;
 * this class fills slot labels, builds list rows, and wires click events.
 */
public class KidoSelectionPage extends InteractiveCustomUIPage<KidoSelectionPage.KidoEventData> {

    // Which slot is currently selected for assignment. Defaults to the Left-Click slot.
    private KidoLoadout.Slot activeSlot = KidoLoadout.Slot.None;

    public KidoSelectionPage(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, KidoEventData.CODEC);
    }

    // ---- event payload from the client ----
    // Exactly one of these is set per click:
    //   slot      -> a slot button was clicked (value = enum name)
    //   assignId  -> a kido row was clicked (value = "*_Cost" id to assign)
    //   done      -> the Done button was clicked
    public static class KidoEventData {
        public String slot;
        public String assignId;
        public String done;

        public static final BuilderCodec<KidoEventData> CODEC =
                BuilderCodec.builder(KidoEventData.class, KidoEventData::new)
                        .append(new KeyedCodec<>("Slot", Codec.STRING),
                                (o, v) -> o.slot = v, o -> o.slot).add()
                        .append(new KeyedCodec<>("AssignId", Codec.STRING),
                                (o, v) -> o.assignId = v, o -> o.assignId).add()
                        .append(new KeyedCodec<>("Done", Codec.STRING),
                                (o, v) -> o.done = v, o -> o.done).add()
                        .build();
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref,
                      @Nonnull UICommandBuilder cmd,
                      @Nonnull UIEventBuilder evt,
                      @Nonnull Store<EntityStore> store) {
        cmd.append("KidoSelection/KidoSelectionPage.ui");
        render(ref, cmd, evt, store);
    }

    /**
     * Fills the shell with current state. Used by both build() and the in-place
     * refresh in handleDataEvent(). Safe to call repeatedly.
     */
    private void render(@Nonnull Ref<EntityStore> ref,
                        @Nonnull UICommandBuilder cmd,
                        @Nonnull UIEventBuilder evt,
                        @Nonnull Store<EntityStore> store) {

        KidoLoadout loadout = getLoadout(ref, store);

        // --- slot button labels (current assignment, or "Empty") ---
        cmd.set("#SlotNoneButton.Text",    slotLabel(loadout, KidoLoadout.Slot.None));
        cmd.set("#SlotForwardButton.Text", slotLabel(loadout, KidoLoadout.Slot.Forward));
        cmd.set("#SlotBackButton.Text",    slotLabel(loadout, KidoLoadout.Slot.Back));
        cmd.set("#SlotLeftButton.Text",    slotLabel(loadout, KidoLoadout.Slot.Left));
        cmd.set("#SlotRightButton.Text",   slotLabel(loadout, KidoLoadout.Slot.Right));

        // --- active-slot hint text ---
        cmd.set("#ActiveSlotHint.Text", "ASSIGNING: " + slotInputName(activeSlot));

        // --- slot click bindings ---
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#SlotNoneButton",
                new EventData().append("Slot", KidoLoadout.Slot.None.name()));
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#SlotForwardButton",
                new EventData().append("Slot", KidoLoadout.Slot.Forward.name()));
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#SlotBackButton",
                new EventData().append("Slot", KidoLoadout.Slot.Back.name()));
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#SlotLeftButton",
                new EventData().append("Slot", KidoLoadout.Slot.Left.name()));
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#SlotRightButton",
                new EventData().append("Slot", KidoLoadout.Slot.Right.name()));

        // --- build the kido list (only UNLOCKED ones) ---
        cmd.clear("#KidoList");

        KidoUnlocks unlocks = store.getComponent(ref, KidoUnlocks.getComponentType());

        String activeAssigned = loadout.get(activeSlot);
        int rowIdx = 0;
        for (KidoCatalog.Entry e : KidoCatalog.ENTRIES) {
            // Skip kido the player hasn't unlocked. If the unlocks component is somehow
            // missing, fall back to showing everything rather than an empty list.
            if (unlocks != null && !unlocks.isUnlocked(e.id())) {
                continue;
            }

            String selector = "#KidoList[" + rowIdx + "]";
            rowIdx++;

            cmd.append("#KidoList", "KidoSelection/KidoRow.ui");

            String marker = e.id().equals(activeAssigned) ? "> " : "";
            cmd.set(selector + ".Text", marker + e.display());

            evt.addEventBinding(CustomUIEventBindingType.Activating, selector,
                    new EventData().append("AssignId", e.id()));
        }

        // --- Done button ---
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#DoneButton",
                new EventData().append("Done", "1"));
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref,
                                @Nonnull Store<EntityStore> store,
                                @Nonnull KidoEventData data) {

        if (data.done != null && !data.done.isEmpty()) {
            Player player = store.getComponent(ref, Player.getComponentType());
            if (player != null) {
                player.getPageManager().setPage(ref, store, Page.None);
            }
            return;
        }

        boolean changed = false;

        // Slot button clicked -> change which slot is active.
        if (data.slot != null && !data.slot.isEmpty()) {
            try {
                activeSlot = KidoLoadout.Slot.valueOf(data.slot);
                changed = true;
            } catch (IllegalArgumentException ignored) {
                // unknown slot name; ignore
            }
        }

        // Kido row clicked -> assign to the active slot (only if unlocked).
        if (data.assignId != null && !data.assignId.isEmpty()) {
            KidoCatalog.Entry e = KidoCatalog.byId(data.assignId);
            if (e != null) {
                KidoUnlocks unlocks = store.getComponent(ref, KidoUnlocks.getComponentType());
                boolean allowed = (unlocks == null) || unlocks.isUnlocked(e.id());
                if (allowed) {
                    KidoLoadout loadout = getLoadout(ref, store);
                    loadout.set(activeSlot, e.id());
                    changed = true;
                }
            }
        }

        if (changed) {
            UICommandBuilder cmd = new UICommandBuilder();
            UIEventBuilder evt = new UIEventBuilder();
            render(ref, cmd, evt, store);
            sendUpdate(cmd, evt, false);
        }
    }

    // ---- helpers ----
    private KidoLoadout getLoadout(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
        KidoLoadout loadout = store.getComponent(ref, KidoLoadout.getComponentType());
        if (loadout == null) {
            loadout = new KidoLoadout();
        }
        return loadout;
    }

    private String slotLabel(KidoLoadout loadout, KidoLoadout.Slot slot) {
        String marker = (slot == activeSlot) ? "> " : "";
        String input = slotShortName(slot);
        String assigned = KidoCatalog.displayFor(loadout.get(slot));
        return marker + input + ": " + assigned;
    }

    private String slotShortName(KidoLoadout.Slot slot) {
        return switch (slot) {
            case None    -> "Left-Click";
            case Forward -> "+ Forward";
            case Back    -> "+ Back";
            case Left    -> "+ Left";
            case Right   -> "+ Right";
        };
    }

    private String slotInputName(KidoLoadout.Slot slot) {
        return switch (slot) {
            case None    -> "LEFT-CLICK";
            case Forward -> "LEFT-CLICK + FORWARD";
            case Back    -> "LEFT-CLICK + BACK";
            case Left    -> "LEFT-CLICK + LEFT";
            case Right   -> "LEFT-CLICK + RIGHT";
        };
    }
}
