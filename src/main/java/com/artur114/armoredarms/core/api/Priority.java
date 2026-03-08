package com.artur114.armoredarms.core.api;


public enum Priority implements IPriority {
    LOWEST, LOW, NORMAL, HIGH, HIGHEST;
    @Override
    public int toInt() {
        return this.ordinal() - 2;
    }

    @Override
    public int applyAsInt(IPriority value) {
        return value.toInt();
    }
}
