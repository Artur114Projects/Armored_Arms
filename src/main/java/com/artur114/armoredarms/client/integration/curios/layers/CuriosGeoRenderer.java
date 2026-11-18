package com.artur114.armoredarms.client.integration.curios.layers;

import com.artur114.armoredarms.client.engines.AbstractRenderEngineForge;
import com.artur114.armoredarms.client.integration.geckolib.modelrender.PSGeoBone;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

final class CuriosGeoRenderer {
    private CuriosGeoRenderer() {}

    static boolean isGeoModel(Model model) {
        return model instanceof GeoArmorRenderer<?>;
    }

    static void render(AbstractRenderEngineForge<?, ?> engine, Model rawModel, PoseStack matrixStack, EnumHandSideAA side, ItemStack stack) {
        renderGeo(engine, (GeoArmorRenderer<?>) rawModel, matrixStack, side, stack);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Item & GeoItem> void renderGeo(AbstractRenderEngineForge<?, ?> engine, GeoArmorRenderer<?> rawModel, PoseStack matrixStack, EnumHandSideAA side, ItemStack stack) {
        GeoArmorRenderer<T> model = (GeoArmorRenderer<T>) rawModel;
        T item = (T) stack.getItem();

        GeoBone arm = side.sided(model.getRightArmBone(), model.getLeftArmBone());
        MultiBufferSource renderTypeBuffer = engine.renderContext.multiBufferSource;

        RenderType renderType = model.renderType(model.getTextureLocation(item));
        VertexConsumer vertexConsumer;
        if (stack.hasFoil()) {
            vertexConsumer = VertexMultiConsumer.create(renderTypeBuffer.getBuffer(RenderType.entityGlint()), renderTypeBuffer.getBuffer(renderType));
        } else {
            vertexConsumer = renderTypeBuffer.getBuffer(renderType);
        }

        PSGeoBone bone = new PSGeoBone();
        bone.stack = matrixStack;
        bone.side = side;
        bone.arm = arm;

        engine.mainBones().bySide(side).injectTo(bone);
        model.renderRecursively(matrixStack, item, arm, renderType, renderTypeBuffer, vertexConsumer, false, Minecraft.getInstance().getPartialTick(), engine.renderContext.packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
