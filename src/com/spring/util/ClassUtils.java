package com.spring.util;

public class ClassUtils {
    public static Class<?> forName(String name, ClassLoader classLoader)
                throws ClassNotFoundException {
        return Class.forName(name, false, ClassUtils.class.getClassLoader());
    }
}
