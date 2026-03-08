package com.artur114.armoredarms.implement.main;

import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;
import com.artur114.armoredarms.core.api.pipeline.IArmRenderPipeline;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.IEvent;
import com.artur114.armoredarms.core.util.RenderException;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.Collection;
import java.util.Collections;

@Mod(modid = ArmoredArms.MODID, useMetadata = true, clientSideOnly = true)
public class ArmoredArms implements IAAModContainer {
    public static final String MODID = "armoredarms";



    @Override
    public Collection<IArmRenderPipeline<?>> defaultPipelines() {
        return Collections.emptyList();
    }

    @Override
    public Collection<IArmRenderEngine<?>> defaultEngines() {
        return Collections.emptyList();
    }

    @Override
    public void processException(RenderException exp) {

    }

    @Override
    public boolean isModLoaded(String modId) {
        return Loader.isModLoaded(modId);
    }

    @Override
    public <R> R post(IEvent<R> event) {
        if (event instanceof Event) {
            MinecraftForge.EVENT_BUS.post((Event) event);
            return event.result();
        }
        return null;
    }

}
