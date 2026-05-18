package com.artur114.armoredarms.client.pipelines;

import com.artur114.armoredarms.client.mixin.RenderArmMixinEvent;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.util.EnumExceptionType;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.Reflector;
import com.artur114.armoredarms.core.util.RenderException;
import com.artur114.armoredarms.main.AAConfig;
import com.artur114.armoredarms.main.ArmoredArms;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ArmRenderPipelineMixin extends AbstractRenderPipelineForge<ArmRenderPipelineMixin> {
    public int noRenderingTicks = 0;

    @SubscribeEvent()
    @OnlyIn(Dist.CLIENT)
    public void renderHand(RenderArmMixinEvent e) {
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

    public void populateContext(RenderArmMixinEvent e) {
        this.renderContext.multiBufferSource = e.getMultiBufferSource();
        this.renderContext.packedLight = e.getPackedLight();
        this.renderContext.poseStack = e.getPoseStack();
        this.renderContext.player = e.getPlayer();
        this.renderContext.arm = e.getArm();
    }

    public void postProcess(RenderArmMixinEvent e) {
        if (this.renderContext.isCanceled()) {
            e.setCanceled(true);
        }
        this.renderContext.reload();
        this.renderArgs.clear();
    }

    @Override
    protected void register(IAAModContainer mod, IArmRenderEngine<ArmRenderPipelineMixin> engine) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public boolean canWork(IAAModContainer mod) {
        return Reflector.isClassExists("org.spongepowered.asm.mixin.Mixin") && AAConfig.Baked.pipelinesPriority.containsKey(ArmRenderPipelineMixin.class);
    }

    @Override
    public Class<ArmRenderPipelineMixin> clazz() {
        return ArmRenderPipelineMixin.class;
    }
}
