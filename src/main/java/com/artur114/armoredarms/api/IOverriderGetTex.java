package com.artur114.armoredarms.api;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.List;

/**
 * Overrider responsible for obtaining armor textures
 * @see com.artur114.armoredarms.client.core.ArmRenderLayerArmor.DefaultTextureGetter
 */
public interface IOverriderGetTex extends IOverrider {

    ResourceLocation getTexture(AbstractClientPlayer player, LayerBipedArmor armorLayer, List<LayerRenderer<AbstractClientPlayer>> layerRenderers, ItemStack chestPlate, ItemArmor itemArmor, EnumTexType type);

    enum EnumTexType {
        NULL, OVERLAY;
    }
}
