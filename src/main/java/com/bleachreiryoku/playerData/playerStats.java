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
            .build();


    // REDUNDANT FOR NOW checks if player has shikai capability. 0 off, 1 on.
    public int ActiveShikai = 0;

    //Replaced later on.
    public boolean ShikaiHozukimaru = false;
    public boolean ShikaiWabisuke = false;
    public boolean ShikaiSodeNoShirayuki = false;
    public boolean ShikaiBenihime = false;



    public playerStats(){}

    public playerStats(boolean shikaiHozukimaru, boolean shikaiWabisuke,
                       boolean shikaiSodeNoShirayuki, boolean shikaiBenihime)
    {
        this.ShikaiHozukimaru = shikaiHozukimaru;
        this.ShikaiWabisuke = shikaiWabisuke;
        this.ShikaiSodeNoShirayuki = shikaiSodeNoShirayuki;
        this.ShikaiBenihime = shikaiBenihime;
    }

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

    // sets all false at once.
    public void deactivateShikai(){
        this.ShikaiHozukimaru = false;
        this.ShikaiWabisuke = false;
        this.ShikaiSodeNoShirayuki = false;
        this.ShikaiBenihime = false;
    }

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
        return new playerStats(this.ShikaiHozukimaru, this.ShikaiWabisuke, this.ShikaiSodeNoShirayuki, this.ShikaiBenihime);
    }


}
