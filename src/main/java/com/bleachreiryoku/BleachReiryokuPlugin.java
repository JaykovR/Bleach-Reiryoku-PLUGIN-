package com.bleachreiryoku;

import com.bleachreiryoku.commands.ReiryokuCommands;
import com.bleachreiryoku.interactions.ShikaiCheckInteraction;
import com.bleachreiryoku.interactions.UnlockShikaiInteraction;
import com.bleachreiryoku.systems.playerJoinSystem;
import com.hypixel.hytale.server.core.command.system.CommandRegistration;
import com.hypixel.hytale.server.core.command.system.CommandRegistry;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.logger.HytaleLogger;
import com.bleachreiryoku.playerData.playerStats;

import javax.annotation.Nonnull;
import java.util.logging.Level;

/**
 * Bleach Reiryoku - A Hytale server plugin.
 *
 * @author jaykov
 * @version 1.0.0
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
        // TODO: Register commands and listeners here
        CommandRegistry commandRegistry = this.getCommandRegistry();
        CommandRegistration commandRegistration = commandRegistry.registerCommand(new ReiryokuCommands());

        var registry = getEntityStoreRegistry();
        var reiryokuType = registry.registerComponent(
                playerStats.class,
                "BR_PlayerData",
                playerStats.CODEC
        );
        playerStats.setComponentType(reiryokuType);

        this.getCodecRegistry(Interaction.CODEC)
                .register("UnlockShikai", UnlockShikaiInteraction.class, UnlockShikaiInteraction.CODEC);

        this.getCodecRegistry(Interaction.CODEC)
                .register("ShikaiCheck", ShikaiCheckInteraction.class, ShikaiCheckInteraction.CODEC);




        LOGGER.at(Level.INFO).log("[Bleach Reiryoku] Setup complete!");
    }

    @Override
    protected void start() {
        LOGGER.at(Level.INFO).log("[Bleach Reiryoku] Started!");
        var registry = getEntityStoreRegistry();
        registry.registerSystem(new playerJoinSystem());
    }

    @Override
    protected void shutdown() {
        LOGGER.at(Level.INFO).log("[Bleach Reiryoku] Shutting down...");
        instance = null;
    }
}