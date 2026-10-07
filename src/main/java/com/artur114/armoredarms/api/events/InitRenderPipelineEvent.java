package com.artur114.armoredarms.api.events;

import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.main.AAConfig;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class InitRenderPipelineEvent extends Event implements ICancellableEvent {
    private final IAAModContainer mod;

    public InitRenderPipelineEvent(IAAModContainer mod) {
        this.mod = mod;
    }

    public IAAModContainer mod() {
        return this.mod;
    }
}