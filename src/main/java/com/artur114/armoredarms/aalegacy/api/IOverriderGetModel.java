package com.artur114.armoredarms.aalegacy.api;

import com.artur114.armoredarms.aalegacy.client.core.ArmRenderLayerArmor;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

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
    IModelOnlyArms getModel(AbstractClientPlayer player, ArmorItem itemArmor, ItemStack stack);
}
