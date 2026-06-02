package com.artur114.armoredarms.client.integration.geckolib.modelrender;

import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.client.modelrender.context.ModelRenderContextOverlay;
import com.artur114.armoredarms.client.util.IModelRenderContext;
import com.artur114.armoredarms.client.util.ItemStackAA;
import com.artur114.armoredarms.client.util.MultiModelRenderContext;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.core.util.Bone;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class ArmModelRendererGecko implements IArmModelRenderer<ArmModelManagerArmor> {
    private final PSGeoBone geoBone = new PSGeoBone();
    public final MultiModelRenderContext context;
    public final GeoArmorRenderer<?> mg;
    public final String[] arms;

    public ArmModelRendererGecko(MultiModelRenderContext context, GeoArmorRenderer<?> model) {
        this.context = context;
        this.mg = model;
        this.arms = new String[] {"armorLeftArm", "armorRightArm"};
    }


    @Override
    public void renderArm(ArmModelManagerArmor context, EnumHandSideAA side) {
        for (IModelRenderContext contextPart : this.context) {
            if (contextPart instanceof ModelRenderContextOverlay) continue;
            this.render(context, contextPart.poseStack(), contextPart.multiBuffer(), context.chestPlate, side, contextPart.packedLight(), contextPart.packedOverlay());
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Item & GeoItem> void render(ArmModelManagerArmor manager, PoseStack pPoseStack, MultiBufferSource multiBuffer, ItemStackAA stack, EnumHandSideAA side, int pPackedLight, int pPackedOverlay) {
        if (stack.isEmpty()) return;
        T t = (T) stack.item();
        GeoArmorRenderer<T> model = (GeoArmorRenderer<T>) this.mg;
        BakedGeoModel baked = model.getGeoModel().getBakedModel(model.getGeoModel().getModelResource(t, model));
        RenderType renderType = model.getRenderType(t, model.getTextureLocation(t), multiBuffer, Minecraft.getInstance().getPartialTick());
        VertexConsumer buffer = ItemRenderer.getArmorFoilBuffer(multiBuffer, renderType, false, stack.stack().hasFoil());

        Bone bone = manager.bone(side);
        GeoBone arm = baked.getBone(this.arms[side.ordinal()]).get();
        this.mg.attackTime = 0.0F;
        this.mg.crouching = false;
        this.mg.swimAmount = 0.0F;
        this.geoBone.arm = arm;
        this.geoBone.side = side;
        this.geoBone.stack = pPoseStack;

        boolean h = arm.isHidden();
        arm.setHidden(false);

        pPoseStack.pushPose();
        pPoseStack.translate(0.0F, 1.5F, 0.0F);
        pPoseStack.scale(-1.0F, -1.0F, 1.0F);
        bone.injectTo(this.geoBone);
        model.renderRecursively(pPoseStack, t, arm, renderType, multiBuffer, buffer, false, Minecraft.getInstance().getPartialTick(), pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        pPoseStack.popPose();

        arm.setHidden(h);
    }
}
