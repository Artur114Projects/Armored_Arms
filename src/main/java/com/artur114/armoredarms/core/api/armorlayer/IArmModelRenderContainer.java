package com.artur114.armoredarms.core.api.armorlayer;

import com.artur114.armoredarms.core.api.IPrioritised;

public interface IArmModelRenderContainer<M extends IArmModelManager<?, ?>> extends IPrioritised {
    IArmModelRenderer<M> create(M manager);
    Class<M> targetManager();
}
