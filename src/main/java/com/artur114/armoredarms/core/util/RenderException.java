package com.artur114.armoredarms.core.util;

import com.artur114.armoredarms.core.api.IArmRenderComponent;


public class RenderException extends RuntimeException {
    private EnumExceptionType type = EnumExceptionType.ERROR;
    private IArmRenderComponent brokenComponent = null;
    private String messageForPlayer = null;

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

    public IArmRenderComponent brokenComponent() {
        return this.brokenComponent;
    }

    public String messageForPlayer() {
        return this.messageForPlayer;
    }

    public EnumExceptionType type() {
        return this.type;
    }

    public RenderException setComponent(IArmRenderComponent component) {
        this.brokenComponent = component; return this;
    }

    public RenderException setType(EnumExceptionType type) {
        this.type = type; return this;
    }

    public RenderException setMessageForPlayer(String messageForPlayer) {
        this.messageForPlayer = messageForPlayer; return this;
    }

    private void processCause(Throwable cause) {
        if (cause instanceof RenderException) {
            this.type = this.nonNull(((RenderException) cause).type, this.type);
            this.messageForPlayer = this.nonNull(((RenderException) cause).messageForPlayer, this.messageForPlayer);
            this.brokenComponent = this.nonNull(((RenderException) cause).brokenComponent, this.brokenComponent);
        }
    }

    private <T> T nonNull(T t1, T t2) {
        if (t1 == null) {
            return t2;
        }
        return t1;
    }
}
