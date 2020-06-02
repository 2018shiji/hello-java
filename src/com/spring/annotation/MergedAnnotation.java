package com.spring.annotation;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;

public interface MergedAnnotation<A extends Annotation> {
    String value = "value";

    Class<A> getType();
    boolean isPresent();
    boolean isDirectoryPresent();
    boolean isMetaPresent();
    int getDistance();
    int getAggregateIndex();

    Object getSource();
    MergedAnnotation<?> getRoot();
    MergedAnnotation<?> getMetaSource();
    List<Class<? extends Annotation>> getMetaTypes();

    boolean hasNonDefaultValue(String attributeName);
    boolean hasDefaultValue(String attributeName) throws NoSuchElementException;

    byte getByte(String attributeName) throws NoSuchElementException;
    byte[] getByteArray(String attributeName) throws NoSuchElementException;
    boolean getBoolean(String attributeName) throws NoSuchElementException;
    boolean[] getBooleanArray(String attributeName) throws NoSuchElementException;
    char getChar(String attributeName) throws NoSuchElementException;
    char[] getCharArray(String attributeName) throws NoSuchElementException;
    short getShort(String attributeName) throws NoSuchElementException;
    short[] getShortArray(String attributeName) throws NoSuchElementException;
    int getInt(String attributeName) throws NoSuchElementException;
    int[] getIntArray(String attributeName) throws NoSuchElementException;
    long getLong(String attributeName) throws NoSuchElementException;
    long[] getLongArray(String attributeName) throws NoSuchElementException;
    double getDouble(String attributeName) throws NoSuchElementException;
    double[] getDoubleArray(String attributeName) throws NoSuchElementException;
    float getFloat(String attributeName) throws NoSuchElementException;
    float[] getFloatArray(String attributeName) throws NoSuchElementException;
    String getString(String attributeName) throws NoSuchElementException;
    String[] getStringArray(String attributeName) throws NoSuchElementException;
    Class<?> getClass(String attributeName) throws NoSuchElementException;
    Class<?>[] getClassArray(String attributeName) throws NoSuchElementException;
    <E extends Enum<E>> E getEnum(String attributeName, Class<E> type) throws NoSuchElementException;
    <E extends Enum<E>> E[] getEnumArray(String attributeName, Class<E> type) throws NoSuchElementException;
    <T extends Annotation> MergedAnnotation<T> getAnnotation(String attributeName, Class<T> type)
            throws NoSuchElementException;
    <T extends Annotation> MergedAnnotation<T>[] getAnnotationArray(String attributeName, Class<T> type)
            throws NoSuchElementException;

    Optional<Object> getValue(String attributeName);
    <T> Optional<T> getValue(String attributeName, Class<T> type);
    Optional<Object> getDefaultValue(String attributeName);
    <T> Optional<T> getDefaultValue(String attributeName, Class<T> type);

    A synthesize() throws NoSuchElementException;
    Optional<A> synthesize(Predicate<? super MergedAnnotation<A>> condition) throws NoSuchElementException;

}
