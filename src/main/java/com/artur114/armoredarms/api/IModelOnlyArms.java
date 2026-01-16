package com.artur114.armoredarms.api;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

/**
 * @see com.artur114.armoredarms.client.core.ArmRenderLayerArmor.DefaultModelOnlyArms
 */
public interface IModelOnlyArms {
    /**
     * @param player Main Client-side player.
     * @param itemArmor Item of equipped armor.
     * @param stackArmor Stack of equipped armor.
     * @param side Side of the arm to draw.
     */
    void renderArm(PoseStack pPoseStack, VertexConsumer pBuffer, AbstractClientPlayer player, ArmorItem itemArmor, ItemStack stackArmor, HumanoidArm side, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha);

    /**
     * @return Original model.
     */
    Model original();
}
