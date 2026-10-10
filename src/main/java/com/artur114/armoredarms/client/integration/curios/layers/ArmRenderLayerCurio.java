package com.artur114.armoredarms.client.integration.curios.layers;

import com.artur114.armoredarms.api.events.InitModelManagersEvent;
import com.artur114.armoredarms.api.events.InitRenderContainersEvent;
import com.artur114.armoredarms.client.engines.AbstractRenderEngineForge;
import com.artur114.armoredarms.client.integration.geckolib.modelrender.PSGeoBone;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelContainerArmor;
import com.artur114.armoredarms.client.modelrender.armor.ArmModelRendererArmor;
import com.artur114.armoredarms.client.util.ArmRenderContext;
import com.artur114.armoredarms.client.util.EnumMods;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.layer.AbstractArmorRenderLayer;
import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelManager;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.SLContainer;
import com.artur114.armoredarms.core.util.ShapelessLocation;
import com.artur114.armoredarms.main.AAConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.List;

public class ArmRenderLayerCurio implements IArmRenderLayer<AbstractRenderEngineForge<?, ?>> {
    public final Minecraft mc = Minecraft.getInstance();
    public AbstractRenderEngineForge<?, ?> engine;
    private boolean isDeactivated = false;

    @Override
    public void update(AbstractRenderEngineForge<?, ?> engine) {}

    @Override
    public void render(AbstractRenderEngineForge<?, ?> engine, EnumHandSideAA handSide) {
        PoseStack matrixStack = engine.renderContext.poseStack;
        matrixStack.pushPose();
        CuriosApi.getCuriosInventory(this.mc.player).ifPresent((handler) -> handler.getCurios().forEach((id, stacksHandler) -> {
            IDynamicStackHandler stackHandler = stacksHandler.getStacks();
            IDynamicStackHandler cosmeticStacksHandler = stacksHandler.getCosmeticStacks();

            for(int i = 0; i < stackHandler.getSlots(); ++i) {
                ItemStack stack = cosmeticStacksHandler.getStackInSlot(i);
                boolean cosmetic = true;
                NonNullList<Boolean> renderStates = stacksHandler.getRenders();
                boolean renderable = renderStates.size() > i && (Boolean) renderStates.get(i);
                if (stack.isEmpty() && renderable) {
                    stack = stackHandler.getStackInSlot(i);
                    cosmetic = false;
                }

                if (!stack.isEmpty()) {
                    SlotContext slotContext = new SlotContext(id, this.mc.player, i, cosmetic, renderable);
                    ItemStack finalStack = stack;
                    CuriosRendererRegistry.getRenderer(stack.getItem()).ifPresent((renderer) -> {
                        if (renderer instanceof ICurioRenderer.ModelRender<?> modelRender) {
                            Model rawModel = modelRender.getModel(finalStack, slotContext);

                            if (EnumMods.GECKO_LIB.isLoaded() && rawModel instanceof GeoArmorRenderer<?>) {
                                this.renderGeo((GeoArmorRenderer<?>) rawModel, matrixStack, handSide, finalStack);
                            } else if (rawModel instanceof HumanoidModel<?> model) {
                                ModelPart arm = handSide.sided(model.rightArm, model.leftArm);
                                MultiBufferSource renderTypeBuffer = engine.renderContext.multiBufferSource;

                                RenderType renderType = model.renderType(modelRender.getModelTexture(finalStack, slotContext));
                                VertexConsumer vertexConsumer;
                                if (finalStack.hasFoil()) {
                                    vertexConsumer = VertexMultiConsumer.create(renderTypeBuffer.getBuffer(RenderType.entityGlint()), renderTypeBuffer.getBuffer(renderType));
                                } else {
                                    vertexConsumer = renderTypeBuffer.getBuffer(renderType);
                                }

                                engine.mainBones().bySide(handSide).injectTo(arm);
                                boolean s = arm.skipDraw;
                                boolean v = arm.visible;
                                arm.skipDraw = false;
                                arm.visible = true;
                                arm.render(matrixStack, vertexConsumer, engine.renderContext.packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                                arm.skipDraw = s;
                                arm.visible = v;
                            }
                        }
                    });
                }
            }

        }));
        matrixStack.popPose();
    }

    private <T extends Item & GeoItem> void renderGeo(GeoArmorRenderer<?> rawModel, PoseStack matrixStack, EnumHandSideAA side, ItemStack stack) {
        GeoArmorRenderer<T> model = (GeoArmorRenderer<T>) rawModel;
        T item = (T) stack.getItem();

        GeoBone arm = side.sided(model.getRightArmBone(), model.getLeftArmBone());
        MultiBufferSource renderTypeBuffer = engine.renderContext.multiBufferSource;

        RenderType renderType = model.renderType(model.getTextureLocation(item));
        VertexConsumer vertexConsumer;
        if (stack.hasFoil()) {
            vertexConsumer = VertexMultiConsumer.create(renderTypeBuffer.getBuffer(RenderType.entityGlint()), renderTypeBuffer.getBuffer(renderType));
        } else {
            vertexConsumer = renderTypeBuffer.getBuffer(renderType);
        }

        PSGeoBone bone = new PSGeoBone();
        bone.stack = matrixStack;
        bone.side = side;
        bone.arm = arm;

        engine.mainBones().bySide(side).injectTo(bone);
        model.renderRecursively(matrixStack, item, arm, renderType, renderTypeBuffer, vertexConsumer, false, Minecraft.getInstance().getPartialTick(), engine.renderContext.packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void init(AbstractRenderEngineForge<?, ?> engine, IAAModContainer mod) {
        this.engine = engine;
    }

    @Override
    public boolean needRender(AbstractRenderEngineForge<?, ?> engine, boolean renderEngineState) {
        return true;
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
        return this.isDeactivated;
    }

    @Override
    public void deactivate() {
        this.isDeactivated = true;
    }

    @Override
    public IPriority priority() {
        return Priority.NORMAL;
    }
}
