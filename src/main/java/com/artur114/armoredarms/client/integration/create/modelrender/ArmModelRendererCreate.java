package com.artur114.armoredarms.client.integration.create.modelrender;

import com.artur114.armoredarms.client.modelrender.IArmModelRendererBase;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelRendererArmor;
import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.client.util.IModelRenderContext;
import com.artur114.armoredarms.client.util.MultiModelRenderContext;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.util.Bone;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class ArmModelRendererCreate implements IArmModelRendererBase<ArmModelManagerArmor> {
    private final ResourceLocation tex = new ResourceLocation("create", "textures/models/armor/netherite_diving_arm.png");
    protected final MultiModelRenderContext context;
    protected HumanoidModel<?> hm;
    protected ModelPart[] arms;


    public ArmModelRendererCreate(MultiModelRenderContext context, HumanoidModel<?> hm) {
        this.context = context;
    }

    @Override
    public void renderArm(ArmModelManagerArmor context, EnumHandSideAA side) {
        if (this.hm == null) {
            this.hm = context.layer.engine.actualHumanoidModel(0.4F);
            this.arms = new ModelPart[] {this.hm.leftArm, this.hm.rightArm};
        }
        Bone bone = context.bone(side);
        this.renderDefault(this.context, bone, this.arms[side.ordinal()]);
        for (IModelRenderContext contextPart : this.context) {
            AbstractClientPlayer player = context.rawContext.player;
            VertexConsumer vertexconsumer = contextPart.multiBuffer().getBuffer(RenderType.entitySolid(this.tex));
            PlayerRenderer renderer = (PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
            PlayerModel<AbstractClientPlayer> model = renderer.getModel();
            ModelPart armPart = side == EnumHandSideAA.LEFT ? model.leftSleeve : model.rightSleeve;
            bone.injectTo(armPart);
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
