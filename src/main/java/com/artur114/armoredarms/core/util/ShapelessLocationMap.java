package com.artur114.armoredarms.core.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.*;

public class ShapelessLocationMap<V> implements Map<ShapelessLocation, V> {
    private final Map<ShapelessLocation.Location, V> shapelessDomains = new HashMap<>();
    private final Map<ShapelessLocation.Location, V> shapelessPaths = new HashMap<>();
    private final Map<ShapelessLocation, V> map = new HashMap<>();
    private V absoluteValue = null;

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof ShapelessLocation)) {
            return false;
        }

        ShapelessLocation location = (ShapelessLocation) key;
        ShapelessLocation.Location path = location.pathLocation();
        ShapelessLocation.Location domain = location.domainLocation();

        boolean ret = this.map.containsKey(location);

        ret |= this.shapelessDomains.containsKey(path);
        ret |= this.shapelessPaths.containsKey(domain);

        return ret;
    }

    @Override
    public boolean containsValue(Object value) {
        return this.map.containsValue(value) || this.shapelessDomains.containsValue(value) || this.shapelessPaths.containsValue(value) || Objects.equals(this.absoluteValue, value);
    }

    @Override
    public V get(Object key) {
        if (!(key instanceof ShapelessLocation)) {
            return null;
        }

        return this.get((ShapelessLocation) key);
    }

    public V get(ShapelessLocation location) {
        V value = this.map.get(location);
        if (value != null) {
            return value;
        }

        value = this.shapelessPaths.get(location.domainLocation());
        if (value != null) {
            return value;
        }

        value = this.shapelessDomains.get(location.pathLocation());
        if (value != null) {
            return value;
        }

        return this.absoluteValue;
    }

    public List<V> getAll(ShapelessLocation location) {
        if (!location.isShapeless()) {
            V value = this.get(location);
            if (value != null) {
                return Collections.singletonList(value);
            } else {
                return Collections.emptyList();
            }
        }

        List<V> list = new ArrayList<>();

        this.map.forEach((k, v) -> {
            if (location.equals(k)) {
                list.add(v);
            }
        });

        return list;
    }

    @Override
    public V put(ShapelessLocation location, V value) {
        if (location.isAbsoluteShapeless()) {
            this.absoluteValue = value;
        }

        V obj = this.map.put(location, value);

        if (location.isAbsoluteShapeless()) {
            return obj;
        }

        ShapelessLocation.Location path = location.pathLocation();
        ShapelessLocation.Location domain = location.domainLocation();

        if (domain.isShapeless()) {
            this.shapelessDomains.put(path, value);
        }

        if (path.isShapeless()) {
            this.shapelessPaths.put(domain, value);
        }

        return obj;
    }

    @Override
    public V remove(Object key) {
        if (!(key instanceof ShapelessLocation)) {
            return null;
        }

        ShapelessLocation location = (ShapelessLocation) key;

        V obj = this.map.remove(location);

        if (obj != null) {
            if (location.domainLocation().isShapeless()) {
                this.shapelessDomains.remove(location.pathLocation());
            }
            if (location.pathLocation().isShapeless()) {
                this.shapelessPaths.remove(location.domainLocation());
            }
        }

        if (location.isAbsoluteShapeless()) {
            absoluteValue = null;
        }

        return obj;
    }

    @Override
    public void putAll(Map<? extends ShapelessLocation, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        this.shapelessDomains.clear();
        this.shapelessPaths.clear();
        this.map.clear();
    }

    @Override
    public Set<ShapelessLocation> keySet() {
        return this.map.keySet();
    }

    @Override
    public Collection<V> values() {
        return this.map.values();
    }

    @Override
    public Set<Entry<ShapelessLocation, V>> entrySet() {
        return this.map.entrySet();
    }
}
