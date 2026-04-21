package com.artur114.armoredarms.client.integration.brimm.modelrender;

import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;

public class ArmModelRendererBrimm implements IArmModelRenderer<ArmModelManagerArmor> {
    @Override
    public void renderArm(ArmModelManagerArmor context, EnumHandSideAA side) {
        // Noop, Because there is nothing to draw
    }
}
