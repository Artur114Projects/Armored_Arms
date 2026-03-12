package com.artur114.armoredarms.core.api.modelrender;

import com.artur114.armoredarms.core.api.IPrioritised;
import com.artur114.armoredarms.core.api.layer.AbstractArmorRenderLayer;
import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;

public interface IArmModelRenderContainer<L extends IArmRenderLayer<?>, M extends IArmModelManager<?, L>> extends IPrioritised {
    IArmModelRenderer<M> create(M manager);
    boolean needWork(M manager);
    Class<M> targetManager();
    Class<L> targetLayer();
}
