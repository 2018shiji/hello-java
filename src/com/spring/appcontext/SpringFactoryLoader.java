package com.spring.appcontext;

import com.spring.resource.UrlResource;
import com.spring.structure.LinkedMultiValueMap;
import com.spring.structure.MultiValueMap;
import com.spring.util.ClassUtils;
import com.spring.util.PropertiesLoaderUtils;
import com.spring.util.ReflectionUtils;
import com.spring.util.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class SpringFactoryLoader {

    public static final String FACTORIES_RESOURCES_LOCATION = "META-INF/spring1.factory";

    private static Map<ClassLoader, MultiValueMap<String, String>> springFactoriesCache = new HashMap();

    public static <T> List<T> loadFactory(Class<T> factoryClass, ClassLoader classLoader) {
        ClassLoader loaderToUse = classLoader;
        if (loaderToUse == null)
            loaderToUse = SpringFactoryLoader.class.getClassLoader();
        List<String> factoryNames = loadFactoryByName(factoryClass, loaderToUse);
        List<T> results = new ArrayList<>(factoryNames.size());
        for (String factoryImplName : factoryNames) {
            results.add(instantiateFactory(factoryImplName, factoryClass, loaderToUse));
        }
        return results;
    }


    public static List<String> loadFactoryByName(Class<?> factoryClass, ClassLoader classLoader) {
        String factoryClassName = factoryClass.getName();
        return loadSpringFactories(classLoader).getOrDefault(factoryClassName, Collections.emptyList());
    }

    private static Map<String, List<String>> loadSpringFactories(ClassLoader classLoader) {
        MultiValueMap<String, String> result = springFactoriesCache.get(classLoader);
        if(result != null)
            return result;

        try {
            Enumeration<URL> urls = classLoader.getResources(FACTORIES_RESOURCES_LOCATION);
            result = new LinkedMultiValueMap<>();

            while(urls.hasMoreElements()) {
                URL url = urls.nextElement();
                UrlResource resource = new UrlResource(url);
                Properties properties = PropertiesLoaderUtils.loadProperties(resource);
                for(Map.Entry<?, ?> entry : properties.entrySet()) {
                    String factoryTypeName = ((String) entry.getKey()).trim();
                    for(String factoryImplName : StringUtils.commaDelimitedListToStringArray((String)entry.getValue())){
                        result.add(factoryTypeName, factoryImplName.trim());
                    }
                }
            }
            springFactoriesCache.put(classLoader, result);
            return result;
        } catch (IOException e) { throw new IllegalArgumentException("unable to load factory"); }

    }

    private static <T> T instantiateFactory(String instanceClassName, Class<T> factoryClass, ClassLoader classLoader) {
        try {
            Class<?> instanceClass = ClassUtils.forName(instanceClassName, classLoader);
            if (!factoryClass.isAssignableFrom(instanceClass)) {
                throw new IllegalArgumentException(
                        "Class [" + instanceClassName + "] is not assignable to [" + factoryClass.getName() + "]");
            }
            return (T) ReflectionUtils.accessibleConstructor(instanceClass).newInstance();
        }
        catch (Throwable ex) {
            throw new IllegalArgumentException("Unable to instantiate factory class: " + factoryClass.getName(), ex);
        }
    }

}
