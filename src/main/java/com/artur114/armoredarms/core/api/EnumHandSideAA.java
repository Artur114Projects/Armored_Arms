package com.artur114.armoredarms.core.api;

public enum EnumHandSideAA {
    LEFT, RIGHT;

    public int delta() {
        switch (this) {
            case RIGHT:
                return 1;
            case LEFT:
                return -1;
            default:
                throw new IllegalStateException();
        }
    }

    public EnumHandSideAA opposite() {
        switch (this) {
            case RIGHT:
                return LEFT;
            case LEFT:
                return RIGHT;
            default:
                throw new IllegalStateException();
        }
    }
}
