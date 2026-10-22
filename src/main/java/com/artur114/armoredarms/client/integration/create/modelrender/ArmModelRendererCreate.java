package com.artur114.armoredarms.client.integration.create.modelrender;

import com.artur114.armoredarms.client.modelrender.IArmModelRendererBase;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelRendererArmor;
import com.artur114.armoredarms.client.modelrender.context.ModelRenderContextGlint;
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
import net.minecraft.world.entity.HumanoidArm;

public class ArmModelRendererCreate implements IArmModelRendererBase<ArmModelManagerArmor> {
    private final ResourceLocation tex = ResourceLocation.fromNamespaceAndPath("create", "textures/models/armor/netherite_diving_arm.png");
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
        AbstractClientPlayer player = context.rawContext.player;
        Bone bone = context.bone(side);
        this.renderDefault(this.context, bone, this.arms[side.ordinal()]);

        PlayerRenderer renderer = (PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
        PlayerModel<AbstractClientPlayer> model = renderer.getModel();
        model.attackTime = 0.0F;
        model.crouching = false;
        model.swimAmount = 0.0F;
        model.setupAnim(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        ModelPart armPart = side == EnumHandSideAA.LEFT ? model.leftSleeve : model.rightSleeve;

        for (IModelRenderContext contextPart : this.context) {
            VertexConsumer vertexconsumer;
            if (contextPart instanceof ModelRenderContextGlint) {
                if (context.layer.chestPlate.stack().hasFoil()) {
                    vertexconsumer = contextPart.multiBuffer().getBuffer(RenderType.entityGlint());
                } else {
                    continue;
                }
            } else {
                vertexconsumer = contextPart.multiBuffer().getBuffer(RenderType.entitySolid(this.tex));
            }
            bone.injectTo(armPart);
            boolean s = armPart.skipDraw;
            boolean v = armPart.visible;
            armPart.skipDraw = false;
            armPart.visible = true;
            armPart.xRot = 0.0F;
            armPart.render(contextPart.poseStack(), vertexconsumer, contextPart.packedLight(), OverlayTexture.NO_OVERLAY);
            armPart.skipDraw = s;
            armPart.visible = v;
        }
    }
}
