package com.artur114.armoredarms.aalegacy.api;

import com.artur114.armoredarms.aalegacy.client.core.ArmRenderLayerArmor;
import com.artur114.armoredarms.aalegacy.client.util.EnumHandSide;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

/**
 * @see ArmRenderLayerArmor.DefaultModelOnlyArms
 */
public interface IModelOnlyArms {
    /**
     * @param player Main Client-side player.
     * @param itemArmor Item of equipped armor.
     * @param stackArmor Stack of equipped armor.
     */
    void renderArm(AbstractClientPlayer player, EnumHandSide side, ItemArmor itemArmor, ItemStack stackArmor);

    /**
     * @return Original model.
     */
    ModelBiped original();
}
