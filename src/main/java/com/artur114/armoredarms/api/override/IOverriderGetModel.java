package com.artur114.armoredarms.api.override;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

/**
 * Overrider responsible for obtaining armor model
 * @see com.artur114.armoredarms.client.RenderArmManager.DefaultModelGetter
 */
public interface IOverriderGetModel extends IOverrider {
    ModelBiped getModel(AbstractClientPlayer player, ItemArmor itemArmor, ItemStack stack);
}
