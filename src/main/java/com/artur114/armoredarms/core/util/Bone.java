package com.artur114.armoredarms.core.util;

import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Bone {
    private List<IBoneAdapter<Object>> adapters = new ArrayList<>();
    private final ObjectBuff loggingBuff = new ObjectBuff();
    private final IAAModContainer mod;
    private final ObjectBuff data;
    private final String name;

    protected Bone(IAAModContainer mod, String name) {
        this.data = new ObjectBuff();
        this.name = name;
        this.mod = mod;
    }

    public void injectTo(Object obj) {
        Class<?> clazz = obj.getClass();

        for (IBoneAdapter<Object> adapter : this.adapters) {
            if (adapter.targetObjectClass().isAssignableFrom(clazz) && adapter.canWork(obj)) {
                adapter.inject(this, this.data.reset(), obj);
                return;
            }
        }

        throw new IllegalArgumentException("Can't find bone adapter for " + clazz);
    }

    public void setFrom(Object obj) {
        Class<?> clazz = obj.getClass();

        for (IBoneAdapter<Object> adapter : this.adapters) {
            if (adapter.targetObjectClass().isAssignableFrom(clazz) && adapter.canWork(obj)) {
                this.loggingBuff.copyFrom(this.data).reset();

                adapter.set(this, this.data.reset(), obj);

                if (this.data.equals(this.loggingBuff)) {
                    return;
                }

                Logger logger = this.mod.logger().namedLogger("ARMOREDARMS-CORE");
                logger.debug("-Bone [{}] data change trace start:", this.name);
                this.data.reset();
                while (this.loggingBuff.hasNext() && this.data.hasNext()) {
                    String nameOld = this.loggingBuff.peekName();
                    Object objOld = this.loggingBuff.readObject();

                    String nameNew = this.data.peekName();
                    Object objNew = this.data.readObject();

                    if (!nameOld.equals(nameNew)) {
                        logger.debug("   Value {} has been renamed to {}", nameOld, nameNew);
                    }
                    if (!nameNew.startsWith("nlc|") && !objOld.equals(objNew)) {
                        logger.debug("   Value {}:{} has been changed to {}", nameOld, objOld, objNew);
                    }
                }
                while (this.data.hasNext()) {
                    String nameNew = this.data.peekName();
                    Object objNew = this.data.readObject();

                    logger.debug("   Value {}:{} has been added", nameNew, objNew);
                }
                logger.debug("-Bone [{}] data change trace end|", this.name);
                return;
            }
        }

        throw new IllegalArgumentException("Can't find bone adapter for " + clazz);
    }

    @SuppressWarnings("unchecked")
    public void registerAdapter(IBoneAdapter<?> adapter) {
        this.adapters.add((IBoneAdapter<Object>) adapter);
        this.adapters = CoreUtils.sortPrioritisedList(this.adapters);
    }

    public boolean hasAdapterFor(Class<?> clazz) {
        for (IBoneAdapter<Object> adapter : this.adapters) {
            if (adapter.targetObjectClass().isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }
}
