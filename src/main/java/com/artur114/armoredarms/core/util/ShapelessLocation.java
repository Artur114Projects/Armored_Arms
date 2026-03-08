package com.artur114.armoredarms.core.util;

import java.util.Objects;

public class ShapelessLocation {
    private static final BoundedCache<ShapelessLocation> cache = new BoundedCache<>(2048);

    public synchronized static ShapelessLocation location(String domain, String path) {
        ShapelessLocation location = cache.get(31 * domain.hashCode() + path.hashCode());

        if (location == null) {
            location = new ShapelessLocation(domain, path);
            cache.add(location);
        }

        return location;
    }

    protected final Location domain;
    protected final Location path;

    public ShapelessLocation(String location) {
        String[] strings = location.split(":");
        this.domain = new Location(strings[0]);
        this.path = new Location(strings[1]);
    }

    public ShapelessLocation(String domain, String path) {
        this.domain = new Location(domain);
        this.path = new Location(path);
    }

    public String path() {
        return this.path.location;
    }

    public String domain() {
        return this.domain.location;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ShapelessLocation) {
            return ((ShapelessLocation) obj).domain.equals(this.domain) && ((ShapelessLocation) obj).path.equals(this.path);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31 * this.domain.hash + this.path.hash;
    }

    protected static class Location {
        protected final String location;
        private final boolean all;
        private final int hash;

        private Location(String location) {
            Objects.requireNonNull(location);

            this.location = location;
            this.all = location.equals("*");
            this.hash = location.hashCode();
        }

        public boolean equals(Location location) {
            if (this.all || location.all) {
                return true;
            } else {
                return this.location.equals(location.location);
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Location) {
                return this.equals((Location) obj);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return this.hash;
        }
    }
}
