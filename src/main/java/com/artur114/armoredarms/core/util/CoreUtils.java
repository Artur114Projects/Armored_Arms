package com.artur114.armoredarms.core.util;

import com.artur114.armoredarms.core.api.IPrioritised;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CoreUtils {
    public static <T extends IPrioritised> List<T> sortPrioritisedList(Collection<T> list) {
        return list.stream().filter((v) -> v.priority() != null).sorted((v1, v2) -> Integer.compare(v2.priority().toInt(), v1.priority().toInt())).collect(Collectors.toList());
    }
}
