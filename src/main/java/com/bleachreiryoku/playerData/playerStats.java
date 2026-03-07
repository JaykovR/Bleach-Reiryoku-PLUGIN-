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
                    new KeyedCodec<>("activeShikai", Codec.INTEGER),
                    (component, value) -> component.activeShikai = value,
                    component -> component.activeShikai
            ).add()
            .build();



    // checks if player has shikai capability. 0 off, 1 on.
    public int activeShikai = 0;

    public playerStats(){}

    public playerStats(int activeShikai){
        this.activeShikai = Math.max(0, activeShikai);
    }

    public void setActiveShikai(){
        this.activeShikai = 1;
    }

    public void setNotActiveShikai(){
        this.activeShikai = 0;
    }

    public int getShikaiState(){
        return activeShikai;
    }

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
        return new playerStats((this.activeShikai));
    }


}
