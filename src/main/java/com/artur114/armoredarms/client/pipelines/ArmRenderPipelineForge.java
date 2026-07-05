package com.artur114.armoredarms.client.pipelines;

import com.artur114.armoredarms.client.util.EnumMods;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.api.pipeline.AbstractRenderPipeline;
import com.artur114.armoredarms.core.util.EnumExceptionType;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.ObjectBuff;
import com.artur114.armoredarms.core.util.RenderException;
import com.artur114.armoredarms.main.AAConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ArmRenderPipelineForge extends AbstractRenderPipeline<ArmRenderPipelineForge> {
    private final ObjectBuff renderArgs = new ObjectBuff();
    public final Minecraft mc = Minecraft.getMinecraft();
    public TickEvent.ClientTickEvent tickContext = null;
    public RenderHandEvent renderContext = null;
    public boolean deactivated = false;
    public boolean initTick = true;

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void renderHand(RenderHandEvent e) {
        if (this.deactivated) {
            return;
        }

        this.renderContext = e;

        try {
            this.engine.tryRender(this);
        } catch (RenderException re) {
            this.mod.processException(re);
        } catch (Throwable exp) {
            this.mod.processException(new RenderException(exp).setComponent(this.engine));
        }

        this.renderContext = null;
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void clientTick(TickEvent.ClientTickEvent e) {
        if (this.deactivated || e.phase != TickEvent.Phase.START || this.mc.player == null || this.mc.isGamePaused()) {
            return;
        }

        if (this.initTick) {
            try {
                this.init();
            } catch (RenderException re) {
                this.mod.processException(re); return;
            } catch (Throwable exp) {
                this.mod.processException(new RenderException(exp).setComponent(this).setType(EnumExceptionType.FATAL)); return;
            }
        }

        this.tickContext = e;

        try {
            this.engine.tryTick(this);
        } catch (RenderException re) {
            this.mod.processException(re);
        } catch (Throwable exp) {
            this.mod.processException(new RenderException(exp).setComponent(this.engine));
        }

        this.tickContext = null;
    }

    public void init() {
        this.initTick = false;
        this.engine.init(this, mod);
    }

    @Override
    protected void register(IAAModContainer mod, IArmRenderEngine<ArmRenderPipelineForge> engine) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public boolean canWork(IAAModContainer mod) {
        return AAConfig.Baked.pipelinesPriority.containsKey(this.clazz());
    }

    @Override
    public ObjectBuff renderArgs() {
        return this.renderArgs;
    }

    @Override
    public Class<ArmRenderPipelineForge> clazz() {
        return ArmRenderPipelineForge.class;
    }

    @Override
    public void deactivate() {
        this.deactivated = true;
    }

    @Override
    public boolean isDeactivated() {
        return this.deactivated;
    }

    @Override
    public IPriority priority() {
        return AAConfig.Baked.pipelinesPriority.get(this.clazz());
    }
}
