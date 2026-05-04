package com.bleachreiryoku.commands;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.bleachreiryoku.effects.BleachStatTypes;
import com.bleachreiryoku.playerData.playerStats;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.SingleArgumentType;
import com.hypixel.hytale.server.core.command.system.ParseResult;

import javax.annotation.Nonnull;




public class ReiryokuCommands extends AbstractCommandCollection {

    public ReiryokuCommands() {
        super("br", "Bleach Reiryoku commands");
        this.addSubCommand(new HelpSubCommand());
        this.addSubCommand(new CheckShikai());
        this.addSubCommand(new RevertShikai());
        // Give Shikai Commands
        this.addSubCommand(new GiveShikaiSodeNoShirayuki());
        this.addSubCommand(new GiveShikaiWabisuke());
        this.addSubCommand(new GiveShikaiBenihime());
        this.addSubCommand(new GiveShikaiHozukimaru());

        // temporary name.
        this.addSubCommand(new CheckHollow());

        this.addSubCommand(new OpenGUICommand("customGui", "Opens custom gui"));

        this.addSubCommand(new setReiryokuMax());



    }

    @Override
    protected boolean canGeneratePermission() {
        return false; //No permission required for base command
    }

    // /br help show all commands
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
            context.sendMessage(Message.raw("/ giveShikaiSodeNoShirayuki - Gives the player Sode No Shirayuki Shikai access"));
            context.sendMessage(Message.raw("/ giveShikaiWabisuke - Gives the player Wabisuke Shikai access"));
            context.sendMessage(Message.raw("/ giveShikaiBenihime - Gives the player Benihime Shikai access"));
            context.sendMessage(Message.raw("/ giveShikaiHozukimaru - Gives the player Hozukimaru Shikai access"));
            context.sendMessage(Message.raw("/ checkHollow - Checks if the player has unlocked hollow mask"));
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
            Ref<EntityStore> owningEntity = context.senderAsPlayerRef();
            Store<EntityStore> store = owningEntity.getStore();
            World world = store.getExternalData().getWorld();
            world.execute(() -> {
                var brType = playerStats.getComponentType();
                playerStats stats = store.getComponent(owningEntity, brType);
                context.sendMessage(Message.raw("You have: "));

                if (stats != null && stats.getShikaiWabisukeState()) {
                    context.sendMessage(Message.raw("Wabisuke Shikai"));
                }
                if (stats != null && stats.getShikaiSodeNoShirayukiState()) {
                    context.sendMessage(Message.raw("Sode no Shirayuki Shikai"));
                }
                if (stats != null && stats.getShikaiHozukimaruState()) {
                    context.sendMessage(Message.raw("Hozukimaru Shikai"));
                }
                if (stats != null && stats.getShikaiBenihimeState()) {
                    context.sendMessage(Message.raw("Benihime Shikai"));
                }
                if (stats != null && stats.getShikaiSenbonzakuraState()) {
                    context.sendMessage(Message.raw("Senbonzakura Shikai"));
                }
                if (stats != null && stats.getShikaiZangetsuState()) {
                    context.sendMessage(Message.raw("Zangetsu Shikai"));
                }
                if (stats != null && stats.getBankaiZangetsuState()) {
                    context.sendMessage(Message.raw("Zangetsu Bankai"));
                }

                if (((stats != null) && !stats.getShikaiHozukimaruState()) && !stats.getShikaiSodeNoShirayukiState()
                        && !stats.getShikaiBenihimeState() && !stats.getShikaiWabisukeState() && !stats.getShikaiSenbonzakuraState())
                {
                    context.sendMessage(Message.raw("No Shikai Available.."));
                    return;
                }
            });
        }
    }

    private static class CheckHollow extends CommandBase{

        public CheckHollow(){
            super("checkHollow", "Tells user if they have adquired hollow (mask transformation");
            this.setPermissionGroup(null);
        }
        @Override
        protected boolean canGeneratePermission() {
            return false;
        }

        protected void executeSync(@Nonnull CommandContext context) {
            Ref<EntityStore> owningEntity = context.senderAsPlayerRef();
            Store<EntityStore> store = owningEntity.getStore();
            World world = store.getExternalData().getWorld();
            world.execute(() -> {
                var brType = playerStats.getComponentType();
                playerStats stats = store.getComponent(owningEntity, brType);
                context.sendMessage(Message.raw("You"));

                if (stats != null && stats.getHollowMaskState()) {
                    context.sendMessage(Message.raw("Have already unlocked hollow mask"));
                } else if (stats!= null && !stats.getHollowMaskState())
                {
                    context.sendMessage(Message.raw("Have not unlocked hollow mask"));
                }
            });
        }
    }

    private static class RevertShikai extends CommandBase{

        // temporarly using hollow mask on this
        public RevertShikai(){
            super("revertShikai", "Reverts the ability for the player to transform into ANYTHING (DEBUG)");
            this.setPermissionGroup(null);
        }
        @Override
        protected boolean canGeneratePermission() {
            return false;
        }

        protected void executeSync(@Nonnull CommandContext context) {
            if (!context.sender().hasPermission("*")) {
                context.sendMessage(Message.raw("You don't have permission to use this command."));
                return;
            }

            Ref<EntityStore> owningEntity = context.senderAsPlayerRef();
            Store<EntityStore> store = owningEntity.getStore();
            World world = store.getExternalData().getWorld();

            world.execute(() -> {
                var brType = playerStats.getComponentType();
                playerStats stats = store.getComponent(owningEntity, brType);

                if (((stats != null) && !stats.getShikaiHozukimaruState()) && !stats.getShikaiSodeNoShirayukiState()
                        && !stats.getShikaiBenihimeState() && !stats.getShikaiWabisukeState() && !stats.getShikaiSenbonzakuraState())
                {
                    context.sendMessage(Message.raw("You were already unable to transform into any Shikai."));
                    return;
                }
                stats.deactivateShikai();
                context.sendMessage(Message.raw("All Shikai access disabled."));
            });
        }
    }

    // ------------- GIVE SHIKAI COMMANDS -----------------------

    private static class GiveShikaiSodeNoShirayuki extends CommandBase{

        public GiveShikaiSodeNoShirayuki(){
            super("giveShikaiSodeNoShirayuki", "Gives access to Sode No Shirayuki shikai.");
            this.setPermissionGroup(null);
        }
        @Override
        protected boolean canGeneratePermission() {
            return false;
        }

        protected void executeSync(@Nonnull CommandContext context) {
            if (!context.sender().hasPermission("*")) {
                context.sendMessage(Message.raw("You don't have permission to use this command."));
                return;
            }

            Ref<EntityStore> owningEntity = context.senderAsPlayerRef();
            Store<EntityStore> store = owningEntity.getStore();
            World world = store.getExternalData().getWorld();

            world.execute(() -> {
                var brType = playerStats.getComponentType();
                playerStats stats = store.getComponent(owningEntity, brType);

                if (stats != null && !stats.getShikaiSodeNoShirayukiState()) {
                    stats.setActiveShikaiSodeNoShirayuki();
                    context.sendMessage(Message.raw("Sode no Shirayuki Shikai has been unlocked."));
                    return;
                }
                context.sendMessage(Message.raw("You already have Sode no Shirayuki Shikai."));
            });
        }
    }

    private static class GiveShikaiWabisuke extends CommandBase{

        public GiveShikaiWabisuke(){
            super("giveShikaiWabisuke", "Gives access to Wabisuke shikai.");
            this.setPermissionGroup(null);
        }
        @Override
        protected boolean canGeneratePermission() {
            return false;
        }

        protected void executeSync(@Nonnull CommandContext context) {
            if (!context.sender().hasPermission("*")) {
                context.sendMessage(Message.raw("You don't have permission to use this command."));
                return;
            }

            Ref<EntityStore> owningEntity = context.senderAsPlayerRef();
            Store<EntityStore> store = owningEntity.getStore();
            World world = store.getExternalData().getWorld();

            world.execute(() -> {
                var brType = playerStats.getComponentType();
                playerStats stats = store.getComponent(owningEntity, brType);

                if (stats != null && !stats.getShikaiWabisukeState()) {
                    stats.setActiveShikaiWabisuke();
                    context.sendMessage(Message.raw("Wabisuke Shikai has been unlocked."));
                    return;
                }
                context.sendMessage(Message.raw("You already have Wabisuke Shikai."));
            });
        }
    }

    private static class GiveShikaiBenihime extends CommandBase{

        public GiveShikaiBenihime(){
            super("giveShikaiBenihime", "Gives access to Benihime shikai.");
            this.setPermissionGroup(null);
        }
        @Override
        protected boolean canGeneratePermission() {
            return false;
        }

        protected void executeSync(@Nonnull CommandContext context) {
            if (!context.sender().hasPermission("*")) {
                context.sendMessage(Message.raw("You don't have permission to use this command."));
                return;
            }

            Ref<EntityStore> owningEntity = context.senderAsPlayerRef();
            Store<EntityStore> store = owningEntity.getStore();
            World world = store.getExternalData().getWorld();

            world.execute(() -> {
                var brType = playerStats.getComponentType();
                playerStats stats = store.getComponent(owningEntity, brType);

                if (stats != null && !stats.getShikaiBenihimeState()) {
                    stats.setActiveShikaiBenihime();
                    context.sendMessage(Message.raw("Benihime Shikai has been unlocked."));
                    return;
                }
                context.sendMessage(Message.raw("You already have Benihime Shikai."));
            });
        }
    }

    private static class GiveShikaiHozukimaru extends CommandBase{

        public GiveShikaiHozukimaru(){
            super("giveShikaiHozukimaru", "Gives access to Hozukimaru shikai.");
            this.setPermissionGroup(null);
        }
        @Override
        protected boolean canGeneratePermission() {
            return false;
        }

        protected void executeSync(@Nonnull CommandContext context) {
            if (!context.sender().hasPermission("*")) {
                context.sendMessage(Message.raw("You don't have permission to use this command."));
                return;
            }
            Ref<EntityStore> owningEntity = context.senderAsPlayerRef();
            Store<EntityStore> store = owningEntity.getStore();
            World world = store.getExternalData().getWorld();

            world.execute(() -> {
                var brType = playerStats.getComponentType();
                playerStats stats = store.getComponent(owningEntity, brType);

                if (stats != null && !stats.getShikaiHozukimaruState()) {
                    stats.setActiveShikaiHozukimaru();
                    context.sendMessage(Message.raw("Hozukimaru Shikai has been unlocked."));
                    return;
                }
                context.sendMessage(Message.raw("You already have Hozukimaru Shikai."));
            });
        }
    }

    private static class setReiryokuMax extends CommandBase {
        private final RequiredArg<Float> amountArg;

        public setReiryokuMax() {
            super("setReiryokuMax", "Modify your max Reiryoku");
            this.setPermissionGroup(null);
            this.amountArg = this.withRequiredArg("amount", "New max Reiryoku value",
                    new SingleArgumentType<Float>("float", "A number") {
                        @Override
                        public Float parse(String input, ParseResult parseResult) {
                            try {
                                return Float.parseFloat(input);
                            } catch (NumberFormatException e) {
                                parseResult.fail(Message.raw("Invalid number: " + input));
                                return null;
                            }
                        }
                    }
            );
        }

        @Override
        protected boolean canGeneratePermission() {
            return false;
        }

        protected void executeSync(@Nonnull CommandContext context) {
            if (!context.sender().hasPermission("*")) {
                context.sendMessage(Message.raw("You don't have permission to use this command."));
                return;
            }
            Ref<EntityStore> owningEntity = context.senderAsPlayerRef();
            Store<EntityStore> store = owningEntity.getStore();
            World world = store.getExternalData().getWorld();

            world.execute(() -> {
                float amount = amountArg.get(context);
                var statValue = store.getComponent(owningEntity,
                        EntityStatMap.getComponentType()).get(BleachStatTypes.getReiryoku());
                float currentMax = statValue.getMax();
                BleachStatTypes.addMaxReiryoku(owningEntity, store, "BR_SetMax",
                        currentMax + amount - 190f);
                context.sendMessage(Message.raw("Max Reiryoku set to " + (int)(currentMax + amount)));
            });
        }
    }

    }

