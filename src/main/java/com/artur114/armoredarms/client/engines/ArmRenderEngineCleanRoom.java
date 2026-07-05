package com.artur114.armoredarms.client.engines;

import com.artur114.armoredarms.api.events.ArmLayerRenderingEvent;
import com.artur114.armoredarms.api.events.InitBoneAdaptersEvent;
import com.artur114.armoredarms.api.events.InitRenderLayersEvent;
import com.artur114.armoredarms.client.layers.ArmRenderLayerArmor;
import com.artur114.armoredarms.client.layers.ArmRenderLayerHand;
import com.artur114.armoredarms.client.modelrender.BoneAdapterModelRenderer;
import com.artur114.armoredarms.client.pipelines.ArmRenderPipelineCleanRoom;
import com.artur114.armoredarms.client.pipelines.ArmRenderPipelineForge;
import com.artur114.armoredarms.client.util.AAUtils;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;
import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;
import com.artur114.armoredarms.core.util.IBoneAdapter;
import com.artur114.armoredarms.core.util.RenderException;
import net.minecraftforge.common.MinecraftForge;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ArmRenderEngineCleanRoom extends AbstractRenderEngineForge<ArmRenderEngineCleanRoom, ArmRenderPipelineCleanRoom>{
    @Override
    protected Map<Class<? extends IArmRenderLayer<?>>, IArmRenderLayer<?>> initLayers() {
        InitRenderLayersEvent event = new InitRenderLayersEvent(ArmRenderEngineCleanRoom.class, this.mod);
        event.registerLayer(ArmRenderLayerArmor.class);
        event.registerLayer(ArmRenderLayerHand.class);
        this.mod.post(event);
        return event.result();
    }

    @Override
    public boolean onLayerRendering(IArmRenderLayer<ArmRenderEngineCleanRoom> layer, EnumHandSideAA side) {
        return !MinecraftForge.EVENT_BUS.post(new ArmLayerRenderingEvent(layer, side));
    }

    @Override
    protected List<IBoneAdapter<?>> initBoneAdapters() {
        InitBoneAdaptersEvent event = new InitBoneAdaptersEvent(this.mod);
        event.registerAdapter(new BoneAdapterModelRenderer());
        this.mod.post(event);
        return event.adaptersList();
    }

    @Override
    public void render(ArmRenderPipelineCleanRoom context) {
        this.renderAllLayers(AAUtils.fromMc(context.renderContext.getArm()));

        if (!this.sortedLayers.isEmpty()) {
            context.renderContext.setCanceled(true);
        }
    }

    @Override
    public void tick(ArmRenderPipelineCleanRoom context) {
        this.render = this.updateAllLayers();
    }

    @Override
    public Class<ArmRenderPipelineCleanRoom> targetPipeline() {
        return ArmRenderPipelineCleanRoom.class;
    }

    @Override
    public IPriority priority() {
        return Priority.NORMAL;
    }
}
