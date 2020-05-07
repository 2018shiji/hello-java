package com.spring.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

public class ReflectionUtils {

    public static <T> Constructor<T> accessibleConstructor(Class<T> clazz, Class<?>... parameterTypes)
                throws NoSuchMethodException {
        Constructor<T> constructor = clazz.getDeclaredConstructor(parameterTypes);
        if(!Modifier.isPublic(constructor.getModifiers()))
            constructor.setAccessible(true);
        return constructor;
    }

}
