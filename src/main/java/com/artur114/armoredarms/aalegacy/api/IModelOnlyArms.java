package com.artur114.armoredarms.aalegacy.api;

import com.artur114.armoredarms.aalegacy.core.ArmRenderLayerArmor;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;

/**
 * @see ArmRenderLayerArmor.DefaultModelOnlyArms
 */
public interface IModelOnlyArms {
    /**
     * @param player Main Client-side player.
     * @param itemArmor Item of equipped armor.
     * @param stackArmor Stack of equipped armor.
     * @param side Side of the arm to draw.
     */
    void renderArm(AbstractClientPlayer player, ItemArmor itemArmor, ItemStack stackArmor, EnumHandSide side);

    /**
     * @return Original model.
     */
    ModelBiped original();
}
