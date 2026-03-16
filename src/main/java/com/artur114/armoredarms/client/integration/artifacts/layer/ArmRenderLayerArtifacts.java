package com.artur114.armoredarms.client.integration.artifacts.layer;

import artifacts.common.init.ModItems;
import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import com.artur114.armoredarms.client.engines.AbstractRenderEngineForge;
import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;
import com.artur114.armoredarms.core.util.IAAModContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class ArmRenderLayerArtifacts implements IArmRenderLayer<AbstractRenderEngineForge<? ,?>> {
    private ResourceLocation feralClawsTextures;
    private ResourceLocation powerGloveTextures;
    private ResourceLocation mechanicalGloveTextures;
    private ResourceLocation fireGauntletTextures;
    private ResourceLocation fireGauntletOverlayTextures;
    private ResourceLocation pocketPistonTextures;
    private RenderPlayer renderPlayer;
    private ModelPlayer defaultModel;
    private boolean deactivate = false;
    private boolean needRender = false;

    @Override
    public void update(AbstractRenderEngineForge<? ,?> engine) {
        if (this.deactivate) {
            return;
        }
        AbstractClientPlayer player = engine.mc.player;
        this.needRender = this.setTextures(player, EnumHandSideAA.RIGHT, false, true) || this.setTextures(player, EnumHandSideAA.LEFT, false, true);
    }

    @Override
    public void render(AbstractRenderEngineForge<? ,?> engine, EnumHandSideAA handSide) {
        if (this.deactivate) {
            return;
        }
        AbstractClientPlayer player = engine.mc.player;
        float lastLightmapX = OpenGlHelper.lastBrightnessX;
        float lastLightmapY = OpenGlHelper.lastBrightnessY;
        int light = 15728880;
        int lightmapX = light % 65536;
        int lightmapY = light / 65536;
        this.renderArm(handSide, player, false);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)lightmapX, (float)lightmapY);
        GlStateManager.disableLighting();
        this.renderArm(handSide, player, true);
        GlStateManager.enableLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastLightmapX, lastLightmapY);
    }

    @Override
    public boolean needRender(AbstractRenderEngineForge<? ,?> engine, boolean renderEngineState) {
        return this.needRender;
    }

    @Override
    public Class<AbstractRenderEngineForge<?, ?>> targetEngine() {
        return AbstractRenderEngineForge.clazz();
    }

    @Override
    public void init(AbstractRenderEngineForge<? ,?> engine, IAAModContainer mod) {
        this.renderPlayer = (RenderPlayer) Minecraft.getMinecraft().getRenderManager().<AbstractClientPlayer>getEntityRenderObject(engine.mc.player);
        boolean smallArms = engine.mc.player.getSkinType().equals("slim");
        this.defaultModel = new ModelPlayer(0.26F, smallArms);
        this.feralClawsTextures = new ResourceLocation("artifacts", "textures/entity/layer/feral_claws_" + (smallArms ? "slim" : "normal") + ".png");
        this.powerGloveTextures = new ResourceLocation("artifacts", "textures/entity/layer/power_glove_" + (smallArms ? "slim" : "normal") + ".png");
        this.mechanicalGloveTextures = new ResourceLocation("artifacts", "textures/entity/layer/mechanical_glove_" + (smallArms ? "slim" : "normal") + ".png");
        this.fireGauntletTextures = new ResourceLocation("artifacts", "textures/entity/layer/fire_gauntlet_" + (smallArms ? "slim" : "normal") + ".png");
        this.fireGauntletOverlayTextures = new ResourceLocation("artifacts", "textures/entity/layer/fire_gauntlet_overlay_" + (smallArms ? "slim" : "normal") + ".png");
        this.pocketPistonTextures = new ResourceLocation("artifacts", "textures/entity/layer/pocket_piston_" + (smallArms ? "slim" : "normal") + ".png");
    }

    private void renderArm(EnumHandSideAA hand, EntityPlayer player, boolean overlay) {
        if (this.setTextures(player, hand, overlay, false)) {
            ModelRenderer playerArm = AAUtils.handFromModelBiped(this.renderPlayer.getMainModel(), hand);

            this.render(hand, AAUtils.handFromModelPlayer(this.defaultModel, hand, false), playerArm);
            this.render(hand, AAUtils.handFromModelPlayer(this.defaultModel, hand, true), playerArm);
        }
    }

    private void render(EnumHandSideAA side, ModelRenderer arm, ModelRenderer playerArm) {
        arm.rotationPointX = -5.0F * side.delta();
        arm.rotationPointY = 2.0F;
        arm.rotationPointZ = 0.0F;
        AAUtils.setPlayerArmDataToArm(arm, playerArm);
        arm.rotateAngleX = 0.0F;
        boolean h = arm.isHidden;
        boolean s = arm.showModel;
        arm.isHidden = false;
        arm.showModel = true;
        arm.render(1.0F / 16.0F);
        arm.isHidden = h;
        arm.showModel = s;
    }

    private boolean setTextures(EntityPlayer player, EnumHandSideAA hand, boolean overlay, boolean simulate) {
        ItemStack stack = BaublesApi.getBaublesHandler(player).getStackInSlot(BaubleType.RING.getValidSlots()[hand == EnumHandSideAA.LEFT ? 0 : 1]);
        ResourceLocation textures = overlay ? this.getOverlayTextures(stack) : this.getTextures(stack);
        if (textures != null) {
            if (!simulate) Minecraft.getMinecraft().getTextureManager().bindTexture(textures);
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    private ResourceLocation getTextures(ItemStack stack) {
        if (stack.getItem() == ModItems.POWER_GLOVE) {
            return this.powerGloveTextures;
        } else if (stack.getItem() == ModItems.FERAL_CLAWS) {
            return this.feralClawsTextures;
        } else if (stack.getItem() == ModItems.MECHANICAL_GLOVE) {
            return this.mechanicalGloveTextures;
        } else if (stack.getItem() == ModItems.FIRE_GAUNTLET) {
            return this.fireGauntletTextures;
        } else {
            return stack.getItem() == ModItems.POCKET_PISTON ? this.pocketPistonTextures : null;
        }
    }

    @Nullable
    private ResourceLocation getOverlayTextures(ItemStack stack) {
        return stack.getItem() != ModItems.FIRE_GAUNTLET && stack.getItem() != ModItems.MAGMA_STONE ? null : this.fireGauntletOverlayTextures;
    }

    @Override
    public void deactivate() {
        this.deactivate = true;
    }

    @Override
    public IPriority priority() {
        return Priority.NORMAL;
    }
}
