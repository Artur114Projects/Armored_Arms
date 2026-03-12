package com.artur114.armoredarms.core.util;

import com.artur114.armoredarms.core.api.IPrioritised;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CoreUtils {
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
}
