package com.artur114.armoredarms.client.modelrender.armor;

import com.artur114.armoredarms.client.modelrender.IArmModelRendererBase;
import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.client.util.MultiModelRenderContext;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.util.Bone;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;

public class ArmModelRendererArmor implements IArmModelRendererBase<ArmModelManagerArmor> {
    protected final MultiModelRenderContext context;
    protected final HumanoidModel<?> hm;
    protected final ModelPart[] arms;

    public ArmModelRendererArmor(MultiModelRenderContext context, HumanoidModel<?> hm) {
        this.arms = new ModelPart[] {hm.leftArm, hm.rightArm};
        this.context = context;
        this.hm = hm;
    }

    @Override
    public void renderArm(ArmModelManagerArmor context, EnumHandSideAA side) {
        this.renderDefault(this.context, context.bone(side), this.arms[side.ordinal()]);
    }
}
