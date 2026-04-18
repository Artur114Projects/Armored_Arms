package com.artur114.armoredarms.client.integration.lotr.modelrender;

import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelRendererArmor;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.util.IMultiTexture;
import com.artur114.armoredarms.core.util.Reflector;
import com.artur114.armoredarms.main.AAConfig;
import net.minecraft.client.model.ModelBiped;

import java.lang.reflect.Constructor;

public class ArmModelRendererLotr extends ArmModelRendererArmor {
    public ArmModelRendererLotr(ModelBiped mb, IMultiTexture texture) throws ClassNotFoundException {
        super(createModel(mb), texture);
    }

    @Override
    public void renderArm(ArmModelManagerArmor context, EnumHandSideAA side) {
        super.renderArm(context, side);
    }


    private static ModelBiped createModel(ModelBiped mb) {
        try {
            Class<?> clazz = Class.forName("lotr.client.model.LOTRModelSwanChestplate");
            Constructor<?> constructor = clazz.getConstructor(float.class);
            return (ModelBiped) constructor.newInstance(((float) AAConfig.vanillaArmorModelSize));
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        return mb;
    }
}
