package com.artur114.armoredarms.client.util;

import com.artur114.armoredarms.core.api.IPriority;

public record GenericPriority(int priority) implements IPriority {
    @Override
    public int toInt() {
        return this.priority;
    }
}
