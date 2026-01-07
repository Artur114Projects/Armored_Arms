package com.artur114.armoredarms.client.util;

import java.util.Objects;

public class Tuple<F, S> {
    private final S second;
    private final F first;

    public Tuple(F first, S second) {
        this.second = second;
        this.first = first;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.first, this.second);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Tuple)) {
            return false;
        }
        return Objects.equals(((Tuple<?, ?>) obj).getFirst(), this.getFirst()) && Objects.equals(((Tuple<?, ?>) obj).getSecond(), this.getSecond());
    }
}
