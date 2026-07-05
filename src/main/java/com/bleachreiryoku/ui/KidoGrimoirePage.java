package com.bleachreiryoku.ui;

import com.bleachreiryoku.playerData.KidoCatalog;
import com.bleachreiryoku.playerData.KidoUnlocks;
import com.bleachreiryoku.playerData.playerStats;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

/**
 * Kido Grimoire - the unlock UI. Two columns (Hado / Bakudo), each showing the
 * player's available Keiko and every kido in that class with its state:
 *   - UNLOCKED           (already owned)
 *   - "Unlock (X Keiko)" (buyable now: enough Keiko + meets proficiency gate)
 *   - "Locked (need P)"  (proficiency gate not met)
 *   - "Need X Keiko"     (gate met but not enough Keiko)
 *
 * Clicking a buyable row spends the Keiko (records it in KidoUnlocks) and unlocks it.
 * Opened via the /kido command.
 */
public class KidoGrimoirePage extends InteractiveCustomUIPage<KidoGrimoirePage.GrimoireEventData> {

    public KidoGrimoirePage(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, GrimoireEventData.CODEC);
    }

    public static class GrimoireEventData {
        public String buyId;
        public String done;

        public static final BuilderCodec<GrimoireEventData> CODEC =
                BuilderCodec.builder(GrimoireEventData.class, GrimoireEventData::new)
                        .append(new KeyedCodec<>("BuyId", Codec.STRING),
                                (o, v) -> o.buyId = v, o -> o.buyId).add()
                        .append(new KeyedCodec<>("Done", Codec.STRING),
                                (o, v) -> o.done = v, o -> o.done).add()
                        .build();
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref,
                      @Nonnull UICommandBuilder cmd,
                      @Nonnull UIEventBuilder evt,
                      @Nonnull Store<EntityStore> store) {
        cmd.append("KidoGrimoire/KidoGrimoirePage.ui");
        render(ref, cmd, evt, store);
    }

    private void render(@Nonnull Ref<EntityStore> ref,
                        @Nonnull UICommandBuilder cmd,
                        @Nonnull UIEventBuilder evt,
                        @Nonnull Store<EntityStore> store) {

        playerStats stats = store.getComponent(ref, playerStats.getComponentType());
        KidoUnlocks unlocks = store.getComponent(ref, KidoUnlocks.getComponentType());
        if (unlocks == null) unlocks = KidoUnlocks.withStarters(); // display fallback

        int hadoProf = stats != null ? stats.getHadoProficiency() : 0;
        int bakudoProf = stats != null ? stats.getBakudoProficiency() : 0;

        int hadoKeiko = unlocks.keikoAvailable(KidoCatalog.CATEGORY_HADO, hadoProf, bakudoProf);
        int bakudoKeiko = unlocks.keikoAvailable(KidoCatalog.CATEGORY_BAKUDO, hadoProf, bakudoProf);

        // Static labels (translated) — set here rather than hardcoded in the .ui so they
        // localize to the player's language via the kidoGrimoire.lang files.
        cmd.set("#SubHint.TextSpans", Message.translation("kidoGrimoire.subHint"));
        cmd.set("#HadoHeader.TextSpans", Message.translation("kidoGrimoire.hado"));
        cmd.set("#BakudoHeader.TextSpans", Message.translation("kidoGrimoire.bakudo"));
        cmd.set("#DoneButton.TextSpans", Message.translation("kidoGrimoire.done"));

        cmd.set("#HadoKeiko.TextSpans", Message.translation("kidoGrimoire.hadoKeiko").param("count", hadoKeiko));
        cmd.set("#BakudoKeiko.TextSpans", Message.translation("kidoGrimoire.bakudoKeiko").param("count", bakudoKeiko));

        cmd.clear("#HadoList");
        cmd.clear("#BakudoList");

        int hadoIdx = 0;
        int bakudoIdx = 0;
        for (KidoCatalog.Entry e : KidoCatalog.ENTRIES) {
            boolean isBakudo = KidoCatalog.CATEGORY_BAKUDO.equals(e.category());
            String listSel = isBakudo ? "#BakudoList" : "#HadoList";
            int idx = isBakudo ? bakudoIdx++ : hadoIdx++;
            String rowSel = listSel + "[" + idx + "]";

            int prof = isBakudo ? bakudoProf : hadoProf;
            int keiko = isBakudo ? bakudoKeiko : hadoKeiko;

            cmd.append(listSel, "KidoGrimoire/KidoGrimoireRow.ui");
            cmd.set(rowSel + " #RowName.Text", e.display());

            Message buttonText;
            boolean clickable = false;
            if (unlocks.isUnlocked(e.id())) {
                buttonText = Message.translation("kidoGrimoire.status.unlocked");
            } else if (prof < e.minProficiency()) {
                buttonText = Message.translation("kidoGrimoire.status.lockedProf")
                        .param("prof", e.minProficiency());
            } else if (keiko < e.unlockCost()) {
                buttonText = Message.translation("kidoGrimoire.status.needKeiko")
                        .param("cost", e.unlockCost());
            } else {
                buttonText = Message.translation("kidoGrimoire.status.unlock")
                        .param("cost", e.unlockCost());
                clickable = true;
            }
            cmd.set(rowSel + " #RowButton.TextSpans", buttonText);

            if (clickable) {
                evt.addEventBinding(CustomUIEventBindingType.Activating, rowSel + " #RowButton",
                        new EventData().append("BuyId", e.id()));
            }
        }

        evt.addEventBinding(CustomUIEventBindingType.Activating, "#DoneButton",
                new EventData().append("Done", "1"));
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref,
                                @Nonnull Store<EntityStore> store,
                                @Nonnull GrimoireEventData data) {

        if (data.done != null && !data.done.isEmpty()) {
            Player player = store.getComponent(ref, Player.getComponentType());
            if (player != null) {
                player.getPageManager().setPage(ref, store, Page.None);
            }
            return;
        }

        if (data.buyId == null || data.buyId.isEmpty()) return;

        KidoCatalog.Entry e = KidoCatalog.byId(data.buyId);
        if (e == null) return;

        playerStats stats = store.getComponent(ref, playerStats.getComponentType());
        KidoUnlocks unlocks = store.getComponent(ref, KidoUnlocks.getComponentType());
        if (stats == null || unlocks == null) return;

        // Re-validate server-side.
        if (unlocks.isUnlocked(e.id())) return;

        int hadoProf = stats.getHadoProficiency();
        int bakudoProf = stats.getBakudoProficiency();
        int prof = KidoCatalog.CATEGORY_BAKUDO.equals(e.category()) ? bakudoProf : hadoProf;
        if (prof < e.minProficiency()) return;

        int keiko = unlocks.keikoAvailable(e.category(), hadoProf, bakudoProf);
        if (keiko < e.unlockCost()) return;

        // Commit: spend + unlock.
        unlocks.addKeikoSpent(e.category(), e.unlockCost());
        unlocks.addUnlocked(e.id());

        // Re-render in place to reflect the purchase.
        UICommandBuilder cmd = new UICommandBuilder();
        UIEventBuilder evt = new UIEventBuilder();
        render(ref, cmd, evt, store);
        sendUpdate(cmd, evt, false);
    }
}
