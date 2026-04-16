package com.artur114.armoredarms.client.integration.galacticraftcore.layer;

import com.artur114.armoredarms.client.engines.AbstractRenderEngineForge;
import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;
import com.artur114.armoredarms.core.util.IAAModContainer;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.client.model.ModelPlayerGC;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import micdoodle8.mods.galacticraft.core.wrappers.PlayerGearData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ArmRenderLayerThermalPadding implements IArmRenderLayer<AbstractRenderEngineForge<?, ?>> {
    private final ResourceLocation thermalPaddingTexture0 = new ResourceLocation("galacticraftasteroids", "textures/misc/thermalPadding_0.png");
    private final ResourceLocation thermalPaddingTexture1 = new ResourceLocation("galacticraftasteroids", "textures/misc/thermalPadding_1.png");
    private final Minecraft mc = Minecraft.getMinecraft();

    private AbstractRenderEngineForge<?, ?> engine;
    private final ModelBiped modelThermalPadding = new ModelPlayerGC(0.25F);
    private boolean deactivate = false;
    private boolean render = false;

    @Override
    public void update(AbstractRenderEngineForge<?, ?> engine) {
        if (this.deactivate) {
            return;
        }
        PlayerGearData gearData = ClientProxyCore.playerItemData.get(engine.mc.thePlayer.getCommandSenderName());
        if (gearData != null) {
            this.render = gearData.getThermalPadding(1) != -1;
        }
    }

    @Override
    public void render(AbstractRenderEngineForge<?, ?> engine, EnumHandSideAA handSide) {
        if (this.deactivate) {
            return;
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().renderEngine.bindTexture(this.thermalPaddingTexture1);
        AbstractClientPlayer player = this.mc.thePlayer;

        ModelRenderer arm = AAUtils.handFromModelBiped(this.modelThermalPadding, handSide);
        engine.mainBones().bySide(handSide).injectTo(arm);
        arm.render(1.0F / 16.0F);

        GL11.glDisable(2896);
        Minecraft.getMinecraft().renderEngine.bindTexture(this.thermalPaddingTexture0);
        GL11.glEnable(3008);
        GL11.glEnable(3042);
        GL11.glAlphaFunc(516, 0.0F);
        GL11.glBlendFunc(770, 771);
        float time = (float) player.ticksExisted / 10.0F;
        float sTime = (float)Math.sin(time) * 0.5F + 0.5F;
        float r = 0.2F * sTime;
        float g = 1.0F * sTime;
        float b = 0.2F * sTime;
        if (player.worldObj.provider instanceof IGalacticraftWorldProvider) {
            float modifier = ((IGalacticraftWorldProvider) player.worldObj.provider).getThermalLevelModifier();
            if (modifier > 0.0F) {
                b = g;
                g = r;
            } else if (modifier < 0.0F) {
                r = g;
                g = b;
            }
        }

        GL11.glColor4f(r, g, b, 0.4F * sTime);
        arm.render(1.0F / 16.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glEnable(2896);
    }


    @Override
    public void init(AbstractRenderEngineForge<?, ?> engine, IAAModContainer mod) {
        this.engine = engine;
        this.modelThermalPadding.swingProgress = 0.0F;
        this.modelThermalPadding.isRiding = false;
        this.modelThermalPadding.isChild = false;
        this.modelThermalPadding.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, this.mc.thePlayer);
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