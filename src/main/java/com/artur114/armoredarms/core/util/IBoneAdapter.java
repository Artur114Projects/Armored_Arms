package com.artur114.armoredarms.core.util;

import com.artur114.armoredarms.core.api.IPrioritised;

public interface IBoneAdapter<T> extends IPrioritised {
    void inject(Bone bone, ObjectBuff data, T to);
    void set(Bone bone, ObjectBuff data, T from);
    Class<T> targetObjectClass();
    boolean canWork(T obj);
}
