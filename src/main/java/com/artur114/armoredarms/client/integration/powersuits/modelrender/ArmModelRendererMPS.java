package com.artur114.armoredarms.client.integration.powersuits.modelrender;

import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelRendererArmor;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.util.IMultiTexture;
import net.machinemuse.powersuits.client.model.item.armor.IArmorModel;
import net.machinemuse.powersuits.common.utils.nbt.MPSNBTUtils;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.inventory.EntityEquipmentSlot;

public class ArmModelRendererMPS extends ArmModelRendererArmor {
    public ArmModelRendererMPS(ModelBiped mb, IMultiTexture texture) {
        super(mb, texture);
    }

    @Override
    public void renderArm(ArmModelManagerArmor context, EnumHandSideAA side) {
        if (!context.stack.isEmpty() && !context.stack.stack().isEmpty()) {
            ((IArmorModel) this.mb).setRenderSpec(MPSNBTUtils.getMuseRenderTag(context.stack.stack(), EntityEquipmentSlot.CHEST));
            ((IArmorModel) this.mb).setVisibleSection(EntityEquipmentSlot.CHEST);
        }
        super.renderArm(context, side);
    }
}
