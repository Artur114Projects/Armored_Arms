package com.artur114.armoredarms.api.events;

import com.artur114.armoredarms.core.api.event.IAbstractGrabModelManagersEvent;
import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelManager;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.SLContainer;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.List;

public class InitModelManagersEvent extends Event implements IAbstractGrabModelManagersEvent {
    private final List<SLContainer<IArmModelManager<?, ?>>> managersSL;
    private final Class<? extends IArmRenderLayer<?>> layer;
    private final List<IArmModelManager<?, ?>> managers;
    private final IAAModContainer mod;

    public InitModelManagersEvent(Class<? extends IArmRenderLayer<?>> layer, IAAModContainer mod, boolean located) {
        this.layer = layer;
        this.mod = mod;

        if (located) {
            this.managersSL = new ArrayList<>();
            this.managers = null;
        } else {
            this.managers = new ArrayList<>();
            this.managersSL = null;
        }
    }

    @Override
    public List<IArmModelManager<?, ?>> managers() {
        return this.managers;
    }

    @Override
    public List<SLContainer<IArmModelManager<?, ?>>> managersSL() {
        return this.managersSL;
    }

    @Override
    public IAAModContainer mod() {
        return this.mod;
    }

    @Override
    public Class<? extends IArmRenderLayer<?>> layer() {
        return this.layer;
    }
}