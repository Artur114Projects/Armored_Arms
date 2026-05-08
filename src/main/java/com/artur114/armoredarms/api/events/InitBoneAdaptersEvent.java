package com.artur114.armoredarms.api.events;

import com.artur114.armoredarms.core.api.event.IAbstractGrabBoneAdaptersEvent;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.IBoneAdapter;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InitBoneAdaptersEvent extends Event implements IAbstractGrabBoneAdaptersEvent {
    private final Map<Class<?>, IBoneAdapter<?>> adapterMap = new HashMap<>();
    private final IAAModContainer mod;

    public InitBoneAdaptersEvent(IAAModContainer mod) {
        this.mod = mod;
    }

    @Override
    public Map<Class<?>, IBoneAdapter<?>> adapters() {
        return this.adapterMap;
    }

    @Override
    public List<IBoneAdapter<?>> adaptersList() {
        return new ArrayList<>(this.adapterMap.values());
    }

    @Override
    public IAAModContainer mod() {
        return this.mod;
    }
}
