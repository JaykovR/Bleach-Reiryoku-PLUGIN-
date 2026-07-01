package com.bleachreiryoku.playerData;

import java.util.List;

/**
 * KidoCatalog - the single source of truth for every kido technique that can be
 * assigned in the Kido Selection UI.
 *
 * Fields per entry:
 *   id       - the "*_Cost" interaction id; stored in KidoLoadout and shown selected
 *   rootId   - the Root interaction RunKidoSlot forks to actually cast the kido
 *   display  - readable name shown in the UI list and on slot buttons
 *   category - "Hado" or "Bakudo", used only for grouping/labels in the UI
 */
public final class KidoCatalog {

    public record Entry(String id, String rootId, String display, String category) {}

    // Order here is the order shown in the UI list.
    public static final List<Entry> ENTRIES = List.of(
            new Entry("Hado1_Sho_Cost",               "Root_Kido_Hado1_Sho",               "Hado 1: Sho",               "Hado"),
            new Entry("Hado4_Byakurai_Cost",          "Root_Kido_Hado4_Byakurai",          "Hado 4: Byakurai",          "Hado"),
            new Entry("Hado31_Shakkaho_Cost",         "Root_Kido_Hado31_Shakkaho",         "Hado 31: Shakkaho",         "Hado"),
            new Entry("Hado33_Sokatsui_Cost",         "Root_Kido_Hado33_Sokatsui",         "Hado 33: Sokatsui",         "Hado"),
            new Entry("Bakudo9_Geki_Cost",            "Root_Kido_Bakudo9_Geki",            "Bakudo 9: Geki",            "Bakudo"),
            new Entry("Bakudo21_Sekienton_Cost",      "Root_Kido_Bakudo21_Sekienton",      "Bakudo 21: Sekienton",      "Bakudo"),
            new Entry("Bakudo30_Shitotsusansen_Cost", "Root_Kido_Bakudo30_Shitotsusansen", "Bakudo 30: Shitotsusansen", "Bakudo")
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

    private KidoCatalog() {}
}
