package com.artur114.armoredarms.api;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Overrider responsible for rendering
 * @see com.artur114.armoredarms.client.core.ArmRenderLayerArmor.DefaultRender
 */
public interface IOverriderRender extends IOverrider {
    /**
     * @param arms The IModelOnlyArms model to render.
     * @param tex The texture to render.
     * @param handSide The side of the hand.
     * @param stackArmor The stack of equipped armor.
     * @param itemArmor The item of equipped armor.
     * @param type The rendering type.
     */
    void render(@Nullable IModelOnlyArms arms, @Nullable ResourceLocation tex, PoseStack pPoseStack, MultiBufferSource pBuffer, HumanoidArm handSide, ItemStack stackArmor, ArmorItem itemArmor, EnumRenderType type, int packedLight);

    enum EnumRenderType {
        ARMOR, ARMOR_OVERLAY, ARMOR_ENCHANT, ARMOR_TRIM
    }
}
