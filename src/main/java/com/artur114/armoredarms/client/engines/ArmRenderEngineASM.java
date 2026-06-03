package com.artur114.armoredarms.client.engines;

import com.artur114.armoredarms.api.events.InitRenderLayersEvent;
import com.artur114.armoredarms.asm.ASMHooksOut;
import com.artur114.armoredarms.client.layers.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.layers.ArmRenderLayerHand;
import com.artur114.armoredarms.client.pipelines.ArmRenderPipelineASM;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;
import com.artur114.armoredarms.core.util.IAAModContainer;
import org.lwjgl.opengl.GL11;

import java.util.Map;

public class ArmRenderEngineASM extends AbstractRenderEngineForge<ArmRenderEngineASM, ArmRenderPipelineASM>{
    @Override
    protected Map<Class<? extends IArmRenderLayer<?>>, IArmRenderLayer<?>> initLayers() {
        InitRenderLayersEvent event = new InitRenderLayersEvent(ArmRenderEngineForge.class, this.mod);
        event.registerLayer(ArmRenderLayerArmor.class);
        event.registerLayer(ArmRenderLayerHand.class);
        this.mod.post(event);
        return event.result();
    }

    @Override
    public void render(ArmRenderPipelineASM context) {
        if (ASMHooksOut.currentHandSide() == EnumHandSideAA.LEFT) {
            GL11.glRotatef(-0.1F * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
        }
        this.renderAllLayers(EnumHandSideAA.RIGHT);

        if (!this.sortedLayers.isEmpty()) {
            context.renderContext.setCanceled(true);
        }
    }

    @Override
    public void tick(ArmRenderPipelineASM context) {
        this.render = this.updateAllLayers();
    }

    @Override
    public boolean canWork(IAAModContainer mod) {
        return true;
    }

    @Override
    public Class<ArmRenderPipelineASM> targetPipeline() {
        return ArmRenderPipelineASM.class;
    }

    @Override
    public IPriority priority() {
        return Priority.NORMAL;
    }
}
