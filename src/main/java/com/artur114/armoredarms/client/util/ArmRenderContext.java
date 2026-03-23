package com.artur114.armoredarms.client.util;

import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.HumanoidArm;

public class ArmRenderContext {
    public MultiBufferSource multiBufferSource;
    public AbstractClientPlayer player;
    public PoseStack poseStack;
    public EnumHandSideAA arm;
    public int packedLight;
}
