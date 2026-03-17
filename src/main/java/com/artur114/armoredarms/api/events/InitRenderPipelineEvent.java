package com.artur114.armoredarms.api.events;

import com.artur114.armoredarms.core.util.IAAModContainer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class InitRenderPipelineEvent extends Event {
    private final IAAModContainer mod;

    public InitRenderPipelineEvent(IAAModContainer mod) {
        this.mod = mod;
    }

    public IAAModContainer mod() {
        return this.mod;
    }
}
