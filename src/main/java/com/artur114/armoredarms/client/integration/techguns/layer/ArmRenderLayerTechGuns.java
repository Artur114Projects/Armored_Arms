package com.artur114.armoredarms.client.integration.techguns.layer;

import com.artur114.armoredarms.client.engines.AbstractRenderEngineForge;
import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.main.AAConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import techguns.capabilities.TGExtendedPlayer;
import techguns.client.models.armor.ModelGloves;
import techguns.items.armors.GenericArmor;

public class ArmRenderLayerTechGuns implements IArmRenderLayer<AbstractRenderEngineForge<?, ?>> {
    private AbstractRenderEngineForge<?, ?> engine;
    private boolean deactivate = false;
    private boolean render = false;

    private final ModelBiped model = new ModelGloves((float) AAConfig.vanillaArmorModelSize - 0.01F, false);
    private final ModelBiped model_slim = new ModelGloves((float) AAConfig.vanillaArmorModelSize - 0.01F, true);
    private final ResourceLocation texture = new ResourceLocation("techguns:textures/models/armor/working_gloves.png");
    private final ResourceLocation texture_slim = new ResourceLocation("techguns:textures/models/armor/working_gloves_slim.png");

    @Override
    public void update(AbstractRenderEngineForge<?, ?> engine) {
        if (this.deactivate) {
            return;
        }
        this.render = this.isGlovesVisible(engine.mc.player);
    }

    @Override
    public void render(AbstractRenderEngineForge<?, ?> engine, EnumHandSideAA handSide) {
        if (this.deactivate) {
            return;
        }
        if (this.render) {
            AbstractClientPlayer player = engine.mc.player;
            boolean slim = false;
            if (this.model_slim != null && player != null) {
                if (player.getSkinType().equals("slim")) {
                    slim = true;
                }
            }

            ModelBiped m = slim ? this.model_slim : this.model;

            Minecraft.getMinecraft().getTextureManager().bindTexture(slim ? this.texture_slim : this.texture);

            ModelRenderer arm = AAUtils.handFromModelBiped(m, handSide);
            engine.mainBones().bySide(handSide).injectTo(arm);
            arm.rotateAngleX = 0.0F;
            boolean h = arm.isHidden;
            boolean s = arm.showModel;
            arm.isHidden = false;
            arm.showModel = true;
            arm.render(1.0F / 16.0F);
            arm.isHidden = h;
            arm.showModel = s;
        }
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
    public AbstractRenderEngineForge<?, ?> engine() {
        return this.engine;
    }

    @Override
    public void init(AbstractRenderEngineForge<?, ?> engine, IAAModContainer mod) {
        this.engine = engine;
    }

    protected boolean isGlovesVisible(EntityPlayer ply) {
        ItemStack b = ply.inventory.armorInventory.get(2);
        TGExtendedPlayer props = TGExtendedPlayer.get(ply);
        if (!b.isEmpty() && b.getItem() instanceof GenericArmor) {
            GenericArmor a = (GenericArmor)b.getItem();
            return !a.isHideGloveslot() && !props.tg_inventory.inventory.get(2).isEmpty();
        }
        return !props.tg_inventory.inventory.get(2).isEmpty();
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
}
