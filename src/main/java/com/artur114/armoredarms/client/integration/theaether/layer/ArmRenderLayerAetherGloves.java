package com.artur114.armoredarms.client.integration.theaether.layer;

import com.artur114.armoredarms.client.engines.AbstractRenderEngineForge;
import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.Reflector;
import com.artur114.armoredarms.main.AAConfig;
import com.gildedgames.the_aether.api.accessories.AccessoryType;
import com.gildedgames.the_aether.api.player.util.IAccessoryInventory;
import com.gildedgames.the_aether.items.ItemsAether;
import com.gildedgames.the_aether.items.accessories.ItemAccessory;
import com.gildedgames.the_aether.player.PlayerAether;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class ArmRenderLayerAetherGloves implements IArmRenderLayer<AbstractRenderEngineForge<?, ?>> {
    private ModelBiped defaultModel = new ModelBiped((float) AAConfig.vanillaArmorModelSize);
    private double modelSize = AAConfig.vanillaArmorModelSize - 1;
    public final Minecraft mc = Minecraft.getMinecraft();
    public RenderPlayer renderPlayer = null;
    public ItemStack gloves = null;
    public AbstractRenderEngineForge<?, ?> engine;
    private boolean deactivated = false;

    @Override
    public void update(AbstractRenderEngineForge<?, ?> engine) {
        AbstractClientPlayer player = engine.mc.thePlayer;
        String method = "getStackInSlot";
        try {
            Class.forName("com.gildedgames.the_aether.api.accessories.DegradationRate");
            method = "getFirstStackIfWearing";
        } catch (ClassNotFoundException ignored) {}

        PlayerAether playerAether = PlayerAether.get(player);
        this.gloves = Reflector.invokeMethod(IAccessoryInventory.class, playerAether.getAccessoryInventory(), method, new Class[] {AccessoryType.class}, new Object[] {AccessoryType.GLOVES});

        if (this.modelSize != AAConfig.vanillaArmorModelSize) {
            this.defaultModel = new ModelBiped((float) AAConfig.vanillaArmorModelSize);
            this.modelSize = AAConfig.vanillaArmorModelSize;
            this.defaultModel.swingProgress = 0.0F;
            this.defaultModel.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, player);
        }
    }

    @Override
    public void render(AbstractRenderEngineForge<?, ?> engine, EnumHandSideAA handSide) {
        AbstractClientPlayer player = engine.mc.thePlayer;
        this.mc.getTextureManager().bindTexture(player.getLocationSkin());
        if (this.gloves != null) {
            if (this.gloves.getItem() instanceof ItemAccessory) {
                this.mc.getTextureManager().bindTexture(((ItemAccessory)this.gloves.getItem()).texture);
                int colour = gloves.getItem().getColorFromItemStack(this.gloves, 0);
                float red = (float)(colour >> 16 & 255) / 255.0F;
                float green = (float)(colour >> 8 & 255) / 255.0F;
                float blue = (float)(colour & 255) / 255.0F;
                if (this.gloves.getItem() != ItemsAether.phoenix_gloves) {
                    GL11.glColor3f(red, green, blue);
                }

                GL11.glEnable(3042);
                ModelRenderer arm = AAUtils.handFromModelBiped(this.defaultModel, handSide);
                engine.mainBones().bySide(handSide).injectTo(arm);
                arm.render(0.0625F);
                GL11.glDisable(3042);
                GL11.glColor3f(1.0F, 1.0F, 1.0F);
            }
        }
    }

    @Override
    public void init(AbstractRenderEngineForge<?, ?> engine, IAAModContainer mod) {
        this.renderPlayer = (RenderPlayer) RenderManager.instance.getEntityRenderObject(engine.mc.thePlayer);

        this.engine = engine;
    }

    @Override
    public boolean needRender(AbstractRenderEngineForge<?, ?> engine, boolean renderEngineState) {
        return this.gloves != null;
    }

    @Override
    public Class<AbstractRenderEngineForge<?, ?>> targetEngine() {
        return AbstractRenderEngineForge.clazz();
    }

    @Override
    public AbstractRenderEngineForge<?, ?> engine() {
        return this.engine;
    }

    @Override
    public boolean isDeactivated() {
        return this.deactivated;
    }

    @Override
    public void deactivate() {
        this.deactivated = true;
    }

    @Override
    public IPriority priority() {
        return Priority.LOW;
    }
}
