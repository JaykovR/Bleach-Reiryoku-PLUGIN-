package com.bleachreiryoku.commands;

import com.bleachreiryoku.hud.ReiryokuBar;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class ToggleHUDCommand extends AbstractPlayerCommand {
    public ToggleHUDCommand(String name, String description){super(name, description);}

    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref,
                           @Nonnull PlayerRef playerRef, @Nonnull World world){
        Player player = commandContext.senderAs((Player.class));
        player.getWorldMapTracker().tick(0);

        CompletableFuture.runAsync(() -> {
            if(player.getHudManager().getCustomHud() == null){
                player.getHudManager().setCustomHud(playerRef, new ReiryokuBar(playerRef));
                playerRef.sendMessage(Message.raw("HUD Shown"));
            }
            else{
                player.getHudManager().resetHud(playerRef);
                playerRef.sendMessage(Message.raw("HUD Hidden"));
            }
        }, world);
    }
}
