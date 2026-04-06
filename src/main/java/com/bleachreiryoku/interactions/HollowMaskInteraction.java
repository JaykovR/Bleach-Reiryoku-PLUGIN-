package com.bleachreiryoku.interactions;

import com.bleachreiryoku.effects.BleachEffect;
import com.bleachreiryoku.effects.BleachEffectService;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

// applies and remove the Hollow Mask
public class HollowMaskInteraction extends SimpleInteraction {

    public static final BuilderCodec<HollowMaskInteraction> CODEC =
            BuilderCodec.builder(HollowMaskInteraction.class, HollowMaskInteraction::new, SimpleInteraction.CODEC)
                    .append(
                            new KeyedCodec<>("Duration", Codec.FLOAT),
                            (t, v) -> t.duration = v,
                            t -> t.duration
                    ).add()
                    .build();

    // = permanent toggle (default). Any positive value = timed.
    private float duration = 3.0f;

    @Override
    protected void tick0(boolean firstRun, float time,
                         @NonNullDecl InteractionType type,
                         @NonNullDecl InteractionContext context,
                         @NonNullDecl CooldownHandler cooldownHandler) {

        if (!firstRun) return; // Only trigger once per item use

        Ref<EntityStore> owningEntity = context.getOwningEntity();
        Store<EntityStore> store = owningEntity.getStore();

        if (store.getComponent(owningEntity, Player.getComponentType()) == null) return;

        var commandBuffer = context.getCommandBuffer();

        if (duration < 0) {
            BleachEffectService.toggleEffect(owningEntity, store, commandBuffer, BleachEffect.HOLLOW_MASK);
        } else {
            BleachEffectService.applyTimedEffect(owningEntity, store, commandBuffer, BleachEffect.HOLLOW_MASK, duration);
        }
    }
}
