package com.bleachreiryoku.commands;

import com.bleachreiryoku.playerData.ActiveMeditation;
import com.bleachreiryoku.playerData.playerStats;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

/** Admin testing helper: resets the caller's daily cooldown without granting a reward. */
public class ResetMeditationCommand extends CommandBase {
    public ResetMeditationCommand() {
        super("resetMeditation", "Reset your daily meditation cooldown (admin testing)");
        requirePermission("bleachreiryoku.admin.resetmeditation");
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        Ref<EntityStore> ref = context.senderAsPlayerRef();
        if (ref == null || !ref.isValid()) {
            context.sendMessage(Message.raw("Run this command in-game as the player whose meditation cooldown you want to reset."));
            return;
        }
        Store<EntityStore> store = ref.getStore();
        store.getExternalData().getWorld().execute(() -> {
            if (!ref.isValid() || ref.getStore() != store) return;
            if (store.getComponent(ref, ActiveMeditation.getComponentType()) != null) {
                context.sendMessage(Message.raw("Finish or cancel your current meditation before resetting its cooldown."));
                return;
            }
            playerStats stats = store.getComponent(ref, playerStats.getComponentType());
            if (stats == null) {
                context.sendMessage(Message.raw("Could not retrieve your player stats."));
                return;
            }
            stats.lastMeditationGameTime = 0L;
            context.sendMessage(Message.raw("Meditation cooldown reset. You can meditate again now."));
        });
    }
}
