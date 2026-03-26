package com.artur114.armoredarms.client.pipelines;

import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.util.*;
import com.artur114.armoredarms.core.util.RenderException;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ArmRenderPipelineForge extends AbstractRenderPipelineForge<ArmRenderPipelineForge> {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void renderHand(RenderArmEvent e) {
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

        if (this.initTick) {
            try {
                this.engine.init(this, this.mod); this.initTick = false;
            } catch (RenderException re) {
                this.mod.processException(re.setType(EnumExceptionType.FATAL));
            } catch (Throwable exp) {
                this.mod.processException(new RenderException(exp).setComponent(this).setType(EnumExceptionType.FATAL));
            }
        }

        try {
            this.engine.tryTick(this);
        } catch (RenderException re) {
            this.mod.processException(re);
        } catch (Throwable exp) {
            this.mod.processException(new RenderException(exp).setComponent(this.engine));
        }
    }

    public void populateContext(RenderArmEvent e) {
        this.renderContext.multiBufferSource = e.getMultiBufferSource();
        this.renderContext.arm = AAUtils.fromMc(e.getArm());
        this.renderContext.packedLight = e.getPackedLight();
        this.renderContext.poseStack = e.getPoseStack();
        this.renderContext.player = e.getPlayer();
    }

    public void postProcess(RenderArmEvent e) {
        if (this.renderContext.isCanceled()) {
            e.setCanceled(true);
        }
        this.renderContext.reload();
    }

    @Override
    protected void register(IAAModContainer mod, IArmRenderEngine<ArmRenderPipelineForge> engine) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public boolean canWork(IAAModContainer mod) {
        return true;
    }

    @Override
    public Class<ArmRenderPipelineForge> clazz() {
        return ArmRenderPipelineForge.class;
    }

    @Override
    public IPriority priority() {
        return Priority.NORMAL;
    }
}
