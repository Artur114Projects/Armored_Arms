package com.artur114.armoredarms.client.integration.smartmoving.modelrender;

import com.artur114.armoredarms.client.modelrender.player.ArmModelManagerPlayer;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import net.minecraft.client.model.ModelBiped;

public class ArmModelManagerSmartPlayer extends ArmModelManagerPlayer {
    @Override
    public void prepareModel(ModelBiped model) {
        model.swingProgress = 0.0F;
        model.isRiding = false;
        model.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, this.mc.thePlayer);

        model.bipedRightArm.rotationPointX = -5.0F;
        model.bipedRightArm.rotationPointY = 2.0F;
        model.bipedRightArm.rotationPointZ = 0.0F;

        model.bipedLeftArm.rotationPointX = 5.0F;
        model.bipedLeftArm.rotationPointY = 2.0F;
        model.bipedLeftArm.rotationPointZ = 0.0F;

        this.layer.engine().mainBones().updateBones(model.bipedRightArm, model.bipedLeftArm);
    }

    @Override
    public IPriority priority() {
        return Priority.HIGH;
    }
}
