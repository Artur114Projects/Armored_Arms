package com.artur114.armoredarms.client.integration.azurelib.modelrender;

import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.azure.azurelib.model.AzBone;
import software.bernie.geckolib.cache.object.GeoBone;

public class PSAzBone {
    public EnumHandSideAA side;
    public PoseStack stack;
    public AzBone arm;
}
