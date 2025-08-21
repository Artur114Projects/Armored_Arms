package com.artur114.armoredarms.api.override;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;

/**
 * Overrider responsible for obtaining armor model
 * @see com.artur114.armoredarms.client.RenderArmManager.DefaultModelGetter
 */
public interface IOverriderGetModel extends IOverrider {
    ModelBase getModel(AbstractClientPlayer player, ItemArmor itemArmor, ItemStack stack);
    ModelRenderer getArm(ModelBase mb, EnumHandSide handSide);
}
