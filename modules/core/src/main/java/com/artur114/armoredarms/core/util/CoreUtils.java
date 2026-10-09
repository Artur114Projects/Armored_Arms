package com.artur114.armoredarms.core.util;

import com.artur114.armoredarms.core.api.IArmRenderComponent;
import com.artur114.armoredarms.core.api.IPrioritised;
import com.artur114.armoredarms.core.api.IPriority;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CoreUtils {
    public static <T> List<T> sortPrioritisedList(Collection<T> list, Function<T, IPriority> priority) {
        return list.stream().filter((v) -> priority.apply(v) != null).sorted((v1, v2) -> Integer.compare(priority.apply(v2).toInt(), priority.apply(v1).toInt())).collect(Collectors.toList());
    }
    public static <T extends IPrioritised> List<T> sortPrioritisedList(Collection<T> list) {
        return list.stream().filter((v) -> v.priority() != null).sorted((v1, v2) -> Integer.compare(v2.priority().toInt(), v1.priority().toInt())).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public static <T, L> List<L> sortByTargetClass(Collection<T> list, Function<T, Class<?>> getTarget, Class<?> clazz) {
        List<L> ret = new ArrayList<>();
        for (T v : list) {
            if (getTarget.apply(v).isAssignableFrom(clazz)) {
                ret.add((L) v);
            }
        }
        return ret;
    }

    public static <T extends IArmRenderComponent> boolean removeDeactivated(Iterable<T> list) {
        Iterator<T> iterator = list.iterator();
        boolean flag = false;

        while (iterator.hasNext()) {
            if (iterator.next().isDeactivated()) {
                iterator.remove(); flag = true;
            }
        }

        return flag;
    }
    public static <T extends IArmRenderComponent> boolean removeDeactivated(ShapelessLocationMap<T> map) {
        List<T> list = null;

        for (T obj : map.values()) {
            if (obj.isDeactivated()) {
                if (list == null) {
                    list = new ArrayList<>(map.size());
                }

                list.add(obj);
            }
        }

        if (list != null) {
            for (T obj : list) {
                map.removeObject(obj);
            }
        }

        return list != null;
    }
    public static <T extends IArmRenderComponent> List<T> filterDeactivated(Collection<T> list) {
        return list.stream().filter((t) -> !t.isDeactivated()).collect(Collectors.toList());
    }

    public static String compressClassName(Class<?> clazz) {
        return compressClassName(clazz, 2);
    }

    public static String compressClassName(Class<?> clazz, int noCutPackagesCount) {
        String className = clazz.getName();
        int lastPoint = className.lastIndexOf(".");

        if (noCutPackagesCount <= 0) {
            return className.substring(lastPoint + 1);
        }

        int substringPoint = 0;
        int packagesCount = 0;

        for (int i = 0; i != className.length(); i++) {
            char c = className.charAt(i);

            if (c == '.') {
                substringPoint = i;
                packagesCount++;
            }

            if (packagesCount >= noCutPackagesCount) {
                break;
            }
        }

        if (substringPoint == lastPoint) {
            return className;
        }

        return className.substring(0, substringPoint) + ":" + className.substring(lastPoint + 1);
    }
}
