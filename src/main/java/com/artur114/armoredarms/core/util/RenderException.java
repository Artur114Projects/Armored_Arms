package com.artur114.armoredarms.core.util;

import com.artur114.armoredarms.core.api.IArmRenderComponent;

public class RenderException extends RuntimeException {
    private IArmRenderComponent brokenComponent = null;
    private String messageForPlayer = null;
    private boolean isFatal = false;

    public RenderException() {}

    public RenderException(String message) {
        super(message);
    }

    public RenderException(String message, Throwable cause) {
        super(message, cause);

        this.processCause(cause);
    }

    public RenderException(Throwable cause) {
        super(cause);

        this.processCause(cause);
    }

    public RenderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);

        this.processCause(cause);
    }

    public RenderException setComponent(IArmRenderComponent component) {
        this.brokenComponent = component; return this;
    }

    public RenderException setFatal() {
        this.isFatal = true; return this;
    }

    public RenderException setMessageForPlayer(String messageForPlayer) {
        this.messageForPlayer = messageForPlayer; return this;
    }

    private void processCause(Throwable cause) {

    }
}
