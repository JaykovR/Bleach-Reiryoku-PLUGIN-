package com.bleachreiryoku.interactions;

import com.bleachreiryoku.playerData.KidoCatalog;
import com.bleachreiryoku.playerData.playerStats;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

/**
 * Grants Kido proficiency to the CASTER. Intended to run on-hit, placed in a kido's
 * damage/hit chain, so proficiency is only earned on successful hits (not misses).
 *
 * In a projectile-hit chain, context.getOwningEntity() is the caster (the damage
 * source), so this targets OWNER. Ex:
 *
 *   {
 *     "Type": "AddKidoProficiency",
 *     "Class": "Hado",
 *     "Amount": 2
 *   }
 *
 * Amount / Class per kido come from KidoCatalog (profGain, category), but are written
 * explicitly in each JSON so I can tweak it whenever I need.
 */
public class AddKidoProficiencyInteraction extends SimpleInstantInteraction {

    public enum KidoClass { Hado, Bakudo }

    public KidoClass kidoClass = KidoClass.Hado;
    public int amount = 1;

    public static final BuilderCodec<AddKidoProficiencyInteraction> CODEC =
            BuilderCodec.builder(AddKidoProficiencyInteraction.class, AddKidoProficiencyInteraction::new, SimpleInteraction.CODEC)
                    .append(
                            new KeyedCodec<>("Class", new EnumCodec<>(KidoClass.class)),
                            (o, v) -> o.kidoClass = v,
                            o -> o.kidoClass
                    ).add()
                    .append(
                            new KeyedCodec<>("Amount", Codec.INTEGER),
                            (o, v) -> o.amount = v,
                            o -> o.amount
                    ).add()
                    .build();

    @Override
    protected void firstRun(@NonNullDecl InteractionType type,
                            @NonNullDecl InteractionContext context,
                            @NonNullDecl CooldownHandler cooldownHandler) {

        // The caster is the owning entity of the (projectile) interaction chain.
        Ref<EntityStore> caster = context.getOwningEntity();
        if (caster == null || !caster.isValid()) {
            return;
        }

        playerStats stats = caster.getStore().getComponent(caster, playerStats.getComponentType());
        if (stats == null) {
            return; // not a player / no stats: nothing to credit
        }

        if (kidoClass == KidoClass.Bakudo) {
            stats.addBakudoProficiency(amount);
        } else {
            stats.addHadoProficiency(amount);
        }
    }
}
