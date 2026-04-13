package com.artur114.armoredarms.api;

import com.artur114.armoredarms.core.api.pipeline.IArmRenderPipeline;
import com.artur114.armoredarms.main.ArmoredArms;

public class ArmoredArmsApi {
    public static IArmRenderPipeline<?> currentPipeline() {
        return ArmoredArms.ARMORED_ARMS.pipeline();
    }
}
