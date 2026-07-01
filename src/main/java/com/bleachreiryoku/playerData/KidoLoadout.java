package com.bleachreiryoku.playerData;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

/**
 * KidoLoadout - per-player persisted assignment of a kido technique to each of the
 * five Left-Click movement inputs handled by the Kido Charm's MovementCondition.
 *
 * Slot meaning (mirrors MovementCondition routing):
 *   None    -> Left-Click while stationary
 *   Forward -> Left-Click + forward
 *   Back    -> Left-Click + backward
 *   Left    -> Left-Click + strafe left
 *   Right   -> Left-Click + strafe right
 *
 * Kept as its own component so the existing
 * playerStats constructor/codec stay untouched.
 */
public class KidoLoadout implements Component<EntityStore> {

    public static ComponentType<EntityStore, KidoLoadout> TYPE;

    public static void setComponentType(ComponentType<EntityStore, KidoLoadout> type) {
        TYPE = type;
    }

    public static ComponentType<EntityStore, KidoLoadout> getComponentType() {
        return TYPE;
    }

    // Stored kido "*_Cost" ids per slot. Empty string = unassigned.
    public String slotNone    = "";
    public String slotForward = "";
    public String slotBack    = "";
    public String slotLeft    = "";
    public String slotRight   = "";

    public static final BuilderCodec<KidoLoadout> CODEC = BuilderCodec
            .builder(KidoLoadout.class, KidoLoadout::new)
            .append(
                    new KeyedCodec<>("SlotNone", Codec.STRING),
                    (c, v) -> c.slotNone = v,
                    c -> c.slotNone
            ).add()
            .append(
                    new KeyedCodec<>("SlotForward", Codec.STRING),
                    (c, v) -> c.slotForward = v,
                    c -> c.slotForward
            ).add()
            .append(
                    new KeyedCodec<>("SlotBack", Codec.STRING),
                    (c, v) -> c.slotBack = v,
                    c -> c.slotBack
            ).add()
            .append(
                    new KeyedCodec<>("SlotLeft", Codec.STRING),
                    (c, v) -> c.slotLeft = v,
                    c -> c.slotLeft
            ).add()
            .append(
                    new KeyedCodec<>("SlotRight", Codec.STRING),
                    (c, v) -> c.slotRight = v,
                    c -> c.slotRight
            ).add()
            .build();

    public KidoLoadout() {}

    public KidoLoadout(String slotNone, String slotForward, String slotBack,
                       String slotLeft, String slotRight) {
        this.slotNone = slotNone;
        this.slotForward = slotForward;
        this.slotBack = slotBack;
        this.slotLeft = slotLeft;
        this.slotRight = slotRight;
    }

    // Returns the assigned kido id for a slot, or "" if unassigned.
    public String get(Slot slot) {
        String v = switch (slot) {
            case None    -> slotNone;
            case Forward -> slotForward;
            case Back    -> slotBack;
            case Left    -> slotLeft;
            case Right   -> slotRight;
        };
        return v == null ? "" : v;
    }

    public void set(Slot slot, String kidoId) {
        String v = kidoId == null ? "" : kidoId;
        switch (slot) {
            case None    -> slotNone = v;
            case Forward -> slotForward = v;
            case Back    -> slotBack = v;
            case Left    -> slotLeft = v;
            case Right   -> slotRight = v;
        }
    }

    public boolean isAssigned(Slot slot) {
        return !get(slot).isEmpty();
    }

    //The five Left-Click movement inputs.
    public enum Slot {
        None,
        Forward,
        Back,
        Left,
        Right
    }

    @NullableDecl
    @Override
    public KidoLoadout clone() {
        return new KidoLoadout(slotNone, slotForward, slotBack, slotLeft, slotRight);
    }
}
