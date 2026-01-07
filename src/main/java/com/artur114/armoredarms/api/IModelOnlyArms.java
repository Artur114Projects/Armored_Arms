package com.artur114.armoredarms.api;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

/**
 * @see com.artur114.armoredarms.client.core.ArmRenderLayerArmor.DefaultModelOnlyArms
 */
public interface IModelOnlyArms {
    /**
     * @param player Main Client-side player.
     * @param itemArmor Item of equipped armor.
     * @param stackArmor Stack of equipped armor.
     */
    void renderArm(AbstractClientPlayer player, ItemArmor itemArmor, ItemStack stackArmor);

    /**
     * @return Original model.
     */
    ModelBiped original();
}
