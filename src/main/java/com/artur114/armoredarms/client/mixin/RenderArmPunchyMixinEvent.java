package com.artur114.armoredarms.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class RenderArmPunchyMixinEvent extends Event {
    private final PlayerModel<?> playerModel;
    private final AbstractClientPlayer player;
    private final MultiBufferSource buffer;
    private final ResourceLocation texture;
    private final PoseStack poseStack;
    private final float partialTicks;
    private final int combinedLight;
    private final HumanoidArm arm;
    private final boolean slim;

    public RenderArmPunchyMixinEvent(PlayerModel playerModel, AbstractClientPlayer player, HumanoidArm arm, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, ResourceLocation texture, boolean slim, float partialTicks) {
        this.playerModel = playerModel;
        this.player = player;
        this.buffer = buffer;
        this.texture = texture;
        this.poseStack = poseStack;
        this.partialTicks = partialTicks;
        this.combinedLight = combinedLight;
        this.arm = arm;
        this.slim = slim;
    }

    public PlayerModel<?> playerModel() {
        return playerModel;
    }

    public AbstractClientPlayer player() {
        return player;
    }

    public MultiBufferSource buffer() {
        return buffer;
    }

    public ResourceLocation texture() {
        return texture;
    }

    public PoseStack poseStack() {
        return poseStack;
    }

    public float partialTicks() {
        return partialTicks;
    }

    public int combinedLight() {
        return combinedLight;
    }

    public HumanoidArm arm() {
        return arm;
    }

    public boolean slim() {
        return slim;
    }
}
