package com.artur114.armoredarms.api;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.List;

/**
 * Overrider responsible for obtaining armor textures
 * @see com.artur114.armoredarms.client.core.ArmRenderLayerArmor.DefaultTextureGetter
 */
public interface IOverriderGetTex extends IOverrider {
    /**
     * @param player Main Client-side player.
     * @param chestPlate Stack of equipped armor.
     * @param itemArmor Item of equipped armor.
     * @param type Texture type.
     * @return Texture belonging to the passed ItemStack.
     */
    ResourceLocation getTexture(AbstractClientPlayer player, ItemStack chestPlate, ItemArmor itemArmor, EnumTexType type);

    enum EnumTexType {
        NULL, OVERLAY
    }
}
