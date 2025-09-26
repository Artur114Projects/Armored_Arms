package com.artur114.armoredarms.api;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

/**
 * Overrider responsible for obtaining armor model
 * @see com.artur114.armoredarms.client.core.ArmRenderLayerArmor.DefaultModelGetter
 */
public interface IOverriderGetModel extends IOverrider {
    /**
     * @param player Main Client-side player.
     * @param itemArmor Equipped armor item.
     * @param stack Stack of equipped armor.
     * @return Model belonging to the passed ItemStack, wrapped in an IModelOnlyArms implementation.
     */
    IModelOnlyArms getModel(AbstractClientPlayer player, ItemArmor itemArmor, ItemStack stack);
}
