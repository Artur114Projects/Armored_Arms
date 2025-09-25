package com.artur114.armoredarms.api;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;

/**
 * @see com.artur114.armoredarms.client.core.ArmRenderLayerArmor.DefaultModelOnlyArms
 */
public interface IModelOnlyArms {
    void renderArm(AbstractClientPlayer player, ItemArmor itemArmor, ItemStack stackArmor, EnumHandSide side);
    ModelBiped original();
}
