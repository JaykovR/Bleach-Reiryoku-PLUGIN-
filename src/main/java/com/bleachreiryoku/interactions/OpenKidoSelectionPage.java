package com.bleachreiryoku.interactions;

import com.bleachreiryoku.ui.KidoSelectionPage;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

    // Opens the Kido Selection UI.
public class OpenKidoSelectionPage extends SimpleInstantInteraction {

    public static final BuilderCodec<OpenKidoSelectionPage> CODEC =
            BuilderCodec.builder(OpenKidoSelectionPage.class, OpenKidoSelectionPage::new, SimpleInteraction.CODEC)
                    .build();

    @Override
    protected void firstRun(@NonNullDecl InteractionType type,
                            @NonNullDecl InteractionContext context,
                            @NonNullDecl CooldownHandler cooldownHandler) {

        Ref<EntityStore> ref = context.getOwningEntity();
        Store<EntityStore> store = ref.getStore();

        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef == null) {
            context.getState().state = InteractionState.Failed;
            return;
        }

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            context.getState().state = InteractionState.Failed;
            return;
        }

        player.getPageManager().openCustomPage(ref, store, new KidoSelectionPage(playerRef));
        context.getState().state = InteractionState.Finished;
    }
}
