package com.bleachreiryoku.playerData;

import java.util.List;

/**
 * KidoCatalog - the single source of truth for every kido technique, including its
 * progression economy.
 * Here's where the unlock cost, proficency gains, gaets and the starter kit are.
 *
 * Fields per entry:
 *   id             - the "*_Cost" interaction id; stored in loadout/unlocks, cast key
 *   rootId         - the Root interaction RunKidoSlot forks to cast the kido
 *   display        - readable name shown in the UI
 *   category       - "Hado" or "Bakudo"
 *   number         - the canon kido number (Hado 4 -> 4)
 *   profGain       - proficiency granted to the caster each time this kido HITS a target
 *   unlockCost     - Keiko (of this category) needed to unlock it
 *   minProficiency - proficiency (of this category) required before it can be unlocked
 *   startsUnlocked - true for the starter kit (Byakurai + Geki); granted on first join
 *
 * Economy summary:
 *   - Keiko earned = floor(proficiency / KEIKO_PER_PROFICIENCY), per category.
 *   - Unlocking spends Keiko (tracked in KidoUnlocks); available = earned - spent.
 *   - unlockCost grows with `number` so high-numbered kido are expensive.
 */
public final class KidoCatalog {

    /** Proficiency needed per 1 Keiko point, per category. */
    public static final int KEIKO_PER_PROFICIENCY = 5;

    public static final String CATEGORY_HADO = "Hado";
    public static final String CATEGORY_BAKUDO = "Bakudo";

    public record Entry(
            String id,
            String rootId,
            String display,
            String category,
            int number,
            int profGain,
            int unlockCost,
            int minProficiency,
            boolean startsUnlocked
    ) {}

    // Order here is the order shown in the UI list.
    public static final List<Entry> ENTRIES = List.of(
            new Entry("Hado1_Sho_Cost",               "Root_Kido_Hado1_Sho",               "Hado 1: Sho",               "Hado",     1,   1,    3,    0,   false),
            new Entry("Hado4_Byakurai_Cost",          "Root_Kido_Hado4_Byakurai",          "Hado 4: Byakurai",          "Hado",     4,   2,    0,    0,   true),
            new Entry("Hado31_Shakkaho_Cost",         "Root_Kido_Hado31_Shakkaho",         "Hado 31: Shakkaho",         "Hado",    31,   4,   12,   40,   false),
            new Entry("Hado33_Sokatsui_Cost",         "Root_Kido_Hado33_Sokatsui",         "Hado 33: Sokatsui",         "Hado",    33,   4,   14,   45,   false),
            new Entry("Bakudo9_Geki_Cost",            "Root_Kido_Bakudo9_Geki",            "Bakudo 9: Geki",            "Bakudo",   9,   2,    0,    0,   true),
            new Entry("Bakudo21_Sekienton_Cost",      "Root_Kido_Bakudo21_Sekienton",      "Bakudo 21: Sekienton",      "Bakudo",  21,   3,    8,   25,   false),
            new Entry("Bakudo30_Shitotsusansen_Cost", "Root_Kido_Bakudo30_Shitotsusansen", "Bakudo 30: Shitotsusansen", "Bakudo",  30,   5,   12,   40,   false)
    );

    // Find an entry by its "*_Cost" id, or null if not in the catalog.
    public static Entry byId(String id) {
        if (id == null || id.isEmpty()) return null;
        for (Entry e : ENTRIES) {
            if (e.id().equals(id)) return e;
        }
        return null;
    }

    // Display name for a stored id, or "Empty" if unassigned/unknown.
    public static String displayFor(String id) {
        Entry e = byId(id);
        return e == null ? "Empty" : e.display();
    }

    // The ids every new player starts with unlocked.
    public static java.util.List<String> starterIds() {
        java.util.ArrayList<String> out = new java.util.ArrayList<>();
        for (Entry e : ENTRIES) {
            if (e.startsUnlocked()) out.add(e.id());
        }
        return out;
    }

    private KidoCatalog() {}
}
