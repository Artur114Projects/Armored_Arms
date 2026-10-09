package com.artur114.armoredarms.core.api.event;

import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelManager;
import com.artur114.armoredarms.core.api.modelrender.IArmModelRenderContainer;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.SLContainer;
import com.artur114.armoredarms.core.util.ShapelessLocation;
import org.apache.logging.log4j.Logger;

import java.util.List;

public interface IAbstractGrabRenderContainersEvent {

    List<IArmModelRenderContainer<?, ?>> containers();
    List<SLContainer<IArmModelRenderContainer<?, ?>>> containersSL();

    IAAModContainer mod();
    Class<? extends IArmRenderLayer<?>> layer();
    default boolean registerContainer(IArmModelRenderContainer<?, ?> container) {
        return this.registerContainer(container, ShapelessLocation.EMPTY);
    }
    default boolean registerContainer(String modId, String itemId, IArmModelRenderContainer<?, ?> container) {
        return this.registerContainer(container, ShapelessLocation.location(modId, itemId));
    }
    default boolean registerContainer(IArmModelRenderContainer<?, ?> container, ShapelessLocation location) {
        Logger logger = mod().logger().namedLogger("ARMOREDARMS-CORE");
        try {
            if (container.targetLayer().isAssignableFrom(this.layer())) {
                if (this.containersSL() != null && !location.isEmpty()) {
                    this.containersSL().add(new SLContainer<>(location, container));
                } else if (this.containers() != null) {
                    this.containers().add(container);
                } else {
                    logger.warn("Cannot register render container");
                    logger.warn("   Container: {}", container);
                    logger.warn("   Location: {}", location);
                    logger.warn("   Layer: {}", this.layer());
                    logger.warn("   Event containers list: {}", this.containers());
                    logger.warn("   Event SL containers list: {}", this.containersSL());
                }
                return true;
            }
        } catch (Exception e) {
            logger.error("An error occurs while registering render container");
            logger.error("  Container: {}", container);
            logger.error("  Stack trace: ", e);
        }
        return false;
    }

    default boolean registerContainerIfModLoaded(IArmModelRenderContainer<?, ?> container, String modId) {
        return this.registerContainerIfModLoaded(container, ShapelessLocation.EMPTY, modId);
    }
    default boolean registerContainerIfModLoaded(String modId, String itemId, IArmModelRenderContainer<?, ?> container) {
        return this.registerContainerIfModLoaded(container, ShapelessLocation.location(modId, itemId), modId);
    }
    default boolean registerContainerIfModLoaded(IArmModelRenderContainer<?, ?> container, ShapelessLocation location, String modId) {
        if (this.mod().isModLoaded(modId)) {
            return this.registerContainer(container, location);
        }
        return false;
    }
}
