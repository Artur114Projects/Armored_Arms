package com.artur114.armoredarms.api.events;

import com.artur114.armoredarms.core.api.event.IAbstractGrabBoneAdaptersEvent;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.IBoneAdapter;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.*;

public class InitBoneAdaptersEvent extends Event implements IAbstractGrabBoneAdaptersEvent {
    private final Map<Class<?>, IBoneAdapter<?>> map = new HashMap<>();
    private final IAAModContainer mod;

    public InitBoneAdaptersEvent(IAAModContainer mod) {
        this.mod = mod;
    }

    @Override
    public Map<Class<?>, IBoneAdapter<?>> adapters() {
        return this.map;
    }

    @Override
    public List<IBoneAdapter<?>> adaptersList() {
        return new ArrayList<>(this.map.values());
    }

    @Override
    public IAAModContainer mod() {
        return this.mod;
    }
}
