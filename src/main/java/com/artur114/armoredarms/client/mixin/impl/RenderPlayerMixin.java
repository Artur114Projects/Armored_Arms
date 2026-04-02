package com.artur114.armoredarms.client.mixin.impl;

import com.artur114.armoredarms.client.mixin.RenderArmMixinEvent;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderPlayer.class)
public class RenderPlayerMixin {
    @Inject(method = "renderRightArm", at = @At("HEAD"), cancellable = true)
    public void renderRightArm(AbstractClientPlayer clientPlayer, CallbackInfo ci) {
        System.out.println("Hello from RenderPlayerMixin::renderRightArm");
        if (MinecraftForge.EVENT_BUS.post(new RenderArmMixinEvent(EnumHandSideAA.RIGHT))) {
            ci.cancel();
        }
    }

    @Inject(method = "renderLeftArm", at = @At("HEAD"), cancellable = true)
    public void renderLeftArm(AbstractClientPlayer clientPlayer, CallbackInfo ci) {
        System.out.println("Hello from RenderPlayerMixin::renderLeftArm");
        if (MinecraftForge.EVENT_BUS.post(new RenderArmMixinEvent(EnumHandSideAA.LEFT))) {
            ci.cancel();
        }
    }
}
