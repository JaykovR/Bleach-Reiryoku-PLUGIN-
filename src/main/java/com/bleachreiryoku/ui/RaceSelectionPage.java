package com.bleachreiryoku.ui;

import com.bleachreiryoku.playerData.playerStats;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

/*
 * After selecting a race goes to RaceConfirm Page.
 */
public class RaceSelectionPage extends InteractiveCustomUIPage<RaceSelectionPage.RaceEventData> {

    private static final String[] SHINIGAMI_STARTER_ITEMS = {
            "Shihakusho_Hakama",
            "Shihakusho_Kosode",
            "Weapon_Sword_Asauchi"
    };
    private static final String[] QUINCY_STARTER_ITEMS = {
            "Weapon_Shortbow_Kojaku"
    };

    public static class RaceEventData {
        public String selectedRace;

        public static final BuilderCodec<RaceEventData> CODEC =
                BuilderCodec.builder(RaceEventData.class, RaceEventData::new)
                        .append(
                                new KeyedCodec<>("SelectedRace", Codec.STRING),
                                (obj, val) -> obj.selectedRace = val,
                                obj -> obj.selectedRace
                        ).add()
                        .build();
    }

    public RaceSelectionPage(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, RaceEventData.CODEC);
    }

    @Override
    public void build(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull UICommandBuilder cmd,
            @Nonnull UIEventBuilder evt,
            @Nonnull Store<EntityStore> store
    ) {
        cmd.append("RaceSelection/RaceSelectionPage.ui");

        evt.addEventBinding(CustomUIEventBindingType.Activating, "#ShinigamiButton",
                new EventData().append("SelectedRace", playerStats.RACE_SHINIGAMI));
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#QuincyButton",
                new EventData().append("SelectedRace", playerStats.RACE_QUINCY));
//        evt.addEventBinding(CustomUIEventBindingType.Activating, "#HollowButton",
//                new EventData().append("SelectedRace", playerStats.RACE_HOLLOW));
//        evt.addEventBinding(CustomUIEventBindingType.Activating, "#HumanButton",
//                new EventData().append("SelectedRace", playerStats.RACE_HUMAN));
    }

    @Override
    public void handleDataEvent(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull Store<EntityStore> store,
            @Nonnull RaceEventData data
    ) {
        if (data.selectedRace == null) return;

        // Set the player's primary race
        playerStats stats = store.getComponent(ref, playerStats.getComponentType());
        if (stats != null) {
            stats.setPlayerPrimaryRace(data.selectedRace);
        }

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) return;

        // Give starter items
        if (playerStats.RACE_SHINIGAMI.equals(data.selectedRace)) {
            for (String itemId : SHINIGAMI_STARTER_ITEMS) {
                player.getInventory().getCombinedHotbarFirst().addItemStack(new ItemStack(itemId, 1));
            }
        } else if (playerStats.RACE_QUINCY.equals(data.selectedRace)) {
            for (String itemId : QUINCY_STARTER_ITEMS) {
                player.getInventory().getCombinedHotbarFirst().addItemStack(new ItemStack(itemId, 1));
            }
        }

        // Run commnads
        String username = playerRef.getUsername();
        String lpGroup  = data.selectedRace.toLowerCase(); // "shinigami", "quincy", etc.

        CommandManager.get().handleCommand(playerRef, "warp tutorial1");
        CommandManager.get().handleCommand(playerRef, "lp user " + username + " parent add " + lpGroup);

        // Go-to confirmation pge
        player.getPageManager().openCustomPage(ref, store,
                new RaceConfirmPage(playerRef, data.selectedRace));
    }
}