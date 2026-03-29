package com.artur114.armoredarms.client.mixin;

import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class RenderArmMixinEvent extends Event {
    private final MultiBufferSource multiBufferSource;
    private final AbstractClientPlayer player;
    private final PoseStack poseStack;
    private final int packedLight;
    private final EnumHandSideAA arm;

    public RenderArmMixinEvent(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, AbstractClientPlayer player, EnumHandSideAA arm) {
        this.multiBufferSource = multiBufferSource;
        this.packedLight = packedLight;
        this.poseStack = poseStack;
        this.player = player;
        this.arm = arm;
    }

    public EnumHandSideAA getArm() {
        return arm;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public MultiBufferSource getMultiBufferSource() {
        return multiBufferSource;
    }

    public int getPackedLight() {
        return packedLight;
    }

    public AbstractClientPlayer getPlayer() {
        return player;
    }
}
