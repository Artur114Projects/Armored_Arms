package com.artur114.armoredarms.api;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

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
    ResourceLocation getTexture(AbstractClientPlayer player, ItemStack chestPlate, ArmorItem itemArmor, EnumTexType type);

    enum EnumTexType {
        NULL, OVERLAY;
    }
}
