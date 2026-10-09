package com.artur114.armoredarms.core.util;

import org.apache.logging.log4j.Level;

public enum EnumExceptionType {
    WARN, ERROR, FATAL;

    public Level loglevel() {
        switch (this) {
            case WARN:
                return Level.WARN;
            case ERROR:
                return Level.ERROR;
            case FATAL:
                return Level.FATAL;
            default:
                throw new IllegalStateException("wtf!?");
        }
    }
}
