package com.artur114.armoredarms.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class RenderExceptionMulti extends RenderException {
    private final List<RenderException> causes = new ArrayList<>();

    public RenderExceptionMulti() {
    }

    public RenderExceptionMulti(Collection<RenderException> causes) {
        this.causes.addAll(causes);
    }

    public RenderExceptionMulti(String message) {
        super(message);
    }

    public RenderExceptionMulti(String message, Throwable cause) {
        super(message, cause);
    }

    public RenderExceptionMulti(Throwable cause) {
        super(cause);
    }

    public RenderExceptionMulti(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public void add(RenderException cause) {
        this.causes.add(cause);
    }

    public RenderException[] causes() {
        List<RenderException> ret = new ArrayList<>();

        for (RenderException cause : this.causes) {
            if (cause instanceof RenderExceptionMulti) {
                ret.addAll(Arrays.asList(((RenderExceptionMulti) cause).causes()));
            } else {
                ret.add(cause);
            }
        }

        return ret.toArray(new RenderException[0]);
    }

    @Override
    protected void processCause(Throwable cause) {
        if (cause instanceof RenderException) {
            this.causes.add((RenderException) cause);
        } else {
            this.causes.add(new RenderException(cause));
        }
    }
}
