package com.bleachreiryoku.hud;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import javax.annotation.Nonnull;

public class ReiryokuBar extends CustomUIHud {
    public ReiryokuBar(@Nonnull PlayerRef playerRef){
        super(playerRef);
    }

    @Override
    protected void build(@Nonnull UICommandBuilder uiCommandBuilder){
        uiCommandBuilder.append("ReiryokuBar/Reiryoku.ui");
    }

    public void updateBar(float percentFilled, float current, float max) {
        UICommandBuilder builder = new UICommandBuilder();
        builder.set("#ProgressBar.Value", percentFilled);
        builder.set("#ReiryokuCurrent.Text", String.valueOf((int)current));
        builder.set("#ReiryokuMax.Text", "/" + String.valueOf((int)max));
        this.update(false, builder);
    }

    }

