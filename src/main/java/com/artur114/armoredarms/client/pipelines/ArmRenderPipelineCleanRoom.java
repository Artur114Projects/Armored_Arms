package com.artur114.armoredarms.client.pipelines;

import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.api.pipeline.AbstractRenderPipeline;
import com.artur114.armoredarms.core.util.EnumExceptionType;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.Reflector;
import com.artur114.armoredarms.core.util.RenderException;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ArmRenderPipelineCleanRoom extends AbstractRenderPipeline<ArmRenderPipelineCleanRoom> {
    public final Minecraft mc = Minecraft.getMinecraft();
    public TickEvent.ClientTickEvent tickContext = null;
    public RenderArmEvent renderContext = null;
    public boolean deactivated = false;
    public boolean initTick = true;

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void renderHand(RenderArmEvent e) {
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
                this.init(); this.initTick = false;
            } catch (RenderException re) {
                this.mod.processException(re.setType(EnumExceptionType.FATAL));
            } catch (Throwable exp) {
                this.mod.processException(new RenderException("It was not possible to load RenderPipeline, custom hands will not be rendered!", exp).setComponent(this).setType(EnumExceptionType.FATAL));
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
        this.engine.init(this, mod);
    }

    @Override
    protected void register(IAAModContainer mod, IArmRenderEngine<ArmRenderPipelineCleanRoom> engine) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public boolean canWork(IAAModContainer mod) {
        return Reflector.isClassExists("net.minecraftforge.client.event.RenderArmEvent");
    }

    @Override
    public Class<ArmRenderPipelineCleanRoom> clazz() {
        return ArmRenderPipelineCleanRoom.class;
    }

    @Override
    public void deactivate() {
        this.deactivated = true;
    }

    @Override
    public IPriority priority() {
        return Priority.HIGH;
    }
}
