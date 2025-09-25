package com.artur114.armoredarms.client.util;

import com.artur114.armoredarms.api.IArmRenderLayer;


public class RMException extends RuntimeException { // TODO: 26.09.2025 Доделать
    private IArmRenderLayer fatalOnLayer = null;
    private boolean isFatal = false;
    private Method method = null;

    public RMException() {}

    public RMException(String message) {
        super(message);
    }

    public RMException(String message, Throwable cause) {
        super(message, cause);

        this.processCause(cause);
    }

    public RMException(Throwable cause) {
        super(cause);

        this.processCause(cause);
    }

    public RMException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);

        this.processCause(cause);
    }

    private void processCause(Throwable cause) {
        if (cause instanceof RMException) {
            this.isFatal |= ((RMException) cause).isFatal;
            this.fatalOnLayer = this.nonNull(((RMException) cause).fatalOnLayer, this.fatalOnLayer);
            this.method = this.nonNull(((RMException) cause).method, this.method);
        }
    }

    private <T> T nonNull(T t1, T t2) {
        if (t1 == null) {
            return t2;
        }
        return t1;
    }

    public RMException setFatal() {
        this.isFatal = true; return this;
    }

    public RMException setFatalLayer(IArmRenderLayer layer) {
        this.fatalOnLayer = layer; return this;
    }

    public RMException setMethod(Method method) {
        this.method = method; return this;
    }

    public boolean isFatal() {
        return this.isFatal;
    }

    public boolean isFatalOnLayer() {
        return this.fatalOnLayer != null;
    }

    public IArmRenderLayer fatalLayer() {
        return this.fatalOnLayer;
    }

    public Method method() {
        return this.method;
    }

    public String[] messageForPlayer() {
        return new String[0];
    }

    public void compileMessage() {

    }

    public enum Method {
        TICK, RENDER;

        public String nameString() {
            switch (this) {
                case TICK:
                    return "tryTick";
                case RENDER:
                    return "tryRender";
                default:
                    return "wtf?";
            }
        }
    }
}
