package com.artur114.armoredarms.client.integration;

import com.artur114.armoredarms.api.IModelOnlyArms;
import com.artur114.armoredarms.client.util.MiscUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.joml.*;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.*;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.RenderUtils;

import java.lang.Math;
import java.util.Iterator;

public class GeckoModelOnlyArms implements IModelOnlyArms {
    public final ModelPart[] playerArms = MiscUtils.playerArms();
    public final GeoArmorRenderer<?> mg;
    public final String[] arms;


    public GeckoModelOnlyArms(GeoArmorRenderer<?> model) {
        this.mg = model;

        this.arms = new String[] {"armorLeftArm", "armorRightArm"};

        System.out.println("Geckolib is gavno");
    }

    @Override
    public void renderArm(PoseStack pPoseStack, VertexConsumer pBuffer, AbstractClientPlayer player, ArmorItem itemArmor, ItemStack stackArmor, HumanoidArm side, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        this.render(pPoseStack, pBuffer, player, itemArmor, stackArmor, side, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
    }

    @SuppressWarnings("unchecked")
    public <T extends Item & GeoItem> void render(PoseStack pPoseStack, VertexConsumer pBuffer, AbstractClientPlayer player, ArmorItem itemArmor, ItemStack stackArmor, HumanoidArm side, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        T t = (T) itemArmor;
        GeoArmorRenderer<T> model = (GeoArmorRenderer<T>) this.mg;
        BakedGeoModel baked = model.getGeoModel().getBakedModel(model.getGeoModel().getModelResource(t, model));

        MultiBufferSource bufferSource = Minecraft.getInstance().levelRenderer.renderBuffers.bufferSource();
        if (Minecraft.getInstance().levelRenderer.shouldShowEntityOutlines() && Minecraft.getInstance().shouldEntityAppearGlowing(player)) {
            bufferSource = Minecraft.getInstance().levelRenderer.renderBuffers.outlineBufferSource();
        }
        RenderType renderType = model.getRenderType(t, model.getTextureLocation(t), bufferSource, Minecraft.getInstance().getPartialTick());
        pBuffer = ItemRenderer.getArmorFoilBuffer(bufferSource, renderType, false, stackArmor.hasFoil());

        GeoBone arm = baked.getBone(this.arms[side.ordinal()]).get();
        arm.setTrackingMatrices(false);
        this.mg.attackTime = 0.0F;
        this.mg.crouching = false;
        this.mg.swimAmount = 0.0F;
        this.mg.setupAnim(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);

        int delta = MiscUtils.handSideDelta(side);
        arm.setRotX(0.0F);
        arm.setRotY(0.0F);
        arm.setRotZ((float) ((Math.PI + 0.1F) * delta));

        arm.setPosX(-4.0F * (delta * 2) * -1);
        arm.setPosY(2.0F * -1 * 10);
        arm.setPosZ(0.0F);

        arm.setScaleX(1.0F);
        arm.setScaleY(1.0F);
        arm.setScaleZ(1.0F);

        boolean h = arm.isHidden();
        arm.setHidden(false);
        model.renderRecursively(pPoseStack, t, arm, renderType, bufferSource, pBuffer, false, Minecraft.getInstance().getPartialTick(), pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        arm.setHidden(h);
    }

    public void renderGeoBone(PoseStack poseStack, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.pushPose();
        this.translateAndRotate(poseStack, bone);
        this.renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.renderChildBones(poseStack, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.popPose();
    }

    public void translateAndRotate(PoseStack pPoseStack, GeoBone bone) {
        pPoseStack.translate(bone.getPosX() / 16.0F, bone.getPosY() / 16.0F, bone.getPosZ() / 16.0F);
        if (bone.getRotX() != 0.0F || bone.getRotY() != 0.0F || bone.getRotZ() != 0.0F) {
            pPoseStack.mulPose((new Quaternionf()).rotationZYX(bone.getRotZ(), bone.getRotY(), bone.getRotX()));
        }

        if (bone.getScaleX() != 1.0F || bone.getScaleY() != 1.0F || bone.getScaleZ() != 1.0F) {
            pPoseStack.scale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
        }
    }

    public void renderChildBones(PoseStack poseStack, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (!bone.isHidingChildren()) {
            for (GeoBone childBone : bone.getChildBones()) {
                this.renderGeoBone(poseStack, childBone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
            }
        }
    }

    public void renderCubesOfBone(PoseStack poseStack, GeoBone bone, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (!bone.isHidden()) {

            for (GeoCube cube : bone.getCubes()) {
                poseStack.pushPose();
                this.renderCube(poseStack, cube, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                poseStack.popPose();
            }

        }
    }

    public void renderCube(PoseStack poseStack, GeoCube cube, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        RenderUtils.translateToPivotPoint(poseStack, cube);
        RenderUtils.rotateMatrixAroundCube(poseStack, cube);
        RenderUtils.translateAwayFromPivotPoint(poseStack, cube);
        Matrix3f normalisedPoseState = poseStack.last().normal();
        Matrix4f poseState = poseStack.last().pose();
        GeoQuad[] quads = cube.quads();

        for (GeoQuad quad : quads) {
            if (quad != null) {
                Vector3f normal = normalisedPoseState.transform(new Vector3f(quad.normal()));
                this.createVerticesOfQuad(quad, poseState, normal, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            }
        }
    }

    public void createVerticesOfQuad(GeoQuad quad, Matrix4f poseState, Vector3f normal, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        GeoVertex[] vertices = quad.vertices();

        for (GeoVertex vertex : vertices) {
            Vector3f position = vertex.position();
            Vector4f vector4f = poseState.transform(new Vector4f(position.x(), position.y(), position.z(), 1.0F));
            buffer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), red, green, blue, alpha, vertex.texU(), vertex.texV(), packedOverlay, packedLight, normal.x(), normal.y(), normal.z());
        }
    }

    @Override
    public Model original() {
        return this.mg;
    }
}
