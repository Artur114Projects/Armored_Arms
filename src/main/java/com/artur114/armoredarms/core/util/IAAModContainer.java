package com.artur114.armoredarms.core.util;

public interface IAAModContainer {
    boolean isModLoaded(String modId);
    <R> R post(IEvent<R> event);
}
