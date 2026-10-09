package com.artur114.armoredarms.core.api;

import com.artur114.armoredarms.core.util.EnumExceptionType;


public interface IArmRenderComponent {
    default boolean needDeactivateThenError(EnumExceptionType type) {return true;}
    boolean isDeactivated();
    void deactivate();
    String type();
}
