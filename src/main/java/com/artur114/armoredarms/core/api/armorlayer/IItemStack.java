package com.artur114.armoredarms.core.api.armorlayer;

import com.artur114.armoredarms.core.util.ShapelessLocation;

public interface IItemStack {
    boolean isNew(IItemStack stack);
    ShapelessLocation location();
    boolean isEmpty();
}
