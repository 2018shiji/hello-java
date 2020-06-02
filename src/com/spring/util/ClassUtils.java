package com.spring.util;

import java.util.IdentityHashMap;
import java.util.Map;

public class ClassUtils {

    private static final Map<Class<?> , Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<>(8);

    public static Class<?> forName(String name, ClassLoader classLoader)
                throws ClassNotFoundException {
        return Class.forName(name, false, ClassUtils.class.getClassLoader());
    }

    public static Class<?> resolvePrimitiveIfNecessary(Class<?> clazz) {
        return (clazz.isPrimitive() && clazz != void.class ? primitiveWrapperTypeMap.get(clazz) : clazz);
    }

}
