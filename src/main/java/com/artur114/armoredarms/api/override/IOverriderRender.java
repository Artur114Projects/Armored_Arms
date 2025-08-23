package com.artur114.armoredarms.api.override;

import net.minecraft.client.model.ModelBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Overrider responsible for rendering
 * @see com.artur114.armoredarms.client.RenderArmManager.DefaultRender
 */
public interface IOverriderRender extends IOverrider {
    void render(ModelBase model, @Nullable IBoneThing hand, @Nullable ResourceLocation tex, EnumHandSide handSide, ItemStack chestPlate, ItemArmor itemArmor, EnumRenderType type);

    enum EnumRenderType {
        ARM, ARM_WEAR, ARMOR, ARMOR_OVERLAY, ARMOR_ENCHANT
    }
}
