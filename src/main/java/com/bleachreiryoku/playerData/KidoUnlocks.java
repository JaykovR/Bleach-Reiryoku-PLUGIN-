package com.bleachreiryoku.playerData;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.ArrayList;
import java.util.List;

/**
 * KidoUnlocks - Progression per player, which kido they had unlocked and how much they have
 *               spent on each class. (Hado/Bakudo)
 *
 * Keiko earned is derived from proficiency (lives on playerStats), so it can
 * never be double-granted. Available Keiko = earned - spent. This component only
 * needs to track the spent side plus the unlocked set.
 *
 */
public class KidoUnlocks implements Component<EntityStore> {

    public static ComponentType<EntityStore, KidoUnlocks> TYPE;

    public static void setComponentType(ComponentType<EntityStore, KidoUnlocks> type) {
        TYPE = type;
    }

    public static ComponentType<EntityStore, KidoUnlocks> getComponentType() {
        return TYPE;
    }

    // Unlocked kido "*_Cost" ids.
    public String[] unlocked = new String[0];

    // Keiko spent per class (available = earned-from-proficiency minus this).
    public int hadoKeikoSpent = 0;
    public int bakudoKeikoSpent = 0;

    public static final BuilderCodec<KidoUnlocks> CODEC = BuilderCodec
            .builder(KidoUnlocks.class, KidoUnlocks::new)
            .append(
                    new KeyedCodec<>("Unlocked", new ArrayCodec<>(Codec.STRING, String[]::new)),
                    (c, v) -> c.unlocked = (v == null ? new String[0] : v),
                    c -> c.unlocked
            ).add()
            .append(
                    new KeyedCodec<>("HadoKeikoSpent", Codec.INTEGER),
                    (c, v) -> c.hadoKeikoSpent = v,
                    c -> c.hadoKeikoSpent
            ).add()
            .append(
                    new KeyedCodec<>("BakudoKeikoSpent", Codec.INTEGER),
                    (c, v) -> c.bakudoKeikoSpent = v,
                    c -> c.bakudoKeikoSpent
            ).add()
            .build();

    public KidoUnlocks() {}

    public KidoUnlocks(String[] unlocked, int hadoKeikoSpent, int bakudoKeikoSpent) {
        this.unlocked = (unlocked == null ? new String[0] : unlocked);
        this.hadoKeikoSpent = hadoKeikoSpent;
        this.bakudoKeikoSpent = bakudoKeikoSpent;
    }

    public boolean isUnlocked(String kidoId) {
        if (kidoId == null) return false;
        for (String s : unlocked) {
            if (kidoId.equals(s)) return true;
        }
        return false;
    }

    // Adds a kido id to the unlocked set (no-op if already present).
    public void addUnlocked(String kidoId) {
        if (kidoId == null || isUnlocked(kidoId)) return;
        String[] next = new String[unlocked.length + 1];
        System.arraycopy(unlocked, 0, next, 0, unlocked.length);
        next[unlocked.length] = kidoId;
        unlocked = next;
    }

    public int getKeikoSpent(String category) {
        return KidoCatalog.CATEGORY_BAKUDO.equals(category) ? bakudoKeikoSpent : hadoKeikoSpent;
    }

    public void addKeikoSpent(String category, int amount) {
        if (KidoCatalog.CATEGORY_BAKUDO.equals(category)) {
            bakudoKeikoSpent += amount;
        } else {
            hadoKeikoSpent += amount;
        }
    }

    // ---- Keiko economy ----

    // Total Keiko ever earned in a class, from proficiency.
    public static int keikoEarned(int proficiency) {
        return proficiency / KidoCatalog.KEIKO_PER_PROFICIENCY;
    }

    /** Keiko currently available to spend in a class. */
    public int keikoAvailable(String category, int hadoProficiency, int bakudoProficiency) {
        int prof = KidoCatalog.CATEGORY_BAKUDO.equals(category) ? bakudoProficiency : hadoProficiency;
        return Math.max(0, keikoEarned(prof) - getKeikoSpent(category));
    }

    @NullableDecl
    @Override
    public KidoUnlocks clone() {
        String[] copy = new String[unlocked.length];
        System.arraycopy(unlocked, 0, copy, 0, unlocked.length);
        return new KidoUnlocks(copy, hadoKeikoSpent, bakudoKeikoSpent);
    }

    // Convenience for seeding starter kit.
    public static KidoUnlocks withStarters() {
        List<String> starters = KidoCatalog.starterIds();
        return new KidoUnlocks(starters.toArray(new String[0]), 0, 0);
    }
}
