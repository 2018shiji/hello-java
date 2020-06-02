package com.spring.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ReflectionUtils {

    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    public static <T> Constructor<T> accessibleConstructor(Class<T> clazz, Class<?>... parameterTypes)
                throws NoSuchMethodException {
        Constructor<T> constructor = clazz.getDeclaredConstructor(parameterTypes);
        if(!Modifier.isPublic(constructor.getModifiers()))
            constructor.setAccessible(true);
        return constructor;
    }

    public static Object invokeMethod(Method method, Object target) {
        return invokeMethod(method, target, EMPTY_OBJECT_ARRAY);
    }

    public static Object invokeMethod(Method method, Object target, Object... args){
        try {
            return method.invoke(target, args);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        throw new IllegalStateException();
    }

}
