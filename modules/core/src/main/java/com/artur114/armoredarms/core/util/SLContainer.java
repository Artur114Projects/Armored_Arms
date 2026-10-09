package com.artur114.armoredarms.core.util;

import java.util.Objects;

public class SLContainer<V> {
    public final ShapelessLocation location;
    public final V value;

    public SLContainer(ShapelessLocation location, V value) {
        this.location = location;
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SLContainer<?>) {
            return this.location.equals(((SLContainer<?>) obj).location) && this.value.equals(((SLContainer<?>) obj).value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.location, this.value);
    }

    @Override
    public String toString() {
        return "[" + this.location + " -> " + this.value + "]";
    }
}
