package com.artur114.armoredarms.client.modelrender;

import com.artur114.armoredarms.client.engines.AbstractRenderEngineForge;
import com.artur114.armoredarms.core.api.engine.IArmRenderEngine;

public interface INeedRenderProvider {
    boolean needRender(IArmRenderEngine<?> engine, boolean renderEngineState);
}
