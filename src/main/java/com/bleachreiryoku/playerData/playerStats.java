package com.bleachreiryoku.playerData;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import javax.swing.*;


/**
 * playerStats - Event listener for player events.
 */
public class playerStats implements Component<EntityStore> {

    public static ComponentType<EntityStore, playerStats> TYPE;

    public static void setComponentType(ComponentType<EntityStore, playerStats> type){
        TYPE = type;
    }

    public static ComponentType<EntityStore, playerStats> getComponentType(){
        return TYPE;
    }

    // Builder CODEC, apparently you need to build each stat. For now, I'm using just booleans since the system
    // it's still in the early primitive stages.
    public static final BuilderCodec<playerStats> CODEC = BuilderCodec
            .builder(playerStats.class, playerStats::new)
            .append(
                    new KeyedCodec<>("ShikaiHozukimaru", Codec.BOOLEAN),
                    (component, value) -> component.ShikaiHozukimaru = value,
                    component -> component.ShikaiHozukimaru
            ).add()
            .append(
                    new KeyedCodec<>("ShikaiWabisuke", Codec.BOOLEAN),
                    (component, value) -> component.ShikaiWabisuke = value,
                    component -> component.ShikaiWabisuke
            ).add()
            .append(
                    new KeyedCodec<>("ShikaiSodeNoShirayuki", Codec.BOOLEAN),
                    (component, value) -> component.ShikaiSodeNoShirayuki = value,
                    component -> component.ShikaiSodeNoShirayuki
            ).add()
            .append(
                    new KeyedCodec<>("ShikaiBenihime", Codec.BOOLEAN),
                    (component, value) -> component.ShikaiBenihime = value,
                    component -> component.ShikaiBenihime
            ).add()
            .append(
                    new KeyedCodec<>("ShikaiSenbonzakura", Codec.BOOLEAN),
                    (component, value) -> component.ShikaiSenbonzakura = value,
                    component -> component.ShikaiSenbonzakura
            ).add()
            .append(
                    new KeyedCodec<>("ShikaiZangetsu", Codec.BOOLEAN),
                    (component, value) -> component.ShikaiZangetsu = value,
                    component -> component.ShikaiZangetsu
            ).add()
            .append(
                    new KeyedCodec<>("BankaiZangetsu", Codec.BOOLEAN),
                    (component, value) -> component.BankaiZangetsu = value,
                    component -> component.BankaiZangetsu
            ).add()
            .append(
                    new KeyedCodec<>("HollowMask", Codec.BOOLEAN),
                    (component, value) -> component.HollowMask = value,
                    component -> component.HollowMask
            ).add()
            .append(
                    new KeyedCodec<>("TotalKills", Codec.INTEGER),
                    (component, value) -> component.totalKills = value,
                    component -> component.totalKills
            ).add()
            .append(
                    new KeyedCodec<>("TotalHollowKills", Codec.INTEGER),
                    (component, value) -> component.totalHollowKills = value,
                    component -> component.totalHollowKills
            ).add()
            .append(
                    new KeyedCodec<>("PlayerPrimaryRace", Codec.STRING),
                    (component, value) -> component.playerPrimaryRace = value,
                    component -> component.playerPrimaryRace
            ).add()
            .append(
                    new KeyedCodec<>("PlayerSecondaryRace", Codec.STRING),
                    (component, value) -> component.playerSecondaryRace = value,
                    component -> component.playerSecondaryRace
            ).add()
            .append(
                    new KeyedCodec<>("HadoProficiency", Codec.INTEGER),
                    (component, value) -> component.hadoProficiency = value,
                    component -> component.hadoProficiency
            ).add()
            .append(
                    new KeyedCodec<>("BakudoProficiency", Codec.INTEGER),
                    (component, value) -> component.bakudoProficiency = value,
                    component -> component.bakudoProficiency
            ).add()
            .build();


    // REDUNDANT FOR NOW checks if player has shikai capability. 0 off, 1 on.
    public int ActiveShikai = 0;

    // ---------------- SHIKAI CHECKERS -------------------------
    //Replaced later on - This is not gonna be replaced until the player is bound to a specific weapon.
    public boolean ShikaiHozukimaru = false;
    public boolean ShikaiWabisuke = false;
    public boolean ShikaiSodeNoShirayuki = false;
    public boolean ShikaiBenihime = false;
    public boolean ShikaiSenbonzakura = false;
    public boolean ShikaiZangetsu = false;
    public boolean BankaiZangetsu = false;
    // ------------------------------------------------------------

    // Replaced later on
    public boolean HollowMask = false;

    // Kill tracking, will be used in UI later.
    public int totalKills = 0;
    public int totalHollowKills = 0;

    // Kido proficiency — increments through casting. Persisted via codec.
    // No combat use yet; for now it's stored & displayed via the /br stats UI.
    public int hadoProficiency = 0;
    public int bakudoProficiency = 0;

    public static final String RACE_SHINIGAMI   = "Shinigami";
    public static final String RACE_QUINCY      = "Quincy";
    public static final String RACE_HOLLOW      = "Hollow";
    public static final String RACE_FULLBRINGER = "Fullbringer";

    public String playerPrimaryRace;
    public String playerSecondaryRace;

    public playerStats(){}

    // This is where the Player Stats become property of the player, per se.
    public playerStats(boolean shikaiHozukimaru, boolean shikaiWabisuke,
                       boolean shikaiSodeNoShirayuki, boolean shikaiBenihime, boolean shikaiSenbonzakura,
                       boolean shikaiZangetsu, boolean bankaiZangetsu, boolean hollowMask, int totalKills,
                       int totalHollowKills, String primaryRace, String secondaryRace,
                       int hadoProficiency, int bakudoProficiency)
    {
        this.ShikaiHozukimaru = shikaiHozukimaru;
        this.ShikaiWabisuke = shikaiWabisuke;
        this.ShikaiSodeNoShirayuki = shikaiSodeNoShirayuki;
        this.ShikaiBenihime = shikaiBenihime;
        this.ShikaiSenbonzakura = shikaiSenbonzakura;
        this.ShikaiZangetsu = shikaiZangetsu;
        this.BankaiZangetsu = bankaiZangetsu;
        this.HollowMask = hollowMask;
        this.totalKills = totalKills;
        this.totalHollowKills = totalHollowKills;
        this.playerPrimaryRace = primaryRace;
        this.playerSecondaryRace = secondaryRace;
        this.hadoProficiency = hadoProficiency;
        this.bakudoProficiency = bakudoProficiency;
    }

    public void setPlayerPrimaryRace(String chosenRace) {
        if (chosenRace.equals(RACE_SHINIGAMI) || chosenRace.equals(RACE_QUINCY)
                || chosenRace.equals(RACE_HOLLOW) || chosenRace.equals(RACE_FULLBRINGER)) {
            playerPrimaryRace = chosenRace;
        }
    }


    // Set Shikai Boolean to True.
    public void setActiveShikaiHozukimaru(){
        this.ShikaiHozukimaru = true;
    }
    public void setActiveShikaiWabisuke(){
        this.ShikaiWabisuke = true;
    }
    public void setActiveShikaiSodeNoShirayuki(){
        this.ShikaiSodeNoShirayuki = true;
    }
    public void setActiveShikaiBenihime(){
        this.ShikaiBenihime = true;
    }
    public void setActiveShikaiSenbonzakura() {
        this.ShikaiSenbonzakura = true;
    }
    public void setActiveShikaiZangetsu(){
        this.ShikaiZangetsu = true;
    }
    public void setActiveBankaiZangetsu(){
        this.BankaiZangetsu = true;
    }

    public void setActiveHollowMask(){
        this.HollowMask = true;
    }

    // Sets all false at once.
    public void deactivateShikai(){
        this.ShikaiHozukimaru = false;
        this.ShikaiWabisuke = false;
        this.ShikaiSodeNoShirayuki = false;
        this.ShikaiBenihime = false;
        this.ShikaiSenbonzakura = false;
        this.ShikaiZangetsu = false;
        this.HollowMask = false;
    }

    // Get your shikai state if it's true or false.
    public boolean getShikaiBenihimeState(){
        return ShikaiBenihime;
    }
    public boolean getShikaiWabisukeState(){
        return ShikaiWabisuke;
    }
    public boolean getShikaiSodeNoShirayukiState(){
        return ShikaiSodeNoShirayuki;
    }
    public boolean getShikaiHozukimaruState(){
        return ShikaiHozukimaru;
    }
    public boolean getShikaiSenbonzakuraState(){
        return ShikaiSenbonzakura;
    }
    public boolean getShikaiZangetsuState() {
        return ShikaiZangetsu;
    }
    public boolean getBankaiZangetsuState(){
        return BankaiZangetsu;
    }
    public boolean getHollowMaskState(){
        return HollowMask;
    }

    // Kill tracking
    public int getTotalKills() { return totalKills; }
    public int getTotalHollowKills() { return totalHollowKills; }
    public void incrementKills() { this.totalKills++; }
    public void incrementHollowKills() { this.totalHollowKills++; this.totalKills++; }

    // Kido proficiency — placeholders. Add* are clamped to >= 0 to avoid negative drift
    // if a future caller passes a negative delta intending to "undo" a cast.
    public int getHadoProficiency()   { return hadoProficiency; }
    public int getBakudoProficiency() { return bakudoProficiency; }
    public void incrementHadoProficiency()   { this.hadoProficiency++; }
    public void incrementBakudoProficiency() { this.bakudoProficiency++; }
    public void addHadoProficiency(int amount) {
        this.hadoProficiency = Math.max(0, this.hadoProficiency + amount);
    }
    public void addBakudoProficiency(int amount) {
        this.bakudoProficiency = Math.max(0, this.bakudoProficiency + amount);
    }


//    public void setActiveShikai(){
//        this.ActiveShikai = 1;
//    }
//    public void setNotActiveShikai(){
//        this.ActiveShikai = 0;
//    }
//
//    public int getShikaiState(){
//        return ActiveShikai;
//    }

    // This will be the same as XP, LVL in the future. For now it's not used. Later on it will serve the purpose to scale
    // damage for Kido, Skills, Etc, as well as brining the player more protection and stuff.
    public static final long[] REIRYOKU_THRESHOLDS = {
            0,      // Dead. Unanimated Object.
            10,     // Living Being
            50,     // Spiritually Aware
            150,    // Awakened Soul (early Chad/Orihime)
            200,    // player Start
            300,    // Academy Student - Shinigami  Trainee
            500,    // Unseated Officer - Graduated, Unranked Shinigami
            600,    // Shikai Unlock Requirement
            750,    // Low-Seat - 10th-20th place
            1000,   // Mid-Seat - 5th-9th place
            1400,   // High Seat - 3rd-4th place (Ikkaku/Yumichika, Soul Society arc)
            1900,   // Lieutenant - Fukutaicho (Renji pre-Bankai)
            2500,   // Bankai-Capable Shinigami (Renji w/ Bankai, Soul Society arc), Bankai Unlock Requirement
            3200,   // Captain - Standard Taicho (Soifon, Komamura)
            4000,   // Veteran Captain (Byakuya Soul Society arc, Toshiro Hueco Mundo)
            5000,   // Pre-Shikai Kenpachi / Mid-tier Espada (Nnoitra, Grimmjow)
            6200,   // High-tier Espada / Strong Captain (Halibel, post-timeskip Toshiro)
            7500,   // Top Espada / Bankai Kenpachi (Ulquiorra Resurrección, Starrk)
            9000,   // Captain-Commander tier (Yamamoto, TYBW Shunsui)
            11000,  // Royal Guard / Transcendent (Zero Division, Aizen pre-Hogyoku)
            13500,  // Hogyoku-Aizen / Final Getsuga Ichigo
            16000,  // Yhwach (The Almighty) / Soul King-tier
            20000   // Beyond Comprehension
    };

    @NullableDecl
    @Override
    public playerStats clone() {
        return new playerStats(this.ShikaiHozukimaru, this.ShikaiWabisuke, this.ShikaiSodeNoShirayuki,
                this.ShikaiBenihime, this.ShikaiSenbonzakura, this.ShikaiZangetsu, this.BankaiZangetsu, this.HollowMask
        , this.totalKills, totalHollowKills, this.playerPrimaryRace, this.playerSecondaryRace,
                this.hadoProficiency, this.bakudoProficiency);
    }


}
