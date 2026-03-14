package com.artur114.armoredarms.core.api.event;

import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;
import com.artur114.armoredarms.core.api.modelrender.IArmModelManager;
import com.artur114.armoredarms.core.util.IAAModContainer;
import com.artur114.armoredarms.core.util.SLContainer;
import com.artur114.armoredarms.core.util.ShapelessLocation;
import org.apache.logging.log4j.Logger;

import java.util.List;

public interface IAbstractGrabModelManagersEvent {
    List<IArmModelManager<?, ?>> managers();
    List<SLContainer<IArmModelManager<?, ?>>> managersSL();

    IAAModContainer mod();
    Class<? extends IArmRenderLayer<?>> layer();

    default boolean registerManager(IArmModelManager<?, ?> manager) {
        return this.registerManager(manager, ShapelessLocation.EMPTY);
    }
    default boolean registerManager(String modId, String itemId, IArmModelManager<?, ?> manager) {
        return this.registerManager(manager, ShapelessLocation.location(modId, itemId));
    }
    default boolean registerManager(IArmModelManager<?, ?> manager, ShapelessLocation location) {
        Logger logger = mod().logger("ARMOREDARMS-CORE");
        try {
            if (manager.targetLayer().isAssignableFrom(this.layer())) {
                if (this.managersSL() != null && !location.isEmpty()) {
                    this.managersSL().add(new SLContainer<>(location, manager));
                } else if (this.managers() != null) {
                    this.managers().add(manager);
                } else {
                    logger.warn("Cannot register model manager");
                    logger.warn("   Model manager: {}", manager);
                    logger.warn("   Location: {}", location);
                    logger.warn("   Layer: {}", this.layer());
                    logger.warn("   Event managers list: {}", this.managers());
                    logger.warn("   Event SL managers list: {}", this.managersSL());
                }
                return true;
            }
        } catch (Exception e) {
            logger.error("An error occurs while registering model manager");
            logger.error("  Manager: {}", manager);
            logger.error("  Stack trace: ", e);
        }
        return false;
    }

    default boolean registerManagerIfModLoaded(IArmModelManager<?, ?> manager, String modId) {
        return this.registerManagerIfModLoaded(manager, ShapelessLocation.EMPTY, modId);
    }
    default boolean registerManagerIfModLoaded(String modId, String itemId, IArmModelManager<?, ?> manager) {
        return this.registerManagerIfModLoaded(manager, ShapelessLocation.location(modId, itemId), modId);
    }
    default boolean registerManagerIfModLoaded(IArmModelManager<?, ?> manager, ShapelessLocation location, String modId) {
        if (this.mod().isModLoaded(modId)) {
            return this.registerManager(manager, location);
        }
        return false;
    }
}
