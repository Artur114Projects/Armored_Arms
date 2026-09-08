package com.artur114.armoredarms.client.pipelines;

import com.artur114.armoredarms.asm.out.RenderArmEvent;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.api.pipeline.AbstractRenderPipeline;
import com.artur114.armoredarms.core.util.EnumExceptionType;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.ObjectBuff;
import com.artur114.armoredarms.core.util.RenderException;
import com.artur114.armoredarms.main.AAConfig;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.MinecraftForge;

public class ArmRenderPipelineASM extends AbstractRenderPipeline<ArmRenderPipelineASM> {
    private final ObjectBuff renderArgs = new ObjectBuff();
    public final Minecraft mc = Minecraft.getMinecraft();
    public TickEvent.ClientTickEvent tickContext = null;
    public RenderArmEvent renderContext = null;

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
        if (this.deactivated || e.phase != TickEvent.Phase.START || this.mc.thePlayer == null || this.mc.isGamePaused()) {
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

    @Override
    protected void register(IAAModContainer mod, IArmRenderEngine<ArmRenderPipelineASM> engine) {
        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public boolean canWork(IAAModContainer mod) {
        return AAConfig.Baked.pipelinesPriority.containsKey(ArmRenderPipelineASM.class);
    }

    @Override
    public ObjectBuff renderArgs() {
        return this.renderArgs;
    }

    @Override
    public Class<ArmRenderPipelineASM> clazz() {
        return ArmRenderPipelineASM.class;
    }

    @Override
    public IPriority priority() {
        return AAConfig.Baked.pipelinesPriority.get(ArmRenderPipelineASM.class);
    }
}
