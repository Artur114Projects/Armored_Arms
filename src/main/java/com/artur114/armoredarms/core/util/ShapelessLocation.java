package com.artur114.armoredarms.core.util;

import java.util.Objects;

@Immutable
public class ShapelessLocation {
    private static final Int2ObjBoundedCache<ShapelessLocation> cache = new Int2ObjBoundedCache<>(2048);
    private static final Location SHAPELESS = new Location("*");
    private static final Location EMPTY = new Location("");

    public synchronized static ShapelessLocation location(String domain, String path) {
        ShapelessLocation location = cache.get(31 * domain.hashCode() + path.hashCode());

        if (location == null) {
            location = new ShapelessLocation(domain, path);
            cache.add(location);
        }

        return location;
    }

    protected static Location createLocation(String location) {
        switch (location) {
            case "":
                return EMPTY;
            case "*":
                return SHAPELESS;
        }

        return new Location(location);
    }

    protected final Location domain;
    protected final Location path;

    public ShapelessLocation(String location) {
        String[] strings = location.split(":");
        this.domain = createLocation(strings[0]);
        this.path = createLocation(strings[1]);
    }

    public ShapelessLocation(String domain, String path) {
        this.domain = createLocation(domain);
        this.path = createLocation(path);
    }

    public boolean isAbsoluteShapeless() {
        return this.domain.isShapeless() && this.path.isShapeless();
    }

    public boolean isShapeless() {
        return this.domain.isShapeless() || this.path.isShapeless();
    }

    public boolean isEmpty() {
        return this.domain.isEmpty() || this.path.isEmpty();
    }

    public Location pathLocation() {
        return this.path;
    }

    public Location domainLocation() {
        return this.domain;
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

    @Immutable
    public static class Location {
        protected final String location;
        private final int hash;

        private Location(String location) {
            Objects.requireNonNull(location);

            this.location = location;
            this.hash = location.hashCode();
        }

        public boolean isShapeless() {
            return this == SHAPELESS;
        }

        public boolean isEmpty() {
            return this == EMPTY;
        }

        public boolean equals(Location location) {
            if (this.isShapeless() || location.isShapeless()) {
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
