package com.artur114.armoredarms.core.util;

public interface IIntBoundedCache<V> {
    void add(V value);
    V get(int hash);

    static <V> IIntBoundedCache<V> createBestInstance(int bound) {
        if (Reflector.isClassExists("it.unimi.dsi.fastutil.ints.Int2ObjectMap")) {
            return new Int2ObjBoundedCache<>(bound);
        } else {
            return new IntMapBoundedCache<>(bound);
        }
    }
}
