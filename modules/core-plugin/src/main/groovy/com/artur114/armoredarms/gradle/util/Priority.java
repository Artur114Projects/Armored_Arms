package com.artur114.armoredarms.gradle.util;


public enum Priority implements IPriority {
    LOWEST, LOW, NORMAL, HIGH, HIGHEST;
    @Override
    public int toInt() {
        return this.ordinal() - 2;
    }
}
