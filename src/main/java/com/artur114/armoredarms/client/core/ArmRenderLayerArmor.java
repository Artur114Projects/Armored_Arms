package com.artur114.armoredarms.client.core;

import com.artur114.armoredarms.api.*;
import com.artur114.armoredarms.api.events.InitArmorRenderLayerEvent;
import com.artur114.armoredarms.client.util.EnumHandSide;
import com.artur114.armoredarms.client.util.MiscUtils;
import com.artur114.armoredarms.client.util.RMException;
import com.artur114.armoredarms.client.util.ShapelessRL;
import com.artur114.armoredarms.main.AAConfig;
import com.artur114.armoredarms.main.ArmoredArms;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

@SideOnly(Side.CLIENT)
public class ArmRenderLayerArmor implements IArmRenderLayer {
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    public final DefaultTextureGetter defaultTextureGetter = new DefaultTextureGetter();
    public final DefaultModelGetter defaultModelGetter = new DefaultModelGetter();
    public Map<ShapelessRL, IOverriderGetModel> modelOverriders = null;
    public Map<ShapelessRL, IOverriderGetTex> textureOverriders = null;
    public Map<ShapelessRL, IOverriderRender> renderOverriders = null;
    public final DefaultRender defaultRender = new DefaultRender();
    public final Minecraft mc = Minecraft.getMinecraft();
    public List<ShapelessRL> renderBlackList = null;
    public Set<Item> killingArmor = new HashSet<>();
    public RenderPlayer renderPlayer = null;
    public boolean render = false;


    public ItemStack chestPlate = null;
    public ItemArmor chestPlateItem = null;
    public IModelOnlyArms currentArmorModel = null;
    public ResourceLocation currentArmorTex = null;
    public ResourceLocation currentArmorTexOv = null;
    public IOverriderGetTex currentGetTexOverrider = null;
    public IOverriderRender currentRenderOverrider = null;
    public IOverriderGetModel currentGetModelOverrider = null;

    @Override
    public void update(AbstractClientPlayer player) {
        try {
            this.tryTick(player);
        } catch (RMException rm) {
            this.render = false;
            this.killingArmor.add(this.chestPlateItem);
            throw rm.setMessage("armoredarms.error.layer.armor");
        } catch (Throwable tr) {
            this.render = false;
            this.killingArmor.add(this.chestPlateItem);
            throw new RMException(tr).setMessage("armoredarms.error.layer.armor");
        }
    }

    @Override
    public void renderTransformed(AbstractClientPlayer player, EnumHandSide side) {
        try {
            this.tryRender(player, side);
        } catch (RMException rm) {
            this.render = false;
            this.killingArmor.add(this.chestPlateItem);
            throw rm.setMessage("armoredarms.error.layer.armor");
        } catch (Throwable tr) {
            this.render = false;
            this.killingArmor.add(this.chestPlateItem);
            throw new RMException(tr).setMessage("armoredarms.error.layer.armor");
        }
    }

    @Override
    public boolean needRender(AbstractClientPlayer player, boolean renderManagerState) {
        return this.render;
    }

    @Override
    public void init(AbstractClientPlayer player) {
        this.renderPlayer = this.initRenderPlayer(player);

        this.initEvent();
    }

    public void tryTick(AbstractClientPlayer player) {
        ItemStack chestPlate = this.itemStackArmor(player);

        if (chestPlate == null || this.killingArmor.contains(chestPlate.getItem()) || !(chestPlate.getItem() instanceof ItemArmor)) {
            this.chestPlate = null;
            this.chestPlateItem = null;
            this.render = false;
            return;
        }

        if (this.checkIsNotNew(chestPlate)) {
            return;
        }

        if (this.renderBlackList.contains(new ShapelessRL(Item.itemRegistry.getNameForObject(chestPlate.getItem())))) {
            this.chestPlateItem = (ItemArmor) chestPlate.getItem();
            this.chestPlate = chestPlate;
            this.render = false;
            return;
        }

        this.render = true;
        this.chestPlate = chestPlate;
        this.chestPlateItem = (ItemArmor) chestPlate.getItem();

        this.updateOverriders();

        this.currentArmorModel = this.getArmorModel(player);
        this.currentArmorTexOv = this.getArmorTex(player, IOverriderGetTex.EnumTexType.OVERLAY);
        this.currentArmorTex = this.getArmorTex(player, IOverriderGetTex.EnumTexType.NULL);
    }

    public void tryRender(AbstractClientPlayer player, EnumHandSide side) {
        ResourceLocation armorTexOv = this.currentArmorTexOv;
        IModelOnlyArms armorModel = this.currentArmorModel;
        ResourceLocation armorTex = this.currentArmorTex;

        this.render(armorModel, side, armorTexOv, IOverriderRender.EnumRenderType.ARMOR_OVERLAY);
        this.render(armorModel, side, armorTex, IOverriderRender.EnumRenderType.ARMOR);

        this.render(armorModel, side, RES_ITEM_GLINT, IOverriderRender.EnumRenderType.ARMOR_ENCHANT);
    }

    public ItemStack itemStackArmor(AbstractClientPlayer player) {
//        if (Loader.isModLoaded("cosmeticarmorreworked")) {
//            CAStacksBase stacks = CosArmorAPI.getCAStacksClient(player.getUniqueID());
//            int chestId = EntityEquipmentSlot.CHEST.getIndex();
//
//            if (stacks.isSkinArmor(chestId)) {
//                return ItemStack.EMPTY;
//            }
//
//            ItemStack stack = stacks.getStackInSlot(chestId);
//
//            if (!stack.isEmpty()) {
//                return stack;
//            }
//        }

        return player.getCurrentArmor(ArmoredArms.CHEST_PLATE_ID);
    }

    private void initEvent() {
        InitArmorRenderLayerEvent event = new InitArmorRenderLayerEvent();
        MinecraftForge.EVENT_BUS.post(event);

        this.textureOverriders = event.textureOverriders();
        this.renderOverriders = event.renderOverriders();
        this.modelOverriders = event.modelOverriders();

        this.renderBlackList = event.renderBlackList();
    }

    public RenderPlayer initRenderPlayer(AbstractClientPlayer player) {
        return (RenderPlayer) RenderManager.instance.getEntityRenderObject(player);
    }

    public void updateOverriders() {
        ShapelessRL rl = new ShapelessRL(Item.itemRegistry.getNameForObject(this.chestPlate.getItem()));

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

    public boolean checkIsNotNew(ItemStack stack) {
        if (AAConfig.useCheckByItem) {
            return stack.getItem() == this.chestPlateItem;
        } else {
            return stack == this.chestPlate;
        }
    }

    public void render(IModelOnlyArms arms, EnumHandSide side, ResourceLocation tex, IOverriderRender.EnumRenderType type) {
        this.currentRenderOverrider.render(arms, side, tex, this.chestPlate, this.chestPlateItem, type);
    }

    public IModelOnlyArms getArmorModel(AbstractClientPlayer player) {
        return this.currentGetModelOverrider.getModel(player, this.chestPlateItem, this.chestPlate);
    }

    public ResourceLocation getArmorTex(AbstractClientPlayer player, IOverriderGetTex.EnumTexType type) {
        return this.currentGetTexOverrider.getTexture(player, this.chestPlate, this.chestPlateItem, type);
    }

    public static class DefaultRender implements IOverriderRender {
        private final Minecraft mc = Minecraft.getMinecraft();

        @Override
        public void render(IModelOnlyArms arms, EnumHandSide side, ResourceLocation tex, ItemStack stackArmor, ItemArmor itemArmor, EnumRenderType type) {
            if (arms == null || tex == null) {
                return;
            }
            switch (type) {
                case ARMOR_ENCHANT:
                    this.renderEnchant(arms, side, tex, stackArmor, itemArmor);
                    break;
                case ARMOR:
                    this.renderArmor(arms, side, tex, stackArmor, itemArmor);
                    break;
                case ARMOR_OVERLAY:
                    this.render(arms, side, tex, stackArmor, itemArmor);
                    break;
            }
        }

        private void renderArmor(IModelOnlyArms arms, EnumHandSide side, ResourceLocation tex, ItemStack stackArmor, ItemArmor itemArmor) {
            int i = itemArmor.getColor(stackArmor);
            if (i != -1) {
                float r = (float) (i >> 16 & 255) / 255.0F;
                float g = (float) (i >> 8 & 255) / 255.0F;
                float b = (float) (i & 255) / 255.0F;
                GL11.glColor3f(r, g, b);
                this.render(arms, side, tex, stackArmor, itemArmor);
                GL11.glColor3f(1.0F, 1.0F, 1.0F);
            } else {
                this.render(arms, side, tex, stackArmor, itemArmor);
            }
        }

        private void render(IModelOnlyArms arms, EnumHandSide side, ResourceLocation tex, ItemStack stackArmor, ItemArmor itemArmor) {
            this.mc.getTextureManager().bindTexture(tex);
            arms.renderArm(this.mc.thePlayer, side, itemArmor, stackArmor);
        }

        private void renderEnchant(IModelOnlyArms arms, EnumHandSide side, ResourceLocation tex, ItemStack stackArmor, ItemArmor itemArmor) {
            if (!stackArmor.isItemEnchanted()) {
                return;
            }
            GL11.glPushMatrix();
            float f8 = (float) this.mc.thePlayer.ticksExisted;
            this.mc.getTextureManager().bindTexture(tex);
            GL11.glEnable(3042);
            float f9 = 0.5F;
            GL11.glColor4f(f9, f9, f9, 1.0F);
            GL11.glDepthFunc(514);
            GL11.glDepthMask(false);

            for (int k = 0; k < 2; ++k) {
                GL11.glDisable(2896);
                float f10 = 0.76F;
                GL11.glColor4f(0.5F * f10, 0.25F * f10, 0.8F * f10, 1.0F);
                GL11.glBlendFunc(768, 1);
                GL11.glMatrixMode(5890);
                GL11.glLoadIdentity();
                float f11 = f8 * (0.001F + (float) k * 0.003F) * 20.0F;
                float f12 = 0.33333334F;
                GL11.glScalef(f12, f12, f12);
                GL11.glRotatef(30.0F - (float) k * 60.0F, 0.0F, 0.0F, 1.0F);
                GL11.glTranslatef(0.0F, f11, 0.0F);
                GL11.glMatrixMode(5888);
                arms.renderArm(this.mc.thePlayer, side, itemArmor, stackArmor);
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glMatrixMode(5890);
            GL11.glDepthMask(true);
            GL11.glLoadIdentity();
            GL11.glMatrixMode(5888);
            GL11.glEnable(2896);
            GL11.glDisable(3042);
            GL11.glDepthFunc(515);
            GL11.glPopMatrix();
        }
    }

    public static class DefaultModelGetter implements IOverriderGetModel {
        private ModelBiped defaultModel = new ModelBiped((float) AAConfig.vanillaArmorModelSize);
        private double modelSize = AAConfig.vanillaArmorModelSize;
        private Function<ModelBiped, IModelOnlyArms> factory;

        public DefaultModelGetter() {
            this.factory = (DefaultModelOnlyArms::new);
        }

        @Override
        public IModelOnlyArms getModel(AbstractClientPlayer player, ItemArmor itemArmor, ItemStack stack) {
            ModelBiped mb = ForgeHooksClient.getArmorModel(player, stack, ArmoredArms.CHEST_PLATE_ID, this.defaultModel);
            if (mb == null) {
                if (this.modelSize != AAConfig.vanillaArmorModelSize) {
                    this.defaultModel = new ModelBiped((float) AAConfig.vanillaArmorModelSize);
                    this.modelSize = AAConfig.vanillaArmorModelSize;
                }
                mb = this.defaultModel;
            }
            return this.factory.apply(mb);
        }

        protected void setFactory(Function<ModelBiped, IModelOnlyArms> factory) {
            this.factory = factory;
        }
    }

    public static class DefaultTextureGetter implements IOverriderGetTex {

        @Override
        public ResourceLocation getTexture(AbstractClientPlayer player, ItemStack chestPlate, ItemArmor itemArmor, EnumTexType type) {
            switch (type) {
                case NULL:
                    return RenderBiped.getArmorResource(player, chestPlate, ArmoredArms.CHEST_PLATE_ID, null);
                case OVERLAY:
                    if (itemArmor.getColor(chestPlate) != -1) return RenderBiped.getArmorResource(player, chestPlate, ArmoredArms.CHEST_PLATE_ID, "overlay");
            }
            return null;
        }
    }

    public static class DefaultModelOnlyArms implements IModelOnlyArms {
        public final ModelRenderer[] playerArms = MiscUtils.playerArms();
        public final ModelBiped mb;

        public DefaultModelOnlyArms(ModelBiped mb) {
            this.mb = mb;
        }

        @Override
        public void renderArm(AbstractClientPlayer player, EnumHandSide side, ItemArmor itemArmor, ItemStack stackArmor) {
            ModelRenderer arm = side.handFromModelBiped(this.mb);
            arm.rotationPointX = -5.0F * side.delta();
            arm.rotationPointY = 2.0F;
            arm.rotationPointZ = 0.0F;
            MiscUtils.setPlayerArmDataToArm(arm, this.playerArms[side.ordinal()]);
            boolean h = arm.isHidden;
            boolean s = arm.showModel;
            arm.isHidden = false;
            arm.showModel = true;
            arm.render(1.0F / 16.0F);
            arm.isHidden = h;
            arm.showModel = s;
        }

        @Override
        public ModelBiped original() {
            return mb;
        }
    }
}
