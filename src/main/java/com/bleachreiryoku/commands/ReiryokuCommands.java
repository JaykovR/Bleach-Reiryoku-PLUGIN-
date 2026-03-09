package com.bleachreiryoku.commands;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import com.bleachreiryoku.playerData.playerStats;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

/**
 * CheckShikaiState - Command collection for /.
 * <p>
 * Usage:
 * - / help - Show available commands
 */
public class ReiryokuCommands extends AbstractCommandCollection {

    public ReiryokuCommands() {
        super("br", "Bleach Reiryoku commands");
        this.addSubCommand(new HelpSubCommand());
        this.addSubCommand(new CheckShikai());
        this.addSubCommand(new RevertShikai());
    }

    @Override
    protected boolean canGeneratePermission() {
        return false; //No permission required for base command
    }

    /**
     * / help - Show available commands
     */
    private static class HelpSubCommand extends CommandBase {

        public HelpSubCommand() {
            super("help", "Show available commands");
            this.setPermissionGroup(null);
        }

        @Override
        protected boolean canGeneratePermission() {
            return false;
        }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            context.sendMessage(Message.raw(""));
            context.sendMessage(Message.raw("===  Commands ==="));
            context.sendMessage(Message.raw("/ help - Show this help message"));
            context.sendMessage(Message.raw("/ checkShikai - Checks for Shikai"));
            context.sendMessage(Message.raw("/ RevertShikai - Reverts ability to transform to Shikai"));
            context.sendMessage(Message.raw("========================"));
        }
    }

    private static class CheckShikai extends CommandBase{

        public CheckShikai(){
            super("checkShikai", "Tells user if shikai is on or off");
            this.setPermissionGroup(null);
        }
        @Override
        protected boolean canGeneratePermission() {
            return false;
        }

        protected void executeSync(@Nonnull CommandContext context) {
            CommandSender sender = context.sender();
            Ref<EntityStore> owningEntity = (Ref<EntityStore>) context.sender();
            Store<EntityStore> store = owningEntity.getStore();
            var brType = playerStats.getComponentType();
            playerStats stats = store.getComponent(owningEntity, brType);
            if(stats.getShikaiState() == 0) {
                context.sendMessage(Message.raw("You are not able to transform to shikai."));
            }
            else{context.sendMessage(Message.raw("You can transform to shikai"));}
        }

    }

    private static class RevertShikai extends CommandBase{

        public RevertShikai(){
            super("revertShikai", "Revers the ability for the player to transform into shikai");
            this.setPermissionGroup(null);
        }
        @Override
        protected boolean canGeneratePermission() {
            return false;
        }

        protected void executeSync(@Nonnull CommandContext context) {
            //aaaa

            CommandSender sender = context.sender();
            Ref<EntityStore> owningEntity = (Ref<EntityStore>) context.sender();
            Store<EntityStore> store = owningEntity.getStore();
            var brType = playerStats.getComponentType();
            playerStats stats = store.getComponent(owningEntity, brType);
            if (stats != null && stats.getShikaiState() == 0) {
                context.sendMessage(Message.raw("You were already unable to transform into Shikai."));
                return;
            }

            stats.setNotActiveShikai();
            context.sendMessage(Message.raw("Shikai access disabled."));
        }

    }

    }

