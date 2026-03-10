package com.artur114.armoredarms.core.util;

import java.util.Iterator;

public interface ITextureIterator {
    boolean hasNext();
    void bindNext();
    void postBind();
}
