package com.bleachreiryoku;

import com.bleachreiryoku.commands.ReiryokuCommands;
import com.bleachreiryoku.effects.ActiveEffectsComponent;
import com.bleachreiryoku.effects.BleachStatTypes;
import com.bleachreiryoku.effects.EffectCleanupSystem;
import com.bleachreiryoku.effects.EffectTickSystem;
import com.bleachreiryoku.interactions.*;
import com.bleachreiryoku.playerData.playerStats;
import com.bleachreiryoku.systems.ReiryokuHudSystem;
import com.bleachreiryoku.systems.ReiryokuDrainSystem;
import com.bleachreiryoku.systems.KillRewardSystem;
import com.bleachreiryoku.systems.playerJoinSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.command.system.CommandRegistry;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import javax.annotation.Nonnull;
import java.util.logging.Level;

/**
 * Bleach Reiryoku - A Hytale server plugin.
 *
 * @author Jaykov
 */
public class BleachReiryokuPlugin extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static BleachReiryokuPlugin instance;

    public BleachReiryokuPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    public static BleachReiryokuPlugin getInstance() {
        return instance;
    }

    @Override
    protected void setup() {
        LOGGER.at(Level.INFO).log("[Bleach Reiryoku] Setting up...");

        // ------------------COMMANDS-----------------
        CommandRegistry commandRegistry = this.getCommandRegistry();
        commandRegistry.registerCommand(new ReiryokuCommands());

        // -------------COMPONENTS--------------------
        var registry = getEntityStoreRegistry();

        // Player stats (shikai unlocks etc.) — persisted to disk
        var reiryokuType = registry.registerComponent(
                playerStats.class,
                "BR_PlayerData",
                playerStats.CODEC
        );
        playerStats.setComponentType(reiryokuType);

        // Active visual effects. Runtime only, NOT persisted
        var effectsType = registry.registerComponent(
                ActiveEffectsComponent.class,
                "BR_ActiveEffects",
                ActiveEffectsComponent.CODEC
        );
        ActiveEffectsComponent.setComponentType(effectsType);

        // -----------------INTERACTIONS---------------------------------
        this.getCodecRegistry(Interaction.CODEC)
                .register("UnlockShikai", UnlockShikaiInteraction.class, UnlockShikaiInteraction.CODEC)
                .register("ShikaiCheck", ShikaiCheckInteraction.class, ShikaiCheckInteraction.CODEC)
                .register("HollowMask", HollowMaskInteraction.class, HollowMaskInteraction.CODEC)
                .register("TensaZangetsu", TensaZangetsuInteraction.class, TensaZangetsuInteraction.CODEC)
                .register("SwapItemInteraction", SwapItemInteraction.class, SwapItemInteraction.CODEC)
                .register("ReiryokuCheck", ReiryokuCheckInteraction.class, ReiryokuCheckInteraction.CODEC)
                .register("OpenRaceSelectionPage", OpenRaceSelectionPage.class, OpenRaceSelectionPage.CODEC);

        LOGGER.at(Level.INFO).log("[Bleach Reiryoku] Setup complete!");
    }

    @Override
    protected void start() {
        LOGGER.at(Level.INFO).log("[Bleach Reiryoku] Starting systems...");

        var registry = getEntityStoreRegistry();

        registry.registerSystem(new playerJoinSystem());

        // Counts down timed effect timers and removes expired effects
        registry.registerSystem(new EffectTickSystem());

        // Cleans up effect attachments when a player disconnects or is unloaded
        registry.registerSystem(new EffectCleanupSystem());

        registry.registerSystem(new ReiryokuHudSystem());

        registry.registerSystem(new ReiryokuDrainSystem());

        // Watches for NPC deaths and rewards Reiryoku max increases to the killer
        registry.registerSystem(new KillRewardSystem());

        BleachStatTypes.update();

        LOGGER.at(Level.INFO).log("[Bleach Reiryoku] Started!");
    }

    @Override
    protected void shutdown() {
        LOGGER.at(Level.INFO).log("[Bleach Reiryoku] Shutting down...");
        instance = null;
    }
}
