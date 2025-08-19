package com.artur114.armoredarms.client;

import com.artur114.armoredarms.api.override.IOverriderGetModel;
import com.artur114.armoredarms.api.override.IOverriderGetTex;
import com.artur114.armoredarms.api.override.IOverriderRender;
import com.artur114.armoredarms.client.util.EnumRenderType;
import com.artur114.armoredarms.client.util.ShapelessRL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.client.event.RenderSpecificHandEvent;

import java.lang.reflect.Field;
import java.util.*;

public class RenderArmManager {
    protected static final ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    public static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");

    public Map<ResourceLocation, IOverriderGetModel> renderOverriders = new HashMap<>();
    public Map<ResourceLocation, IOverriderGetTex> textureOverriders = new HashMap<>();
    public Map<ResourceLocation, IOverriderRender> modelOverriders = new HashMap<>();
    public List<LayerRenderer<AbstractClientPlayer>> layerRenderers = null;
    public Set<ResourceLocation> renderBlackList = new HashSet<>();
    public final Minecraft mc = Minecraft.getMinecraft();
    public Set<Item> killingArmor = new HashSet<>();
    public LayerBipedArmor armorLayer = null;
    public RenderPlayer renderPlayer = null;
    public ModelBiped armorDefault = null;
    public boolean firstFrame = true;
    public boolean died = false;

    public ItemStack chestPlate = null;
    public ItemArmor chestPlateItem = null;

    public void renderSpecificHandEvent(RenderSpecificHandEvent e) {
        if (this.died) return;
        Item item = this.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem();
        if (this.killingArmor.contains(item)) return;


        try {
            this.tryRender(e);
        } catch (Exception ex) {
            if (ex.getMessage().startsWith("[FATAL]")) {
                this.died = true;
            } else if (this.chestPlateItem != null) {
                this.killingArmor.add(this.chestPlateItem);
            }
            new RuntimeException(ex).printStackTrace(System.err);
        }
    }

    public void tryRender(RenderSpecificHandEvent e) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        ItemStack chestPlate = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        EnumHandSide enumhandside = e.getHand() == EnumHand.MAIN_HAND ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
        ItemArmor chestPlateItem = chestPlate.getItem() instanceof ItemArmor ? (ItemArmor) chestPlate.getItem() : null;
        float interpPitch = e.getInterpolatedPitch();
        float swingProgress = e.getSwingProgress();
        float equipProgress = e.getEquipProgress();
        ItemStack stack = e.getItemStack();
        EnumHand hand = e.getHand();

        if (chestPlateItem == null) {
            return;
        }

        if (this.firstFrame) {
            try {
                this.init(player);
            } catch (Exception exp) {
                throw new RuntimeException("[FATAL] It was not possible to load RenderArmManager, custom hands will not be rendered!", exp);
            }
        }

        this.chestPlate = chestPlate;
        this.chestPlateItem = chestPlateItem;

        GlStateManager.pushMatrix();
        boolean flag = false;

        if (stack.isEmpty()) {
            if (hand == EnumHand.MAIN_HAND) {
                this.renderArmFirstPerson(equipProgress, swingProgress, enumhandside);

                flag = true;
            }
        } else if (stack.getItem() instanceof ItemMap) {
            if (hand == EnumHand.MAIN_HAND && player.getHeldItem(EnumHand.OFF_HAND).isEmpty()) {
                this.renderMapFirstPerson(interpPitch, equipProgress, swingProgress, stack);
            } else {
                this.renderMapFirstPersonSide(equipProgress, enumhandside, swingProgress, stack);
            }

            flag = true;
        }

        if (flag) {
            e.setCanceled(true);
        }

        GlStateManager.popMatrix();
    }

    public void init(AbstractClientPlayer player) {
        this.firstFrame = false;
        this.renderPlayer = this.initRenderPlayer(player);
        this.layerRenderers = this.initLayerRenderers(player);
        this.armorLayer = this.findArmorLayer();
        this.armorDefault = new ModelBiped(1.0F);
    }

    @SuppressWarnings("unchecked")
    public List<LayerRenderer<AbstractClientPlayer>> initLayerRenderers(AbstractClientPlayer player) {
        try {
            Field field = null;
            Field[] fields = RenderLivingBase.class.getDeclaredFields();

            for (Field rField : fields) {
                rField.setAccessible(true);
                if (rField.get(this.renderPlayer) instanceof List) {
                    field = rField;
                }
                rField.setAccessible(false);
            }

            if (field == null) {
                throw new NullPointerException("layerRenderers is not find!");
            }

            field.setAccessible(true);
            List<LayerRenderer<AbstractClientPlayer>> layers = (List<LayerRenderer<AbstractClientPlayer>>) field.get(this.renderPlayer);
            field.setAccessible(false);
            return layers;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public LayerBipedArmor findArmorLayer() {
        for (LayerRenderer<?> layerRenderer : this.layerRenderers) {
            if (layerRenderer instanceof LayerBipedArmor) {
                return (LayerBipedArmor) layerRenderer;
            }
        }
        throw new IllegalStateException();
    }

    public RenderPlayer initRenderPlayer(AbstractClientPlayer player) {
        return (RenderPlayer) Minecraft.getMinecraft().getRenderManager().<AbstractClientPlayer>getEntityRenderObject(player);
    }

    public void renderArms() {
        GlStateManager.disableCull();
        GlStateManager.pushMatrix();
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        this.transAndRenderArm(EnumHandSide.RIGHT);
        this.transAndRenderArm(EnumHandSide.LEFT);
        GlStateManager.popMatrix();
        GlStateManager.enableCull();
    }

    public float getMapAngleFromPitch(float pitch) {
        float f = 1.0F - pitch / 45.0F + 0.1F;
        f = MathHelper.clamp(f, 0.0F, 1.0F);
        f = -MathHelper.cos(f * (float)Math.PI) * 0.5F + 0.5F;
        return f;
    }

    public void transAndRenderArm(EnumHandSide enumHandSide) {
        GlStateManager.pushMatrix();
        float f = enumHandSide == EnumHandSide.RIGHT ? 1.0F : -1.0F;
        GlStateManager.rotate(92.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f * -41.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(f * 0.3F, -1.1F, 0.45F);

        this.renderArm(this.mc.player, enumHandSide);

        GlStateManager.popMatrix();
    }

    public void renderMapFirstPerson(float interPitch, float equipProgress, float swingProgress, ItemStack stack) {
        float f = MathHelper.sqrt(swingProgress);
        float f1 = -0.2F * MathHelper.sin(swingProgress * (float)Math.PI);
        float f2 = -0.4F * MathHelper.sin(f * (float)Math.PI);
        GlStateManager.translate(0.0F, -f1 / 2.0F, f2);
        float f3 = this.getMapAngleFromPitch(interPitch);
        GlStateManager.translate(0.0F, 0.04F + equipProgress * -1.2F + f3 * -0.5F, -0.72F);
        GlStateManager.rotate(f3 * -85.0F, 1.0F, 0.0F, 0.0F);
        this.renderArms();
        float f4 = MathHelper.sin(f * (float)Math.PI);
        GlStateManager.rotate(f4 * 20.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        this.renderMapFirstPerson(stack);
    }

    public void renderMapFirstPersonSide(float p_187465_1_, EnumHandSide hand, float p_187465_3_, ItemStack stack) {
        float f = hand == EnumHandSide.RIGHT ? 1.0F : -1.0F;
        GlStateManager.translate(f * 0.125F, -0.125F, 0.0F);

        GlStateManager.pushMatrix();
        GlStateManager.rotate(f * 10.0F, 0.0F, 0.0F, 1.0F);
        this.renderArmFirstPerson(p_187465_1_, p_187465_3_, hand);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(f * 0.51F, -0.08F + p_187465_1_ * -1.2F, -0.75F);
        float f1 = MathHelper.sqrt(p_187465_3_);
        float f2 = MathHelper.sin(f1 * (float)Math.PI);
        float f3 = -0.5F * f2;
        float f4 = 0.4F * MathHelper.sin(f1 * ((float)Math.PI * 2F));
        float f5 = -0.3F * MathHelper.sin(p_187465_3_ * (float)Math.PI);
        GlStateManager.translate(f * f3, f4 - 0.3F * f2, f5);
        GlStateManager.rotate(f2 * -45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f * f2 * -30.0F, 0.0F, 1.0F, 0.0F);
        this.renderMapFirstPerson(stack);
        GlStateManager.popMatrix();
    }

    public void renderMapFirstPerson(ItemStack stack) {
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.scale(0.38F, 0.38F, 0.38F);
        GlStateManager.disableLighting();
        this.mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.translate(-0.5F, -0.5F, 0.0F);
        GlStateManager.scale(0.0078125F, 0.0078125F, 0.0078125F);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(-7.0D, 135.0D, 0.0D).tex(0.0D, 1.0D).endVertex();
        bufferbuilder.pos(135.0D, 135.0D, 0.0D).tex(1.0D, 1.0D).endVertex();
        bufferbuilder.pos(135.0D, -7.0D, 0.0D).tex(1.0D, 0.0D).endVertex();
        bufferbuilder.pos(-7.0D, -7.0D, 0.0D).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();
        MapData mapdata = ((ItemMap) stack.getItem()).getMapData(stack, this.mc.world);

        if (mapdata != null) {
            this.mc.entityRenderer.getMapItemRenderer().renderMap(mapdata, false);
        }

        GlStateManager.enableLighting();
    }

    public void renderArmFirstPerson(float equipProgress, float swingProgress, EnumHandSide enumhandside) {
        float f = enumhandside != EnumHandSide.LEFT ? 1.0F : -1.0F;
        float f1 = MathHelper.sqrt(swingProgress);
        float f2 = -0.3F * MathHelper.sin(f1 * (float)Math.PI);
        float f3 = 0.4F * MathHelper.sin(f1 * ((float)Math.PI * 2F));
        float f4 = -0.4F * MathHelper.sin(swingProgress * (float)Math.PI);
        GlStateManager.translate(f * (f2 + 0.64000005F), f3 + -0.6F + equipProgress * -0.6F, f4 + -0.71999997F);
        GlStateManager.rotate(f * 45.0F, 0.0F, 1.0F, 0.0F);
        float f5 = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        float f6 = MathHelper.sin(f1 * (float)Math.PI);
        GlStateManager.rotate(f * f6 * 70.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f * f5 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(f * -1.0F, 3.6F, 3.5F);
        GlStateManager.rotate(f * 120.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f * -135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(f * 5.6F, 0.0F, 0.0F);
        GlStateManager.disableCull();

        this.renderArm(this.mc.player, enumhandside);

        GlStateManager.enableCull();
    }

    public void renderArm(AbstractClientPlayer player, EnumHandSide handSide) {
        ResourceLocation playerTex = player.isInvisible() ? null : this.renderPlayer.getEntityTexture(player);
        ModelPlayer modelPlayer = this.renderPlayer.getMainModel();

        ResourceLocation armorTexOv = this.getArmorTex(player, "o");
        ResourceLocation armorTex = this.getArmorTex(player, null);
        ModelBiped armorModel = this.getArmorModel(player);

        ModelRenderer[] playerArms = this.getHand(modelPlayer, handSide);
        ModelRenderer armorArm = this.getHand(armorModel, handSide)[0];

        this.render(playerArms[0], playerTex, EnumRenderType.ARM);
        this.render(playerArms[1], playerTex, EnumRenderType.ARM_WEAR);
        this.render(armorArm, armorTexOv, EnumRenderType.ARMOR_OVERLAY);
        this.render(armorArm, armorTex, EnumRenderType.ARMOR);

        this.render(armorArm, ENCHANTED_ITEM_GLINT_RES, EnumRenderType.ARMOR_ENCHANT);
    }

    public void render(ModelRenderer hand, ResourceLocation tex, EnumRenderType type) {
        if (tex == ENCHANTED_ITEM_GLINT_RES && this.chestPlate.hasEffect()) {
            float f = (float) this.mc.player.ticksExisted;
            GlStateManager.pushMatrix();
            GlStateManager.pushAttrib();
            Minecraft.getMinecraft().getTextureManager().bindTexture(ENCHANTED_ITEM_GLINT_RES);
            Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
            GlStateManager.enableBlend();
            GlStateManager.depthFunc(514);
            GlStateManager.depthMask(false);
            float f1 = 0.5F;
            GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0F);

            for (int i = 0; i < 2; ++i) {
                GlStateManager.disableLighting();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
                float f2 = 0.76F;
                GlStateManager.color(0.38F, 0.19F, 0.608F, 1.0F);
                GlStateManager.matrixMode(5890);
                GlStateManager.loadIdentity();
                float f3 = 0.33333334F;
                GlStateManager.scale(0.33333334F, 0.33333334F, 0.33333334F);
                GlStateManager.rotate(30.0F - (float)i * 60.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.translate(0.0F, f * (0.001F + (float)i * 0.003F) * 20.0F, 0.0F);
                GlStateManager.matrixMode(5888);
                hand.rotateAngleX = 0.0F;
                hand.rotateAngleY = 0.0F;
                hand.rotateAngleZ = 0.0F;
                hand.render(0.0625F);
                GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            }

            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(5888);
            GlStateManager.enableLighting();
            GlStateManager.depthMask(true);
            GlStateManager.depthFunc(515);
            GlStateManager.disableBlend();
            Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
            GlStateManager.popMatrix();
            GlStateManager.popAttrib();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        } else if (tex != null && tex != ENCHANTED_ITEM_GLINT_RES) {
            this.mc.getTextureManager().bindTexture(tex);
            hand.rotateAngleX = 0.0F;
            hand.rotateAngleY = 0.0F;
            hand.rotateAngleZ = 0.0F;
            hand.render(0.0625F);
        }
    }

    public ModelBiped getArmorModel(AbstractClientPlayer player) {
        ModelBiped mb = this.chestPlateItem.getArmorModel(player, this.chestPlate, EntityEquipmentSlot.CHEST, this.armorDefault);
        if (mb == null) mb = this.armorDefault;
        return mb;
    }

    public ResourceLocation getArmorTex(AbstractClientPlayer player, String type) {
        if (type == null) {
            return this.armorLayer.getArmorResource(player, this.chestPlate, EntityEquipmentSlot.CHEST, null);
        } else if (type.equals("o")) {
            if (this.chestPlateItem.hasOverlay(this.chestPlate)) {
                return this.armorLayer.getArmorResource(player, this.chestPlate, EntityEquipmentSlot.CHEST, "overlay");
            }
        }
        return null;
    }

    public ModelRenderer[] getHand(ModelBiped mb, EnumHandSide handSide) {
        if (mb instanceof ModelPlayer) {
            switch (handSide) {
                case RIGHT:
                    return new ModelRenderer[] {mb.bipedRightArm, ((ModelPlayer) mb).bipedRightArmwear};
                case LEFT:
                    return new ModelRenderer[] {mb.bipedLeftArm, ((ModelPlayer) mb).bipedLeftArmwear};
                default:
                    return new ModelRenderer[0];
            }
        } else {
            switch (handSide) {
                case RIGHT:
                    return new ModelRenderer[] {mb.bipedRightArm};
                case LEFT:
                    return new ModelRenderer[] {mb.bipedLeftArm};
                default:
                    return new ModelRenderer[0];
            }
        }
    }
}
