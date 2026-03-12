package com.artur114.armoredarms.core.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class ShapelessLocationList<V> implements Iterable<ShapelessLocationList.Entry<V>> {
    private final List<Entry<V>> list = new ArrayList<>();

    public void add(String location, V value) {
        this.list.add(new Entry<>(ShapelessLocation.location(location), value));
    }

    public void add(String domain, String path, V value) {
        this.list.add(new Entry<>(ShapelessLocation.location(domain, path), value));
    }

    public void add(ShapelessLocation location, V value) {
        this.list.add(new Entry<>(location, value));
    }

    public List<V> get(ShapelessLocation location) {
        List<V> ret = new ArrayList<>();
        for (Entry<V> entry : this) {
            if (entry.location.equals(location)) {
                ret.add(entry.value);
            }
        }
        return ret;
    }

    public V get(int index) {
        return this.list.get(index).value;
    }

    public int size() {
        return this.list.size();
    }

    @Override
    public Iterator<Entry<V>> iterator() {
        return list.iterator();
    }


    @Immutable
    public static class Entry<V> {
        public final ShapelessLocation location;
        public final V value;

        public Entry(ShapelessLocation location, V value) {
            this.location = location;
            this.value = value;
        }
    }
}
