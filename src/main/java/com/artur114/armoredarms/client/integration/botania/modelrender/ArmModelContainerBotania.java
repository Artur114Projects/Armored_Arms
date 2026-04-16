package com.artur114.armoredarms.client.integration.botania.modelrender;

import com.artur114.armoredarms.client.layers.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelRendererArmor;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.core.util.Reflector;
import net.minecraft.client.model.ModelBiped;

public class ArmModelContainerBotania implements IArmModelRenderContainer<ArmRenderLayerArmor, ArmModelManagerArmor> {
    private final IPriority priority;
    private final String rightArm;
    private final String leftArm;

    public ArmModelContainerBotania(IPriority priority, String rightArm, String leftArm) {
        this.priority = priority;
        this.rightArm = rightArm;
        this.leftArm = leftArm;
    }

    public ArmModelContainerBotania() {
        this.priority = Priority.NORMAL;
        this.rightArm = "armR";
        this.leftArm = "armL";
    }

    @Override
    public IArmModelRenderer<ArmModelManagerArmor> create(ArmModelManagerArmor manager) {
        ModelBiped mb = manager.model;
        return new ArmModelRendererArmor(mb, manager.texture, Reflector.getPrivateField(mb, this.rightArm), Reflector.getPrivateField(mb, this.leftArm));
    }

    @Override
    public boolean needWork(ArmModelManagerArmor manager) {
        return false;
    }

    @Override
    public Class<ArmModelManagerArmor> targetManager() {
        return ArmModelManagerArmor.class;
    }

    @Override
    public Class<ArmRenderLayerArmor> targetLayer() {
        return ArmRenderLayerArmor.class;
    }

    @Override
    public IPriority priority() {
        return this.priority;
    }
}
