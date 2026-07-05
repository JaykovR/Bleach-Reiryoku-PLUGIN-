package com.bleachreiryoku.ui;

import com.bleachreiryoku.systems.MeditationService;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

/**
 * Soul Meditation menu (opened by using the Soul Meditation Seat block). Four buttons:
 *   Meditate        -> start a 30s meditation (once per in-game day) for +15 Reiryoku
 *   Unlock New Kido -> opens the Kido Grimoire
 *   View Stats      -> opens the Player Stats page
 *   WIP             -> placeholder, does nothing for now. Here we will have probably the
 *                                   inner-world access or the stance selector.
 *
 * The seat position is passed in so Meditate can teleport the player onto it.
 */
public class SoulMeditationPage extends InteractiveCustomUIPage<SoulMeditationPage.MeditationEventData> {

    private final double seatX, seatY, seatZ;

    public SoulMeditationPage(@Nonnull PlayerRef playerRef, double seatX, double seatY, double seatZ) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, MeditationEventData.CODEC);
        this.seatX = seatX;
        this.seatY = seatY;
        this.seatZ = seatZ;
    }

    public static class MeditationEventData {
        public String action;

        public static final BuilderCodec<MeditationEventData> CODEC =
                BuilderCodec.builder(MeditationEventData.class, MeditationEventData::new)
                        .append(new KeyedCodec<>("Action", Codec.STRING),
                                (o, v) -> o.action = v, o -> o.action).add()
                        .build();
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref,
                      @Nonnull UICommandBuilder cmd,
                      @Nonnull UIEventBuilder evt,
                      @Nonnull Store<EntityStore> store) {
        cmd.append("SoulMeditation/SoulMeditationPage.ui");

        evt.addEventBinding(CustomUIEventBindingType.Activating, "#MeditateButton",
                new EventData().append("Action", "meditate"));
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#UnlockButton",
                new EventData().append("Action", "unlock"));
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#StatsButton",
                new EventData().append("Action", "stats"));
        // WIP button intentionally has no binding (does nothing).
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#DoneButton",
                new EventData().append("Action", "close"));
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref,
                                @Nonnull Store<EntityStore> store,
                                @Nonnull MeditationEventData data) {

        if (data.action == null) return;

        Player player = store.getComponent(ref, Player.getComponentType());
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());

        switch (data.action) {
            case "close" -> {
                if (player != null) player.getPageManager().setPage(ref, store, Page.None);
            }
            case "unlock" -> {
                if (player != null && playerRef != null) {
                    player.getPageManager().openCustomPage(ref, store, new KidoGrimoirePage(playerRef));
                }
            }
            case "stats" -> {
                if (player != null && playerRef != null) {
                    player.getPageManager().openCustomPage(ref, store, new PlayerStatsPage(playerRef));
                }
            }
            case "meditate" -> {
                // Close the menu, then start meditation off-tick
                if (player != null) player.getPageManager().setPage(ref, store, Page.None);

                World world = store.getExternalData().getWorld();
                world.execute(() -> {
                    if (!ref.isValid()) return;
                    MeditationService.StartResult result =
                            MeditationService.start(ref, store, seatX, seatY, seatZ);
                    messageResult(ref, store, result);
                });
            }
            default -> { }
        }
    }

    private void messageResult(Ref<EntityStore> ref, Store<EntityStore> store,
                               MeditationService.StartResult result) {
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef == null) return;
        // TODO TRANSLATION
        String msg = switch (result) {
            case STARTED -> "server.meditation.started";
            case ON_COOLDOWN -> "server.meditation.cooldown";
            case ALREADY_MEDITATING -> "server.meditation.meditating";
            case ERROR -> "server.meditation.error";
        };
        playerRef.sendMessage(Message.translation(msg));
    }
}
