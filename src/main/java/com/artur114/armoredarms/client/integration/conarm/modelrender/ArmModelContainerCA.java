package com.artur114.armoredarms.client.integration.conarm.modelrender;

import c4.conarm.client.models.ModelConstructsArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelContainerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelRendererArmor;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import net.minecraft.client.model.ModelBiped;

public class ArmModelContainerCA extends ArmModelContainerArmor {
    public ArmModelContainerCA() {
        super(ArmModelRendererArmor.class);
    }

    @Override
    public IArmModelRenderer<ArmModelManagerArmor> create(ArmModelManagerArmor manager) {
        ModelBiped mb = manager.model;
        if (mb instanceof ModelConstructsArmor && mb.bipedRightArm != ((ModelConstructsArmor) mb).armRightAnchor) {
            mb.bipedRightArm = ((ModelConstructsArmor) mb).armRightAnchor;
            mb.bipedLeftArm = ((ModelConstructsArmor) mb).armLeftAnchor;
            mb.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F / 16.0F, manager.player);
        }
        return super.create(manager);
    }
}
