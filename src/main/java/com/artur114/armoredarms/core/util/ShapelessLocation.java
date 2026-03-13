package com.artur114.armoredarms.core.util;

import java.util.Objects;

@Immutable
public class ShapelessLocation {
    private static final Int2ObjBoundedCache<ShapelessLocation> cache = new Int2ObjBoundedCache<>(2048);
    private static final Location SHAPELESS_L = new Location("*");
    private static final Location EMPTY_L = new Location("");

    public static final ShapelessLocation EMPTY = location("", "");
    public static final ShapelessLocation ABSOLUTE = location("*:*");

    public synchronized static ShapelessLocation location(String location) {
        String[] strings = location.split(":");
        if (strings.length < 2) {
            throw new IllegalArgumentException("Illegal location string: " + location);
        }
        return location(strings[0], strings[1]);
    }

    public synchronized static ShapelessLocation location(String domain, String path) {
        ShapelessLocation location = cache.get(31 * domain.hashCode() + path.hashCode());

        if (location == null) {
            location = new ShapelessLocation(domain, path);
            cache.add(location);
        }

        return location;
    }

    private static Location createLocation(String location) {
        switch (location) {
            case "":
                return EMPTY_L;
            case "*":
                return SHAPELESS_L;
        }

        return new Location(location);
    }

    private final Location domain;
    private final Location path;

    public ShapelessLocation(String location) { // TODO: 10.03.2026 Доделать
        String[] strings = location.split(":");
        if (strings.length < 2) {
            throw new IllegalArgumentException("Illegal location string: " + location);
        }
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

    @Override
    public String toString() {
        return this.domain + ":" + this.path;
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
            return this == SHAPELESS_L;
        }

        public boolean isEmpty() {
            return this == EMPTY_L;
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

        @Override
        public String toString() {
            return this.location;
        }
    }
}
