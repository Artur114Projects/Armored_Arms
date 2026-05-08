package com.artur114.armoredarms.core.api.event;

import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.IBoneAdapter;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public interface IAbstractGrabBoneAdaptersEvent {
    Map<Class<?>, IBoneAdapter<?>> adapters();
    List<IBoneAdapter<?>> adaptersList();
    IAAModContainer mod();

    default void registerAdapter(IBoneAdapter<?> adapter) {
        this.adapters().put(adapter.targetObjectClass(), adapter);
    }
    default void registerAdapterIfModLoaded(Class<? extends IBoneAdapter<?>> clazz, String modid) {
        if (this.mod().isModLoaded(modid)) {
            Logger logger = mod().logger().namedLogger("ARMOREDARMS-CORE");
            try {
                IBoneAdapter<?> newAdapter = clazz.newInstance();
                IBoneAdapter<?> oldAdapter = this.adapters().get(newAdapter.targetObjectClass());
                if (oldAdapter == null || oldAdapter.priority().toInt() < newAdapter.priority().toInt()) {
                    this.adapters().put(newAdapter.targetObjectClass(), newAdapter);
                }
            } catch (Exception e) {
                logger.error("An error occurs while registering bone adapter");
                logger.error("  Adapter: {}", clazz);
                logger.error("  Stack trace: ", e);
            }
        }
    }
    default boolean hasAdapterFor(Class<?> clazz) {
        return this.adapters().containsKey(clazz);
    }
    default void removeAdapter(Class<?> clazz) {
        this.adapters().remove(clazz);
    }
    default void removeAdapter(IBoneAdapter<?> adapter) {
        this.adapters().values().removeIf(adapter::equals);
    }
}
