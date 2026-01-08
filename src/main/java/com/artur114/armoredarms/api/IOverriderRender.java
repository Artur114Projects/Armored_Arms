package com.artur114.armoredarms.api;

import com.artur114.armoredarms.client.util.EnumHandSide;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Overrider responsible for rendering
 * @see com.artur114.armoredarms.client.core.ArmRenderLayerArmor.DefaultRender
 */
public interface IOverriderRender extends IOverrider {
    /**
     * @param arms The IModelOnlyArms model to render.
     * @param tex The texture to render.
     * @param stackArmor The stack of equipped armor.
     * @param itemArmor The item of equipped armor.
     * @param type The rendering type.
     */
    void render(@Nullable IModelOnlyArms arms, EnumHandSide side, @Nullable ResourceLocation tex, ItemStack stackArmor, ItemArmor itemArmor, EnumRenderType type);

    enum EnumRenderType {
        ARMOR, ARMOR_OVERLAY, ARMOR_ENCHANT
    }
}
