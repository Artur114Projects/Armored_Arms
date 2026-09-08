package com.artur114.armoredarms.client.integration.dragonblockc.modelrender;

import com.artur114.armoredarms.client.modelrender.armor.ArmModelRendererArmor;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.util.Bone;
import com.artur114.armoredarms.core.util.IMultiTexture;
import com.artur114.armoredarms.core.util.Reflector;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;

public class ArmModelRendererDragonShitC extends ArmModelRendererArmor {
    public ArmModelRendererDragonShitC(ModelBiped mb, IMultiTexture texture) {
        super(mb, texture, Reflector.getPrivateField(mb, "Brightarm"), Reflector.getPrivateField(mb, "Bleftarm"));
    }

    @Override
    public void defaultRenderModel(Bone bone, ModelRenderer arm) {
        boolean h = arm.isHidden;
        boolean s = arm.showModel;
        arm.isHidden = false;
        arm.showModel = true;
        arm.render(1.0F / 16.0F);
        arm.isHidden = h;
        arm.showModel = s;
    }
}
