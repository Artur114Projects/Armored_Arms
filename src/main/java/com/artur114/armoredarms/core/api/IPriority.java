package com.artur114.armoredarms.core.api;


import java.util.function.ToIntFunction;

public interface IPriority extends ToIntFunction<IPriority> {
    int toInt();
}
