package com.bleachreiryoku.commands;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.bleachreiryoku.effects.BleachStatTypes;
import com.bleachreiryoku.playerData.playerStats;
import com.bleachreiryoku.ui.RaceSelectionPage;
import com.bleachreiryoku.ui.PlayerStatsPage;
import com.bleachreiryoku.ui.KidoGrimoirePage;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.universe.PlayerRef;
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

        this.addSubCommand(new ToggleHUDCommand("toggleHUD", "Toggles the custom HUD elements"));

        this.addSubCommand(new OpenKidoGrimoireCommand());

        this.addSubCommand(new setReiryokuMax());
        this.addSubCommand(new ChooseRaceCommand());
        this.addSubCommand(new GetPlayerRaceCommand());
        this.addSubCommand(new StatsCommand());



    }

    @Override
    protected boolean canGeneratePermission() {
        return false; //No permission required for base command
    }

    // /br help show all commands
    private static class HelpSubCommand extends CommandBase {

        public HelpSubCommand() {
            super("help", "Show available commands");
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
                BleachStatTypes.addMaxReiryoku(owningEntity, store, amount);
                context.sendMessage(Message.raw("Max Reiryoku increased by " + (int)amount));
            });
        }
    }


    private static class ChooseRaceCommand extends CommandBase {

        public ChooseRaceCommand() {
            super("chooseRace", "Opens the race selection screen");
        }

        @Override
        protected boolean canGeneratePermission() { return false; }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            Ref<EntityStore> ref = context.senderAsPlayerRef();
            Store<EntityStore> store = ref.getStore();
            World world = store.getExternalData().getWorld();

            world.execute(() -> {
                PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
                if (playerRef == null) return;

                Player player = store.getComponent(ref, Player.getComponentType());
                if (player == null) return;

                player.getPageManager().openCustomPage(ref, store, new RaceSelectionPage(playerRef));
            });
        }
    }

    private static class GetPlayerRaceCommand extends CommandBase {

        public GetPlayerRaceCommand() {
            super("getPlayerRace", "Displays your current race");
        }

        @Override
        protected boolean canGeneratePermission() { return false; }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            Ref<EntityStore> ref = context.senderAsPlayerRef();
            Store<EntityStore> store = ref.getStore();
            World world = store.getExternalData().getWorld();

            world.execute(() -> {
                playerStats stats = store.getComponent(ref, playerStats.getComponentType());
                if (stats == null) {
                    context.sendMessage(Message.raw("Could not retrieve your stats."));
                    return;
                }

                String race = stats.playerPrimaryRace == null || stats.playerPrimaryRace.isEmpty()
                        ? "None"
                        : stats.playerPrimaryRace;

                context.sendMessage(Message.raw("Your race is: " + race));
            });
        }
    }


    /*
     * Later there will be different Stats screens depening which race the player is.
     */
    private static class StatsCommand extends CommandBase {

        public StatsCommand() {
            super("stats", "Opens the player stats screen");
        }

        @Override
        protected boolean canGeneratePermission() { return false; }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            Ref<EntityStore> ref = context.senderAsPlayerRef();
            Store<EntityStore> store = ref.getStore();
            World world = store.getExternalData().getWorld();

            world.execute(() -> {
                PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
                if (playerRef == null) return;

                Player player = store.getComponent(ref, Player.getComponentType());
                if (player == null) return;

                player.getPageManager().openCustomPage(ref, store, new PlayerStatsPage(playerRef));
            });
        }
    }

    // /br kido - opens the Kido Grimoire (unlock UI)
    private static class OpenKidoGrimoireCommand extends CommandBase {

        public OpenKidoGrimoireCommand() {
            super("kido", "Opens the Kido Grimoire to unlock techniques");
        }

        @Override
        protected boolean canGeneratePermission() { return false; }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            Ref<EntityStore> ref = context.senderAsPlayerRef();
            Store<EntityStore> store = ref.getStore();
            World world = store.getExternalData().getWorld();

            world.execute(() -> {
                PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
                if (playerRef == null) return;

                Player player = store.getComponent(ref, Player.getComponentType());
                if (player == null) return;

                player.getPageManager().openCustomPage(ref, store, new KidoGrimoirePage(playerRef));
            });
        }
    }

}

