package com.artur114.armoredarms.api;

import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Overrider responsible for rendering
 * @see com.artur114.armoredarms.client.core.ArmRenderLayerArmor.DefaultRender
 */
public interface IOverriderRender extends IOverrider {
    void render(IModelOnlyArms arms, @Nullable ResourceLocation tex, EnumHandSide handSide, ItemStack chestPlate, ItemArmor itemArmor, EnumRenderType type);

    enum EnumRenderType {
        ARMOR, ARMOR_OVERLAY, ARMOR_ENCHANT
    }
}
