package com.artur114.armoredarms.client.integration.smartmoving.modelrender;

import com.artur114.armoredarms.client.modelrender.player.ArmModelManagerPlayer;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import net.minecraft.client.model.ModelBiped;
import net.smart.render.ModelRotationRenderer;

public class ArmModelManagerSmartPlayer extends ArmModelManagerPlayer {
    @Override
    public void prepareModel(ModelBiped model) {
        model.swingProgress = 0.0F;
        model.isRiding = false;
        model.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, this.mc.thePlayer);

        model.bipedRightArm.rotationPointX = -5.0F;
        model.bipedRightArm.rotationPointY = 2.0F;
        model.bipedRightArm.rotationPointZ = 0.0F;
        model.bipedRightArm.rotateAngleX = 0.0F;
        model.bipedRightArm.rotateAngleY = 0.0F;
        model.bipedRightArm.rotateAngleZ = 0.1F;
        model.bipedRightArm.offsetX = 0.0F;
        model.bipedRightArm.offsetY = 0.0F;
        model.bipedRightArm.offsetZ = 0.0F;
        model.bipedRightArm.showModel = true;
        model.bipedRightArm.isHidden = false;

        ((ModelRotationRenderer) model.bipedRightArm).forceRender = true;

        model.bipedLeftArm.rotationPointX = 5.0F;
        model.bipedLeftArm.rotationPointY = 2.0F;
        model.bipedLeftArm.rotationPointZ = 0.0F;
        model.bipedLeftArm.rotateAngleX = 0.0F;
        model.bipedLeftArm.rotateAngleY = 0.0F;
        model.bipedLeftArm.rotateAngleZ = -0.1F;
        model.bipedLeftArm.offsetX = 0.0F;
        model.bipedLeftArm.offsetY = 0.0F;
        model.bipedLeftArm.offsetZ = 0.0F;
        model.bipedLeftArm.showModel = true;
        model.bipedLeftArm.isHidden = false;

        ((ModelRotationRenderer) model.bipedLeftArm).forceRender = true;

        this.layer.engine().mainBones().updateBones(model.bipedRightArm, model.bipedLeftArm);
    }

    @Override
    public IPriority priority() {
        return Priority.HIGH;
    }
}
