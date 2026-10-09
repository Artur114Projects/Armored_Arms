package com.artur114.armoredarms.core.util;

public interface IItemStack {
    boolean isNew(IItemStack stack);
    ShapelessLocation location();
    boolean isEmpty();
}
