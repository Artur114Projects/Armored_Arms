package com.artur114.armoredarms.aalegacy.api;

import com.artur114.armoredarms.aalegacy.core.ArmRenderLayerArmor;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

/**
 * Overrider responsible for obtaining armor model
 * @see ArmRenderLayerArmor.DefaultModelGetter
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
