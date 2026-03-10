package com.artur114.armoredarms.core.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class Int2ObjBoundedCache<V> {
    private final Int2ObjectMap<CacheEntry<V>> cache = new Int2ObjectOpenHashMap<>();
    private final int bound;

    public Int2ObjBoundedCache(int bound) {
        this.bound = Math.max(1, bound);
    }

    public V get(int hash) {
        CacheEntry<V> cache = this.cache.get(hash);
        if (cache != null) return cache.get();
        return null;
    }

    public void add(V value) {
        if (value == null) {
            return;
        }

        if (this.cache.size() + 1 >= this.bound) {
            long min = Long.MAX_VALUE;
            int minHash = 0;

            for (CacheEntry<V> cache : this.cache.values()) {
                if (cache.getCount < min) {
                    min = cache.getCount;
                    minHash = cache.hash;
                }
            }

            this.cache.remove(minHash);
        }

        int hash = value.hashCode();
        this.cache.put(hash, new CacheEntry<>(hash, value));
    }

    private static class CacheEntry<V> {
        private final int hash;
        private final V value;
        private long getCount;

        private CacheEntry(int hash, V value) {
            this.value = value;
            this.hash = hash;
        }

        public V get() {
            this.getCount++;
            return this.value;
        }
    }
}
