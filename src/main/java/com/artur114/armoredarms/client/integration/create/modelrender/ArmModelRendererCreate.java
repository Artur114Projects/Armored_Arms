package com.artur114.armoredarms.client.integration.create.modelrender;

import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelRendererArmor;
import com.artur114.armoredarms.client.util.IModelRenderContext;
import com.artur114.armoredarms.client.util.MultiModelRenderContext;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class ArmModelRendererCreate extends ArmModelRendererArmor {
    private final ResourceLocation tex = new ResourceLocation("create", "textures/models/armor/netherite_diving_arm.png");

    public ArmModelRendererCreate(MultiModelRenderContext context, HumanoidModel<?> hm) {
        super(context, hm);
    }

    @Override
    public void renderArm(ArmModelManagerArmor context, EnumHandSideAA side) { //TODO: Доделать
        super.renderArm(context, side);
        for (IModelRenderContext contextPart : this.context) {
            AbstractClientPlayer player = context.rawContext.player;
            VertexConsumer vertexconsumer = contextPart.multiBuffer().getBuffer(RenderType.entitySolid(this.tex));
            PlayerRenderer renderer = (PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
            PlayerModel<AbstractClientPlayer> model = renderer.getModel();
            ModelPart armPart = side == EnumHandSideAA.LEFT ? model.leftSleeve : model.rightSleeve;
            armPart.xRot = 0.0F;
            boolean s = armPart.skipDraw;
            boolean v = armPart.visible;
            armPart.skipDraw = false;
            armPart.visible = true;
            armPart.render(contextPart.poseStack(), vertexconsumer, contextPart.packedLight(), OverlayTexture.NO_OVERLAY);
            armPart.skipDraw = s;
            armPart.visible = v;
        }
    }
}
