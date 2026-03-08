package com.artur114.armoredarms.core.util;

import com.artur114.armoredarms.core.api.IArmRenderComponent;

public class RenderException extends RuntimeException {
    private IArmRenderComponent brokenComponent = null;
    private String messageForPlayer = null;
    private boolean isFatal = false;
}
