package com.artur114.armoredarms.client.util;

import com.artur114.armoredarms.core.api.IPriority;

public class GenericPriority implements IPriority {
    private final int priority;

    public GenericPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public int toInt() {
        return this.priority;
    }
}