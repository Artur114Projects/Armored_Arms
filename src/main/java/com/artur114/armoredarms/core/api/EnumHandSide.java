package com.artur114.armoredarms.core.api;

public enum EnumHandSide {
    RIGHT, LEFT;

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

    public EnumHandSide opposite() {
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
