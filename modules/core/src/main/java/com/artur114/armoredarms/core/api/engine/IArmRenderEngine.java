package com.artur114.armoredarms.core.api.engine;

import com.artur114.armoredarms.core.api.layer.IArmRenderLayer;
import com.artur114.armoredarms.core.api.IPrioritised;
import com.artur114.armoredarms.core.api.IArmRenderComponent;
import com.artur114.armoredarms.core.api.pipeline.IArmRenderPipeline;
import com.artur114.armoredarms.core.util.ArmsBone;
import com.artur114.armoredarms.core.util.IAAModContainer;

public interface IArmRenderEngine<P extends IArmRenderPipeline<?>> extends IPrioritised, IArmRenderComponent {
    void init(P context, IAAModContainer mod);
    void tryRender(P context);
    void tryTick(P context);
    ArmsBone mainBones();
    <L extends IArmRenderLayer<?>> L layer(Class<L> clazz);
    boolean canWork(IAAModContainer mod);
    Class<P> targetPipeline();

    @Override
    default String type() {
        return "engine";
    }
}
