package com.bleachreiryoku.effects;

import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAttachment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

// Rebuilds player with the modified attachments, copying the original model and adding attachments.

public final class ModelRebuildUtil {

    private ModelRebuildUtil() {}


    public static Model rebuildWithAttachments(
            Model base,
            Collection<ModelAttachment> toAdd,
            Collection<ModelAttachment> toRemove
    ) {
        ModelAttachment[] existing = base.getAttachments();
        Set<ModelAttachment> removeSet = toRemove != null
                ? Set.copyOf(toRemove)   // identity check
                : Set.of();

        // Build new attachment list: keep existing (minus removed ones) then add new ones
        List<ModelAttachment> result = new ArrayList<>(existing.length);
        for (ModelAttachment a : existing) {
            if (!removeSet.contains(a)) result.add(a);
        }
        if (toAdd != null) result.addAll(toAdd);

        return new Model(
                base.getModelAssetId(),
                base.getScale(),
                base.getRandomAttachmentIds(),
                result.toArray(ModelAttachment[]::new),
                base.getBoundingBox(),
                base.getModel(),
                base.getTexture(),
                base.getGradientSet(),
                base.getGradientId(),
                base.getEyeHeight(),
                base.getCrouchOffset(),
                base.getSittingOffset(),
                base.getSleepingOffset(),
                base.getAnimationSetMap(),
                base.getCamera(),
                base.getLight(),
                base.getParticles(),
                base.getTrails(),
                base.getPhysicsValues(),
                base.getDetailBoxes(),
                base.getPhobia(),
                base.getPhobiaModelAssetId()
        );
    }
}
