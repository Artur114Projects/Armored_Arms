package com.artur114.armoredarms.client.modelrender.player;

import com.artur114.armoredarms.client.layers.ArmRenderLayerHand;
import com.artur114.armoredarms.client.util.AAItemStack;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.modelrender.IArmModelManager;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderer;
import com.artur114.armoredarms.main.AAConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.ResourceLocation;

public class ArmModelManagerPlayer implements IArmModelManager<ArmModelManagerPlayer, ArmRenderLayerHand> {
    protected boolean deactivated = false;
    public final Minecraft mc = Minecraft.getMinecraft();
    public AAItemStack chestPlate =AAItemStack.EMPTY;
    public ResourceLocation playerSkin;
    public RenderPlayer renderPlayer;
    public boolean shouldRenderWear;

    @Override
    public void update(ArmRenderLayerHand layer) {
        if (this.deactivated) {
            return;
        }
        this.playerSkin = layer.mc.player.getLocationSkin();
        this.shouldRenderWear = !AAConfig.disableArmWear || layer.renderArmWearList.contains(layer.currentChestPlate.location()) || (AAConfig.enableArmWearWithVanillaM && layer.currentChestPlate.isBiped());
    }

    @Override
    public void render(ArmRenderLayerHand layer, IArmModelRenderer<ArmModelManagerPlayer> renderer, EnumHandSideAA side) {
        if (this.deactivated) {
            return;
        }
        this.chestPlate = layer.currentChestPlate;
        this.renderPlayer = layer.renderPlayer;

        renderer.renderArm(this, side);

        this.renderPlayer = null;
        this.chestPlate = null;
    }

    @Override
    public IArmModelRenderer<ArmModelManagerPlayer> cacheRenderer(ArmRenderLayerHand layer, IArmModelRenderContainer<ArmRenderLayerHand, ArmModelManagerPlayer> container) {
        if (this.deactivated) {
            return null;
        }

        this.chestPlate = layer.currentChestPlate;
        this.renderPlayer = layer.renderPlayer;

        IArmModelRenderer<ArmModelManagerPlayer> ret = container.create(this);

        this.renderPlayer = null;
        this.chestPlate = null;

        return ret;
    }

    @Override
    public void deactivate() {
        this.deactivated = true;
    }

    @Override
    public Class<ArmRenderLayerHand> targetLayer() {
        return ArmRenderLayerHand.class;
    }

    @Override
    public Class<ArmModelManagerPlayer> clazz() {
        return ArmModelManagerPlayer.class;
    }

    @Override
    public IPriority priority() {
        return Priority.LOW;
    }
}
