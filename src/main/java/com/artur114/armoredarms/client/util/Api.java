package com.artur114.armoredarms.client.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Non-functional annotation intended for developers.
 */
public @interface Api {

    /**
     * If you see this annotation above a class, it means that the class is deprecated and will soon be removed/replaced.
     * The alternative field specifies an alternative for this class. If the field is empty, it means that the alternative is not yet ready/will not be available.
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.CLASS)
    @interface Legacy {
        Class<?> alternative() default Object.class;
    }

    /**
     * If you see this annotation above a class, it means its contents can be changed.
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.CLASS)
    @interface Unstable {}
}
