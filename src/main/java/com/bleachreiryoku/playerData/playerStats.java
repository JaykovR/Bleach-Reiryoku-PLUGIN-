package com.bleachreiryoku.playerData;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

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

    public playerStats(){}

    // This is where the Player Stats become property of the player, per se.
    public playerStats(boolean shikaiHozukimaru, boolean shikaiWabisuke,
                       boolean shikaiSodeNoShirayuki, boolean shikaiBenihime, boolean shikaiSenbonzakura,
                       boolean shikaiZangetsu, boolean bankaiZangetsu, boolean hollowMask)
    {
        this.ShikaiHozukimaru = shikaiHozukimaru;
        this.ShikaiWabisuke = shikaiWabisuke;
        this.ShikaiSodeNoShirayuki = shikaiSodeNoShirayuki;
        this.ShikaiBenihime = shikaiBenihime;
        this.ShikaiSenbonzakura = shikaiSenbonzakura;
        this.ShikaiZangetsu = shikaiZangetsu;
        this.BankaiZangetsu = bankaiZangetsu;
        this.HollowMask = hollowMask;
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
            0,      // new player
            100,    // trainee level
            300,    // low level
            500,    // average level
            900,    // shikai user?
            1500,   // bankai user?
            2500,   // strong
            4000,   // very strong
            6000,   // i dont know.
            9000
    };

    @NullableDecl
    @Override
    public playerStats clone() {
        return new playerStats(this.ShikaiHozukimaru, this.ShikaiWabisuke, this.ShikaiSodeNoShirayuki,
                this.ShikaiBenihime, this.ShikaiSenbonzakura, this.ShikaiZangetsu, this.BankaiZangetsu, this.HollowMask);
    }


}
