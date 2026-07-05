package com.bleachreiryoku.ui;

import com.bleachreiryoku.playerData.playerStats;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;


public class PlayerStatsPage extends InteractiveCustomUIPage<PlayerStatsPage.CloseEventData> {


    public static class CloseEventData {
        public static final BuilderCodec<CloseEventData> CODEC =
                BuilderCodec.builder(CloseEventData.class, CloseEventData::new).build();
    }

    public PlayerStatsPage(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, CloseEventData.CODEC);
    }

    @Override
    public void build(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull UICommandBuilder cmd,
            @Nonnull UIEventBuilder evt,
            @Nonnull Store<EntityStore> store
    ) {
        cmd.append("PlayerStats/PlayerStatsPage.ui");

        // Static labels (translated).
        cmd.set("#CloseButton.TextSpans", Message.translation("playerStats.close"));
        cmd.set("#ShikaiHeader.TextSpans", Message.translation("playerStats.shikaiUnlocked"));

        playerStats stats = store.getComponent(ref, playerStats.getComponentType());

        if (stats != null) {
            boolean noRace = (stats.playerPrimaryRace == null || stats.playerPrimaryRace.isEmpty());
            Message raceMsg = noRace
                    ? Message.translation("playerStats.none")
                    : Message.raw(stats.playerPrimaryRace);

            cmd.set("#RaceLabel.TextSpans",        Message.translation("playerStats.race").param("race", raceMsg));
            cmd.set("#TotalKillsLabel.TextSpans",  Message.translation("playerStats.totalKills").param("count", stats.getTotalKills()));
            cmd.set("#HollowKillsLabel.TextSpans", Message.translation("playerStats.hollowKills").param("count", stats.getTotalHollowKills()));
            cmd.set("#HadoLabel.TextSpans",        Message.translation("playerStats.hadoProf").param("count", stats.getHadoProficiency()));
            cmd.set("#BakudoLabel.TextSpans",      Message.translation("playerStats.bakudoProf").param("count", stats.getBakudoProficiency()));

            List<String> unlocked = new ArrayList<>();
            if (stats.getShikaiZangetsuState())       unlocked.add("Zangetsu");
            if (stats.getBankaiZangetsuState())       unlocked.add("Tensa Zangetsu (Bankai)");
            if (stats.getShikaiHozukimaruState())     unlocked.add("Hozukimaru");
            if (stats.getShikaiWabisukeState())       unlocked.add("Wabisuke");
            if (stats.getShikaiBenihimeState())       unlocked.add("Benihime");
            if (stats.getShikaiSodeNoShirayukiState())unlocked.add("Sode no Shirayuki");
            if (stats.getShikaiSenbonzakuraState())   unlocked.add("Senbonzakura");

            if (unlocked.isEmpty()) {
                cmd.set("#ShikaiList.TextSpans", Message.translation("playerStats.none"));
            } else {
                cmd.set("#ShikaiList.Text", String.join(", ", unlocked));
            }
        }
        // If stats is null, the .ui defaults stay as-is.

        evt.addEventBinding(CustomUIEventBindingType.Activating, "#CloseButton", null);
    }

    @Override
    public void handleDataEvent(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull Store<EntityStore> store,
            @Nonnull CloseEventData data
    ) {
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player != null) {
            player.getPageManager().setPage(ref, store, Page.None);
        }
    }
}
