package com.artur114.armoredarms.client.integration.punchy.pipeline;

import com.artur114.armoredarms.client.mixin.RenderArmPunchyMixinEvent;
import com.artur114.armoredarms.client.pipelines.AbstractRenderPipelineForge;
import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.client.util.EnumMods;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.api.pipeline.AbstractRenderPipeline;
import com.artur114.armoredarms.core.util.EnumExceptionType;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.RenderException;
import com.artur114.armoredarms.main.AAConfig;
import com.artur114.armoredarms.main.ArmoredArms;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ArmRenderPipelinePunchy extends AbstractRenderPipelineForge<ArmRenderPipelinePunchy> {
    public RenderArmPunchyMixinEvent context = null;
    public final Minecraft mc = Minecraft.getInstance();
    public int noRenderingTicks = 0;

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void renderHand(RenderArmPunchyMixinEvent e) {
        this.noRenderingTicks = 0;

        if (this.deactivated) {
            return;
        }

        this.populateContext(e);

        try {
            this.engine.tryRender(this);
        } catch (RenderException re) {
            this.mod.processException(re);
        } catch (Throwable exp) {
            this.mod.processException(new RenderException(exp).setComponent(this.engine));
        }

        this.postProcess(e);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void clientTick(TickEvent.ClientTickEvent e) {
        if (this.deactivated || e.phase != TickEvent.Phase.START || this.mc.player == null || this.mc.isPaused()) {
            return;
        }

        if (this.mc.player.getMainHandItem() != ItemStack.EMPTY) {
            this.noRenderingTicks = 0;
        }

        if (this.noRenderingTicks > 20 * 30) {
            ArmoredArms.LOGGER.AA_LOG.warn("The rendering method hasn't been called for 30 seconds! Is this normal?"); this.noRenderingTicks = 0;
        }

        if (this.initTick) {
            try {
                this.engine.init(this, this.mod); this.initTick = false;
            } catch (RenderException re) {
                this.mod.processException(re); return;
            } catch (Throwable exp) {
                this.mod.processException(new RenderException(exp).setComponent(this).setType(EnumExceptionType.FATAL)); return;
            }
        }

        try {
            this.engine.tryTick(this);
        } catch (RenderException re) {
            this.mod.processException(re);
        } catch (Throwable exp) {
            this.mod.processException(new RenderException(exp).setComponent(this.engine));
        }

        this.noRenderingTicks++;
    }


    public void populateContext(RenderArmPunchyMixinEvent e) {
        this.renderContext.multiBufferSource = e.buffer();
        this.renderContext.arm = AAUtils.fromMc(e.arm());
        this.renderContext.packedLight = e.combinedLight();
        this.renderContext.poseStack = e.poseStack();
        this.renderContext.player = e.player();
        this.context = e;
    }

    public void postProcess(RenderArmPunchyMixinEvent e) {
        if (this.renderContext.isCanceled()) {
            e.setCanceled(true);
        }
        this.renderContext.reload();
        this.context = null;
    }

    @Override
    protected void register(IAAModContainer mod, IArmRenderEngine<ArmRenderPipelinePunchy> engine) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public boolean canWork(IAAModContainer mod) {
        return super.canWork(mod) && EnumMods.PUNCHY.isLoaded();
    }

    @Override
    public Class<ArmRenderPipelinePunchy> clazz() {
        return ArmRenderPipelinePunchy.class;
    }
}
