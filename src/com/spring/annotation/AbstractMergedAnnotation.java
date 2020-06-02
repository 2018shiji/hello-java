package com.spring.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class AbstractMergedAnnotation <A extends Annotation> implements MergedAnnotation<A> {
    private volatile A synthesizedAnnotation;

    @Override
    public boolean isDirectoryPresent() {
        return isPresent() && getDistance() == 0;
    }

    @Override
    public boolean isMetaPresent() {
        return isPresent() && getDistance() > 0;
    }

    @Override
    public boolean hasNonDefaultValue(String attributeName) {
        return !hasDefaultValue(attributeName);
    }

    @Override
    public byte getByte(String attributeName) throws NoSuchElementException {
        return getRequiredAttributeValue(attributeName, Byte.class);
    }

    @Override
    public byte[] getByteArray(String attributeName) throws NoSuchElementException {
        return getRequiredAttributeValue(attributeName, byte[].class);
    }

    @Override
    public boolean getBoolean(String attributeName) throws NoSuchElementException {
        return getRequiredAttributeValue(attributeName, Boolean.class);
    }

    @Override
    public boolean[] getBooleanArray(String attributeName) throws NoSuchElementException {
        return getRequiredAttributeValue(attributeName, boolean[].class);
    }

    @Override
    public char getChar(String attributeName) throws NoSuchElementException {
        return getRequiredAttributeValue(attributeName, Character.class);
    }

    @Override
    public char[] getCharArray(String attributeName) throws NoSuchElementException {
        return getRequiredAttributeValue(attributeName, char[].class);
    }

    @Override
    public short getShort(String attributeName) throws NoSuchElementException {
        return getRequiredAttributeValue(attributeName, Short.class);
    }

    @Override
    public short[] getShortArray(String attributeName) throws NoSuchElementException {
        return getRequiredAttributeValue(attributeName, short[].class);
    }

    @Override
    public int getInt(String attributeName) throws NoSuchElementException {
        return getRequiredAttributeValue(attributeName, Integer.class);
    }

    @Override
    public int[] getIntArray(String attributeName) throws NoSuchElementException {
        return getRequiredAttributeValue(attributeName, int[].class);
    }

    @Override
    public long getLong(String attributeName) throws NoSuchElementException {
        return getRequiredAttributeValue(attributeName, Long.class);
    }

    @Override
    public long[] getLongArray(String attributeName) throws NoSuchElementException {
        return getRequiredAttributeValue(attributeName, long[].class);
    }

    @Override
    public double getDouble(String attributeName) throws NoSuchElementException {
        return getRequiredAttributeValue(attributeName, Double.class);
    }

    @Override
    public double[] getDoubleArray(String attributeName) throws NoSuchElementException {
        return getRequiredAttributeValue(attributeName, double[].class);
    }

    @Override
    public float getFloat(String attributeName) throws NoSuchElementException {
        return getRequiredAttributeValue(attributeName, Float.class);
    }

    @Override
    public float[] getFloatArray(String attributeName) throws NoSuchElementException {
        return getRequiredAttributeValue(attributeName, float[].class);
    }

    @Override
    public String getString(String attributeName) throws NoSuchElementException {
        return getRequiredAttributeValue(attributeName, String.class);
    }

    @Override
    public String[] getStringArray(String attributeName) throws NoSuchElementException {
        return getRequiredAttributeValue(attributeName, String[].class);
    }

    @Override
    public Class<?> getClass(String attributeName) throws NoSuchElementException {
        return getRequiredAttributeValue(attributeName, Class.class);
    }

    @Override
    public Class<?>[] getClassArray(String attributeName) throws NoSuchElementException {
        return getRequiredAttributeValue(attributeName, Class[].class);
    }

    @Override
    public <E extends Enum<E>> E getEnum(String attributeName, Class<E> type) {
        return getRequiredAttributeValue(attributeName, type);
    }

    @Override
    public <E extends Enum<E>> E[] getEnumArray(String attributeName, Class<E> type) throws NoSuchElementException {
        Class<?> arrayType = Array.newInstance(type, 0).getClass();
        return (E[])getRequiredAttributeValue(attributeName, arrayType);
    }

    @Override
    public Optional<Object> getValue(String attributeName) {
        return getValue(attributeName, Object.class);
    }

    @Override
    public <T> Optional<T> getValue(String attributeName, Class<T> type) {
        return Optional.ofNullable(getAttributeValue(attributeName, type));
    }

    @Override
    public Optional<Object> getDefaultValue(String attributeName) {
        return getDefaultValue(attributeName, Object.class);
    }

    @Override
    public Optional<A> synthesize(Predicate<? super MergedAnnotation<A>> condition)
            throws NoSuchElementException {

        return (condition.test(this) ? Optional.of(synthesize()) : Optional.empty());
    }

    @Override
    public A synthesize() throws NoSuchElementException {
        if(!isPresent()){
            throw new NoSuchElementException("Unable to synthesize missing annotation");
        }
        A synthesized = this.synthesizedAnnotation;
        if (synthesized == null) {
            synthesized = createSynthesized();
            this.synthesizedAnnotation = synthesized;
        }
        return synthesized;
    }

    private <T> T getRequiredAttributeValue(String attributeName, Class<T> type) {
        T value = getAttributeValue(attributeName, type);
        if(value == null){
            throw new NoSuchElementException("No attribute named '" + attributeName +
                    "' present in merged annotation " + getType().getName());
        }
        return value;
    }

    protected abstract<T> T getAttributeValue(String attributeName, Class<T> type);

    protected abstract A createSynthesized();

}
