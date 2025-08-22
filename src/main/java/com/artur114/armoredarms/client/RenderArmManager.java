package com.artur114.armoredarms.client;

import com.artur114.armoredarms.api.override.*;
import com.artur114.armoredarms.client.util.ShapelessRL;
import com.artur114.armoredarms.main.AAConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBase;
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
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@SideOnly(Side.CLIENT)
public class RenderArmManager {
    protected static final ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    public static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
    public final DefaultTextureGetter defaultTextureGetter = new DefaultTextureGetter();
    public final DefaultModelGetter defaultModelGetter = new DefaultModelGetter();
    public final DefaultRender defaultRender = new DefaultRender();
    public Map<ShapelessRL, IOverriderGetModel> modelOverriders = new HashMap<>();
    public Map<ShapelessRL, IOverriderGetTex> textureOverriders = new HashMap<>();
    public Map<ShapelessRL, IOverriderRender> renderOverriders = new HashMap<>();
    public List<LayerRenderer<AbstractClientPlayer>> layerRenderers = null;
    public List<ShapelessRL> renderBlackList = new ArrayList<>();
    public final Minecraft mc = Minecraft.getMinecraft();
    public Set<Item> killingArmor = new HashSet<>();
    public LayerBipedArmor armorLayer = null;
    public RenderPlayer renderPlayer = null;
    public ModelBiped armorDefault = null;
    public boolean initTick = true;
    public boolean render = false;
    public boolean died = false;


    public ItemStack chestPlate = null;
    public ItemArmor chestPlateItem = null;
    public ModelBase currentArmorModel = null;
    public ResourceLocation currentArmorTex = null;
    public ResourceLocation currentPlayerTex = null;
    public ResourceLocation currentArmorTexOv = null;
    public ModelPlayerIBone currentPlayerModel = null;
    public IOverriderGetTex currentGetTexOverrider = null;
    public IOverriderRender currentRenderOverrider = null;
    public IOverriderGetModel currentGetModelOverrider = null;

    public void renderSpecificHandEvent(RenderSpecificHandEvent e) {
        if (this.died || !this.render) {
            return;
        }

        try {
            this.tryRender(e);
        } catch (Throwable exp) {
            this.onException(exp, "tryRender");
        }
    }

    public void tickEventClientTickEvent(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.START || this.mc.player == null || this.mc.isGamePaused()) {
            return;
        }

        try {
            this.tryTick(e);
        } catch (Throwable exp) {
            this.onException(exp, "tryTick");
        }
    }

    public void tryRender(RenderSpecificHandEvent e) {
        EntityPlayerSP player = this.mc.player;
        EnumHandSide enumhandside = e.getHand() == EnumHand.MAIN_HAND ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
        float interpPitch = e.getInterpolatedPitch();
        float swingProgress = e.getSwingProgress();
        float equipProgress = e.getEquipProgress();
        ItemStack stack = e.getItemStack();
        EnumHand hand = e.getHand();

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

    public void tryTick(TickEvent.ClientTickEvent e) {
        AbstractClientPlayer player = this.mc.player;
        ItemStack chestPlate = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

        if (this.died || this.killingArmor.contains(chestPlate.getItem()) || !(chestPlate.getItem() instanceof ItemArmor)) {
            this.render = false; return;
        }

        if (this.initTick) {
            try {
                this.init(player); this.initTick = false;
            } catch (Throwable exp) {
                throw new RuntimeException("[FATAL] It was not possible to load RenderArmManager, custom hands will not be rendered!", exp);
            }
        }

        if (chestPlate == this.chestPlate) {
            return;
        }

        if (this.renderBlackList.contains(new ShapelessRL(chestPlate.getItem().getRegistryName()))) {
            this.chestPlate = chestPlate; this.render = false; return;
        }

        System.out.println("New armor: " + chestPlate.getItem().getRegistryName());

        this.render = true;
        this.chestPlate = chestPlate;
        this.chestPlateItem = (ItemArmor) chestPlate.getItem();

        this.updateOverriders();

        this.currentPlayerModel = new ModelPlayerIBone(this.renderPlayer.getMainModel());
        this.currentArmorModel = this.getArmorModel(player);

        this.currentPlayerTex = this.renderPlayer.getEntityTexture(player);
        this.currentArmorTexOv = this.getArmorTex(player, IOverriderGetTex.EnumModelTexType.OVERLAY);
        this.currentArmorTex = this.getArmorTex(player, IOverriderGetTex.EnumModelTexType.NULL);
    }

    public void updateOverriders() {
        ShapelessRL rl = new ShapelessRL(this.chestPlateItem.getRegistryName());

        AtomicReference<IOverriderRender> overriderRender = new AtomicReference<>(this.renderOverriders.get(rl));
        AtomicReference<IOverriderGetTex> overriderGetTex = new AtomicReference<>(this.textureOverriders.get(rl));
        AtomicReference<IOverriderGetModel> overriderGetModel = new AtomicReference<>(this.modelOverriders.get(rl));

        this.findOverrider(overriderRender, this.renderOverriders, rl);
        this.findOverrider(overriderGetTex, this.textureOverriders, rl);
        this.findOverrider(overriderGetModel, this.modelOverriders, rl);

        if (overriderRender.get() == null) overriderRender.set(this.defaultRender);
        if (overriderGetTex.get() == null) overriderGetTex.set(this.defaultTextureGetter);
        if (overriderGetModel.get() == null) overriderGetModel.set(this.defaultModelGetter);

        this.currentRenderOverrider = overriderRender.get();
        this.currentGetTexOverrider = overriderGetTex.get();
        this.currentGetModelOverrider = overriderGetModel.get();
    }

    public <T extends IOverrider> void findOverrider(AtomicReference<T> overrider, Map<ShapelessRL, T> map, ShapelessRL rl) {
        if (overrider.get() == null) {
            map.forEach((k, v) -> {
                if (k.equals(rl)) {
                    if (k.isAbsoluteShapeless() && overrider.get() != null) {
                        return;
                    }
                    overrider.set(v);
                }
            });
        }
    }

    public void onException(Throwable e, String method) {
        if (e.getMessage() != null && e.getMessage().startsWith("[FATAL]")) {
            this.died = true;
            if (this.mc.player != null) {
                this.mc.player.sendMessage(new TextComponentTranslation("armoredarms.error.fatal." + method).setStyle(new Style().setColor(TextFormatting.RED)));
            }
        } else if (this.chestPlateItem != null) {
            this.killingArmor.add(this.chestPlateItem);
            this.render = false;
            if (this.mc.player != null) {
                this.mc.player.sendMessage(new TextComponentTranslation("armoredarms.error." + method).setStyle(new Style().setColor(TextFormatting.RED)));
            }
        }
        new RuntimeException(e).printStackTrace(System.err);
    }

    public void init(AbstractClientPlayer player) {
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
        ResourceLocation playerTex = player.isInvisible() ? null : this.currentPlayerTex;
        ModelPlayerIBone modelPlayer = this.currentPlayerModel;

        ResourceLocation armorTexOv = this.currentArmorTexOv;
        ResourceLocation armorTex = this.currentArmorTex;
        ModelBase armorModel = this.currentArmorModel;

        IBodeThing playerArmsWear = this.getHand(modelPlayer, handSide, true);
        IBodeThing playerArms = this.getHand(modelPlayer, handSide, false);
        IBodeThing armorArm = this.getArmorArm(armorModel, handSide);

        this.render(modelPlayer.model, playerArms, playerTex, handSide, IOverriderRender.EnumRenderType.ARM);
        this.render(modelPlayer.model, playerArmsWear, playerTex, handSide, IOverriderRender.EnumRenderType.ARM_WEAR);
        this.render(armorModel, armorArm, armorTexOv, handSide, IOverriderRender.EnumRenderType.ARMOR_OVERLAY);
        this.render(armorModel, armorArm, armorTex, handSide, IOverriderRender.EnumRenderType.ARMOR);

        this.render(armorModel, armorArm, ENCHANTED_ITEM_GLINT_RES, handSide, IOverriderRender.EnumRenderType.ARMOR_ENCHANT);
    }

    public void render(ModelBase model, IBodeThing hand, ResourceLocation tex, EnumHandSide handSide, IOverriderRender.EnumRenderType type) {
        this.currentRenderOverrider.render(model, hand, tex, handSide, this.chestPlate, this.chestPlateItem, type);
    }

    public ModelBase getArmorModel(AbstractClientPlayer player) {
        return this.currentGetModelOverrider.getModel(player, this.chestPlateItem, this.chestPlate);
    }

    public IBodeThing getArmorArm(ModelBase mb, EnumHandSide handSide) {
        return this.currentGetModelOverrider.getArm(mb, this.chestPlateItem, this.chestPlate, handSide);
    }

    public ResourceLocation getArmorTex(AbstractClientPlayer player, IOverriderGetTex.EnumModelTexType type) {
        return this.currentGetTexOverrider.getTexture(player, this.armorLayer, this.layerRenderers, this.chestPlate, this.chestPlateItem, type);
    }

    public IBodeThing getHand(ModelPlayerIBone mb, EnumHandSide handSide, boolean wear) {
        if (mb == null) {
            return null;
        }
        switch (handSide) {
            case RIGHT:
                return wear ? mb.bipedRightArmwear : mb.bipedRightArm;
            case LEFT:
                return wear ? mb.bipedLeftArmwear : mb.bipedLeftArm;
            default:
                return null;
        }
    }

    public void addOverrider(ShapelessRL rl, IOverrider overrider, boolean replaceIfHas) {
        if (rl.isEmpty()) {
            return;
        }
        if (overrider instanceof IOverriderRender && (!this.renderOverriders.containsKey(rl) || replaceIfHas)) {
            this.renderOverriders.put(rl, (IOverriderRender) overrider);
            System.out.println("Added overrider render! " + overrider);
        }
        if (overrider instanceof IOverriderGetTex && (!this.textureOverriders.containsKey(rl) || replaceIfHas)) {
            this.textureOverriders.put(rl, (IOverriderGetTex) overrider);
            System.out.println("Added overrider get texture! " + overrider);
        }
        if (overrider instanceof IOverriderGetModel && (!this.modelOverriders.containsKey(rl) || replaceIfHas)) {
            this.modelOverriders.put(rl, (IOverriderGetModel) overrider);
            System.out.println("Added overrider get model! " + overrider);
        }
    }

    public void addToBlackList(ShapelessRL rl) {
        if (rl == null) {
            return;
        }
        if (!this.renderBlackList.contains(rl)) {
            this.renderBlackList.add(rl);
        }
    }

    public void addToBlackList(String[] rls) {
        if (rls == null) {
            return;
        }
        for (String str : rls) {
            ShapelessRL rl = null;
            try {
                rl = new ShapelessRL(str);
            } catch (Throwable t) {
                t.printStackTrace(System.err);
            }
            if (rl != null && !rl.isEmpty()) {
                this.addToBlackList(rl);
            }
        }
    }

    public static class DefaultRender implements IOverriderRender {
        private final Minecraft mc = Minecraft.getMinecraft();

        @Override
        public void render(ModelBase model, IBodeThing hand, ResourceLocation tex, EnumHandSide handSide, ItemStack chestPlate, ItemArmor itemArmor, EnumRenderType type) {
            if (hand == null || tex == null) {
                return;
            }
            switch (type) {
                case ARMOR_ENCHANT:
                    if (chestPlate.hasEffect()) this.renderEnchant(hand, tex, handSide);
                    break;
                case ARM_WEAR:
                    if (!AAConfig.disableArmWear) this.render(hand, tex, handSide);
                    break;
                case ARMOR:
                    this.renderArmor(hand, tex, handSide, chestPlate, itemArmor);
                    break;
                default:
                    this.render(hand, tex, handSide);
            }
        }

        private void renderArmor(IBodeThing hand, ResourceLocation tex, EnumHandSide handSide, ItemStack armor, ItemArmor itemArmor) {
            if (itemArmor.hasOverlay(armor)) {
                int i = itemArmor.getColor(armor);
                float r = (float) (i >> 16 & 255) / 255.0F;
                float g = (float) (i >> 8 & 255) / 255.0F;
                float b = (float) (i & 255) / 255.0F;
                GlStateManager.color(r, g, b, 1.0F);
                this.render(hand, tex, handSide);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            } else {
                this.render(hand, tex, handSide);
            }
        }

        private void render(IBodeThing hand, ResourceLocation tex, EnumHandSide handSide) {
            this.mc.getTextureManager().bindTexture(tex);
            hand.setRotation(0.0F, 0.0F, 0.1F * this.getHadSideDelta(handSide));
            hand.render(0.0625F);
        }

        private void renderEnchant(IBodeThing hand, ResourceLocation tex, EnumHandSide handSide) {
            float f = (float) this.mc.player.ticksExisted;
            GlStateManager.pushMatrix();
            GlStateManager.pushAttrib();
            this.mc.getTextureManager().bindTexture(tex);
            this.mc.entityRenderer.setupFogColor(true);
            GlStateManager.enableBlend();
            GlStateManager.depthFunc(514);
            GlStateManager.depthMask(false);
            GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0F);

            for (int i = 0; i < 2; ++i) {
                GlStateManager.disableLighting();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
                GlStateManager.color(0.38F, 0.19F, 0.608F, 1.0F);
                GlStateManager.matrixMode(5890);
                GlStateManager.loadIdentity();
                GlStateManager.scale(0.33333334F, 0.33333334F, 0.33333334F);
                GlStateManager.rotate(30.0F - (float)i * 60.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.translate(0.0F, f * (0.001F + (float)i * 0.003F) * 20.0F, 0.0F);
                GlStateManager.matrixMode(5888);
                hand.setRotation(0.0F, 0.0F, 0.1F * this.getHadSideDelta(handSide));
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
            this.mc.entityRenderer.setupFogColor(false);
            GlStateManager.popMatrix();
            GlStateManager.popAttrib();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        }

        private int getHadSideDelta(EnumHandSide handSide) {
            switch (handSide) {
                case RIGHT:
                    return 1;
                case LEFT:
                    return -1;
                default:
                    return 0;
            }
        }
    }

    public static class DefaultModelGetter implements IOverriderGetModel {
        private final ModelBiped finalModel = new ModelBiped(1.0F);
        private ModelBiped defaultModel = new ModelBiped((float) AAConfig.vanillaArmorModelSize);
        private double modelSize = AAConfig.vanillaArmorModelSize;
        private IBodeThing[] hands = null;

        @Override
        public ModelBase getModel(AbstractClientPlayer player, ItemArmor itemArmor, ItemStack stack) {
            ModelBiped mb = itemArmor.getArmorModel(player, stack, EntityEquipmentSlot.CHEST, this.finalModel);
            if (mb == null) {
                if (this.modelSize != AAConfig.vanillaArmorModelSize) {
                    this.defaultModel = new ModelBiped((float) AAConfig.vanillaArmorModelSize);
                    this.modelSize = AAConfig.vanillaArmorModelSize;
                }
                mb = this.defaultModel;
            }
            this.hands = new IBodeThing[] {
                new BoneThingModelRender(mb.bipedRightArm),
                new BoneThingModelRender(mb.bipedLeftArm)
            };
            return mb;
        }

        @Override
        public IBodeThing getArm(ModelBase mb, ItemArmor itemArmor, ItemStack stack, EnumHandSide handSide) {
            switch (handSide) {
                case RIGHT:
                    return this.hands[0];
                case LEFT:
                    return this.hands[1];
                default:
                    return null;
            }
        }
    }

    public static class DefaultTextureGetter implements IOverriderGetTex {

        @Override
        public ResourceLocation getTexture(AbstractClientPlayer player, LayerBipedArmor armorLayer, List<LayerRenderer<AbstractClientPlayer>> layerRenderers, ItemStack chestPlate, ItemArmor itemArmor, EnumModelTexType type) {
            switch (type) {
                case NULL:
                    return armorLayer.getArmorResource(player, chestPlate, EntityEquipmentSlot.CHEST, null);
                case OVERLAY:
                    if (itemArmor.hasOverlay(chestPlate)) return armorLayer.getArmorResource(player, chestPlate, EntityEquipmentSlot.CHEST, "overlay");
            }
            return null;
        }
    }

    public static class BoneThingModelRender implements IBodeThing {
        private ModelRenderer mr;
        public BoneThingModelRender(ModelRenderer mr) {
            this.mr = mr;
        }

        @Override
        public void setRotation(float x, float y, float z) {
            this.mr.rotateAngleX = x;
            this.mr.rotateAngleY = y;
            this.mr.rotateAngleZ = z;
        }

        @Override
        public void render(float scale) {
            this.mr.render(scale);
        }
    }

    public static class ModelPlayerIBone {
        public final ModelPlayer model;
        public final IBodeThing bipedRightArmwear;
        public final IBodeThing bipedLeftArmwear;
        public final IBodeThing bipedRightArm;
        public final IBodeThing bipedLeftArm;
        public ModelPlayerIBone(ModelPlayer model) {
            this.model = model;

            this.bipedRightArmwear = new BoneThingModelRender(model.bipedRightArmwear);
            this.bipedLeftArmwear = new BoneThingModelRender(model.bipedLeftArmwear);
            this.bipedRightArm = new BoneThingModelRender(model.bipedRightArm);
            this.bipedLeftArm = new BoneThingModelRender(model.bipedLeftArm);
        }
    }
}
