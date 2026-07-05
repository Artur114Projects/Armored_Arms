package com.artur114.armoredarms.client.integration.galacticraftcore.layer;

import com.artur114.armoredarms.client.engines.AbstractRenderEngineForge;
import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;
import com.artur114.armoredarms.core.util.IAAModContainer;
import galaxyspace.core.GSItems;
import micdoodle8.mods.galacticraft.api.item.IItemThermal;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderPlayerGC;
import micdoodle8.mods.galacticraft.core.client.render.entities.layer.LayerThermalPadding;
import micdoodle8.mods.galacticraft.core.wrappers.PlayerGearData;
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems;
import micdoodle8.mods.galacticraft.planets.venus.VenusItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ArmRenderLayerThermalPadding implements IArmRenderLayer<AbstractRenderEngineForge<?, ?>> {
    private final ResourceLocation texture_t3 = new ResourceLocation("galaxyspace", "textures/model/armor/thermal_padding_t3_1.png");
    private final ResourceLocation texture_t4 = new ResourceLocation("galaxyspace", "textures/model/armor/thermal_padding_t4_1.png");
    private AbstractRenderEngineForge<?, ?> engine;
    private LayerThermalPadding renderTermal;
    private RenderPlayer renderPlayer;
    private boolean deactivate = false;
    private boolean render = false;

    @Override
    public void update(AbstractRenderEngineForge<?, ?> engine) {
        if (this.deactivate) {
            return;
        }
        PlayerGearData gearData = GalacticraftCore.proxy.getGearData(engine.mc.player);
        if (gearData != null) {
            this.render = gearData.getThermalPadding(1) != -1;
        }
    }

    @Override
    public void render(AbstractRenderEngineForge<?, ?> engine, EnumHandSideAA handSide) {
        if (this.deactivate) {
            return;
        }
        Item item = this.getItemStackFromSlot(engine.mc.player, EntityEquipmentSlot.CHEST);
        AbstractClientPlayer player = engine.mc.player;
        float scale = 1.0F / 16.0F;
        if (item != null) {
            ModelBiped model = this.renderTermal.getModelFromSlot(EntityEquipmentSlot.CHEST);
            ModelRenderer renderer = AAUtils.handFromModelBiped(model, handSide);
            if (item instanceof IItemThermal) {
                IItemThermal itemT = (IItemThermal) item;
                switch (itemT.getThermalStrength()) {
                    case 1:
                        this.renderPlayer.bindTexture(RenderPlayerGC.thermalPaddingTexture1);
                        break;
                    case 2:
                        this.renderPlayer.bindTexture(RenderPlayerGC.thermalPaddingTexture1_T2);
                        break;
                    case 3:
                        this.renderPlayer.bindTexture(this.texture_t3);
                        break;
                    case 4:
                        this.renderPlayer.bindTexture(this.texture_t4);
                        break;
                }
            }

            engine.mainBones().bySide(handSide).injectTo(renderer);

            boolean h = renderer.isHidden;
            boolean s = renderer.showModel;
            renderer.isHidden = false;
            renderer.showModel = true;
            renderer.render(scale);
            renderer.isHidden = h;
            renderer.showModel = s;
            GlStateManager.disableLighting();
            Minecraft.getMinecraft().renderEngine.bindTexture(RenderPlayerGC.thermalPaddingTexture0);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            float time = (float)player.ticksExisted / 10.0F;
            float sTime = (float)Math.sin(time) * 0.5F + 0.5F;
            float r = 0.2F * sTime;
            float g = 1.0F * sTime;
            float b = 0.2F * sTime;
            if (player.world.provider instanceof IGalacticraftWorldProvider) {
                float modifier = ((IGalacticraftWorldProvider) player.world.provider).getThermalLevelModifier();
                if (modifier > 0.0F) {
                    b = g;
                    g = r;
                } else if (modifier < 0.0F) {
                    r = g;
                    g = b;
                }
            }
            GlStateManager.color(r, g, b, 0.4F * sTime);
            h = renderer.isHidden;
            s = renderer.showModel;
            renderer.isHidden = false;
            renderer.showModel = true;
            renderer.render(scale);
            renderer.isHidden = h;
            renderer.showModel = s;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.enableLighting();
        }
    }

    public Item getItemStackFromSlot(EntityLivingBase living, EntityEquipmentSlot slotIn) {
        PlayerGearData gearData = GalacticraftCore.proxy.getGearData((EntityPlayer)living);
        if (gearData != null) {
            int padding = gearData.getThermalPadding(1);
            if (padding != -1) {
                switch (padding) {
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                        return AsteroidsItems.thermalPadding;
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                        return VenusItems.thermalPaddingTier2;
                    case 45:
                    case 46:
                    case 47:
                    case 48:
                        return GSItems.THERMAL_PADDING_3;
                    case 49:
                    case 50:
                    case 51:
                    case 52:
                        return GSItems.THERMAL_PADDING_4;
                }
            }
        }

        return null;
    }

    @Override
    public void init(AbstractRenderEngineForge<?, ?> engine, IAAModContainer mod) {
        this.renderPlayer = (RenderPlayer) Minecraft.getMinecraft().getRenderManager().<AbstractClientPlayer>getEntityRenderObject(engine.mc.player);
        this.renderTermal = new LayerThermalPadding(this.renderPlayer);
        this.engine = engine;
    }

    @Override
    public boolean needRender(AbstractRenderEngineForge<?, ?> engine, boolean renderEngineState) {
        return this.render;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<AbstractRenderEngineForge<?, ?>> targetEngine() {
        return (Class<AbstractRenderEngineForge<?, ?>>) (Class<?>) AbstractRenderEngineForge.class;
    }

    @Override
    public AbstractRenderEngineForge<?, ?> engine() {
        return this.engine;
    }

    @Override
    public void deactivate() {
        this.deactivate = true;
    }

    @Override
    public boolean isDeactivated() {
        return this.deactivate;
    }

    @Override
    public IPriority priority() {
        return Priority.LOW;
    }
}
