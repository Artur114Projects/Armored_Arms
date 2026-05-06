package com.artur114.armoredarms.client.util;

import com.artur114.armoredarms.core.api.IPriority;
import org.jetbrains.annotations.NotNull;

public record GenericPriority(int priority) implements IPriority {
    @Override
    public int toInt() {
        return this.priority;
    }

    @Override
    public @NotNull String toString() {
        return "P:" + this.priority;
    }
}
