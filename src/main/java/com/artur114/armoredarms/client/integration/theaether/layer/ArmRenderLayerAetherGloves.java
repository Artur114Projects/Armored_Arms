package com.artur114.armoredarms.client.integration.theaether.layer;

import com.artur114.armoredarms.client.engines.AbstractRenderEngineForge;
import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.main.AAConfig;
import com.gildedgames.the_aether.api.AetherAPI;
import com.gildedgames.the_aether.api.player.IPlayerAether;
import com.gildedgames.the_aether.api.player.util.IAccessoryInventory;
import com.gildedgames.the_aether.items.ItemsAether;
import com.gildedgames.the_aether.items.accessories.ItemAccessory;
import com.gildedgames.the_aether.items.accessories.ItemAccessoryDyable;
import com.gildedgames.the_aether.player.PlayerAether;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ArmRenderLayerAetherGloves implements IArmRenderLayer<AbstractRenderEngineForge<?, ?>> {
    private RenderPlayer renderPlayer;
    public ModelBiped modelMisc = null;
    private boolean shouldRenderGloves;
    private boolean deactivate = false;
    private double modelSize = -1.0D;
    private boolean render = false;

    @Override
    public void update(AbstractRenderEngineForge<?, ?> engine) {
        if (this.deactivate) {
            return;
        }
        IPlayerAether playerAether = AetherAPI.getInstance().get(engine.mc.player);
        IAccessoryInventory accessories = playerAether.getAccessoryInventory();
        boolean flag = !accessories.getStackInSlot(6).isEmpty() && ((PlayerAether) playerAether).shouldRenderGloves;
        if (flag && this.modelSize != AAConfig.vanillaArmorModelSize) {
            this.modelMisc = new ModelBiped(((float) AAConfig.vanillaArmorModelSize + 0.01F));
            this.modelSize = AAConfig.vanillaArmorModelSize;
        }
        this.render = flag && this.shouldRenderGloves;
    }

    @Override
    public void render(AbstractRenderEngineForge<?, ?> engine, EnumHandSideAA handSide) {
        if (this.deactivate) {
            return;
        }
        RenderManager manager = Minecraft.getMinecraft().getRenderManager();
        IPlayerAether playerAether = AetherAPI.getInstance().get(engine.mc.player);
        IAccessoryInventory accessories = playerAether.getAccessoryInventory();
        ModelRenderer renderer = AAUtils.handFromModelBiped(this.modelMisc, handSide);

        float scale = 1.0F / 16.0F;

        GlStateManager.pushMatrix();

        if (accessories.getStackInSlot(6).getItem().getClass() == ItemAccessory.class) {
            ItemAccessory shield = (ItemAccessory) accessories.getStackInSlot(6).getItem();
            manager.renderEngine.bindTexture(shield.texture);
            int j = shield.getColorFromItemStack(accessories.getStackInSlot(6), 0);
            float red = (float) (j >> 16 & 255) / 255.0F;
            float green = (float) (j >> 8 & 255) / 255.0F;
            red = (float) (j & 255) / 255.0F;
            if (shield != ItemsAether.phoenix_gloves) {
                GlStateManager.color(red, green, red);
            }

            renderer.rotationPointX = -5.0F * handSide.delta();
            renderer.rotationPointY = 2.0F;
            renderer.rotationPointZ = 0.0F;
            AAUtils.setPlayerArmDataToArm(renderer, AAUtils.handFromModelBiped(this.renderPlayer.getMainModel(), handSide));

            boolean h = renderer.isHidden;
            boolean s = renderer.showModel;
            renderer.isHidden = false;
            renderer.showModel = true;
            renderer.render(scale);
            renderer.isHidden = h;
            renderer.showModel = s;

            GlStateManager.color(1.0F, 1.0F, 1.0F);
        } else if (accessories.getStackInSlot(6).getItem().getClass() == ItemAccessoryDyable.class) {
            ItemAccessoryDyable gloves = (ItemAccessoryDyable) accessories.getStackInSlot(6).getItem();
            manager.renderEngine.bindTexture(gloves.texture);
            int j = gloves.getColor(accessories.getStackInSlot(6));
            float red = (float) (j >> 16 & 255) / 255.0F;
            float green = (float) (j >> 8 & 255) / 255.0F;
            red = (float) (j & 255) / 255.0F;
            GlStateManager.color(red, green, red);

            renderer.rotationPointX = -5.0F * handSide.delta();
            renderer.rotationPointY = 2.0F;
            renderer.rotationPointZ = 0.0F;
            AAUtils.setPlayerArmDataToArm(renderer, AAUtils.handFromModelBiped(this.renderPlayer.getMainModel(), handSide));

            boolean h = renderer.isHidden;
            boolean s = renderer.showModel;
            renderer.isHidden = false;
            renderer.showModel = true;
            renderer.render(scale);
            renderer.isHidden = h;
            renderer.showModel = s;

            GlStateManager.color(1.0F, 1.0F, 1.0F);
        }

        GlStateManager.popMatrix();
    }

    @Override
    public void init(AbstractRenderEngineForge<?, ?> engine, IAAModContainer mod) {
        this.renderPlayer = (RenderPlayer) Minecraft.getMinecraft().getRenderManager().<AbstractClientPlayer>getEntityRenderObject(engine.mc.player);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public boolean needRender(AbstractRenderEngineForge<?, ?> engine, boolean renderEngineState) {
        return this.render;
    }

    @Override
    public Class<AbstractRenderEngineForge<?, ?>> targetEngine() {
        return AbstractRenderEngineForge.clazz();
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
        return Priority.NORMAL;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderHandHeight(RenderSpecificHandEvent e) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        PlayerAether playerAether = (PlayerAether) AetherAPI.getInstance().get(player);
        this.shouldRenderGloves = playerAether.shouldRenderGloves;
        playerAether.shouldRenderGloves = false;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void renderHandLow(RenderSpecificHandEvent e) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        PlayerAether playerAether = (PlayerAether) AetherAPI.getInstance().get(player);
        playerAether.shouldRenderGloves = this.shouldRenderGloves;
    }
}
