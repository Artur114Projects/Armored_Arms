package com.artur114.armoredarms.client.modelrender.player;

import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.client.util.IModelRenderContext;
import com.artur114.armoredarms.client.util.MultiModelRenderContext;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.main.AAConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.PlayerModelPart;

public class ArmModelRendererPlayer implements IArmModelRenderer<ArmModelManagerPlayer> {
    protected final Minecraft mc = Minecraft.getInstance();
    protected final MultiModelRenderContext context;

    public ArmModelRendererPlayer(MultiModelRenderContext context) {
        this.context = context;
    }

    @Override
    public void renderArm(ArmModelManagerPlayer context, EnumHandSideAA side) {
        if (this.mc.player == null) return;
        PlayerModel<AbstractClientPlayer> model = context.model;
        ModelPart armWear;
        ModelPart arm = switch (side) {
            case RIGHT -> {
                armWear = model.rightSleeve;
                yield model.rightArm;
            }
            case LEFT -> {
                armWear = model.leftSleeve;
                yield model.leftArm;
            }
        };

        this.setModelProperties(model, this.mc.player);
        model.attackTime = 0.0F;
        model.crouching = false;
        model.swimAmount = 0.0F;
        model.setupAnim(this.mc.player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);


        boolean wear = false;

        for (IModelRenderContext part : this.context) {
            ModelPart modelPart = arm;

            if (wear) {
                modelPart = armWear; wear = false;
            } else {
                wear = true;
            }

            if (modelPart == armWear && !context.shouldRenderWear) {
                continue;
            }

            modelPart.xRot = 0.0F;
            if (AAConfig.useForcedRotations) AAUtils.setForcedRotations(modelPart, side);
            part.renderPart(modelPart);
        }
    }

    private void setModelProperties(PlayerModel<AbstractClientPlayer> playermodel, AbstractClientPlayer pClientPlayer) {
        if (pClientPlayer.isSpectator()) {
            playermodel.setAllVisible(false);
            playermodel.head.visible = true;
            playermodel.hat.visible = true;
        } else {
            playermodel.setAllVisible(true);
            playermodel.hat.visible = pClientPlayer.isModelPartShown(PlayerModelPart.HAT);
            playermodel.jacket.visible = pClientPlayer.isModelPartShown(PlayerModelPart.JACKET);
            playermodel.leftPants.visible = pClientPlayer.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
            playermodel.rightPants.visible = pClientPlayer.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
            playermodel.leftSleeve.visible = pClientPlayer.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
            playermodel.rightSleeve.visible = pClientPlayer.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
            playermodel.crouching = pClientPlayer.isCrouching();
        }
    }
}
