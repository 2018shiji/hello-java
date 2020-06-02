package com.spring.annotation;

import com.spring.util.ClassUtils;
import com.spring.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class TypeMappedAnnotation<A extends Annotation> extends AbstractMergedAnnotation<A> {

    private static final Map<Class<?>, Object> EMPTY_ARRAYS;
    static{
        Map<Class<?>, Object> emptyArrays = new HashMap();
        emptyArrays.put(String.class, new String[]{});
        emptyArrays.put(boolean.class, new boolean[] {});
        emptyArrays.put(byte.class, new byte[] {});
        emptyArrays.put(char.class, new char[] {});
        emptyArrays.put(double.class, new double[] {});
        emptyArrays.put(float.class, new float[] {});
        emptyArrays.put(int.class, new int[] {});
        emptyArrays.put(long.class, new long[] {});
        emptyArrays.put(short.class, new short[] {});
        EMPTY_ARRAYS = Collections.unmodifiableMap(emptyArrays);
    }

    private final AnnotationTypeMapping mapping;

    private final ClassLoader classLoader;

    private final Object source;

    private final Object rootAttributes;

    private final ValueExtractor valueExtractor;

    private final int aggregateIndex;

    private final boolean useMergedValues;

    private final Predicate<String> attributeFilter;

    private final int[] resolvedRootMirrors;

    private final int[] resolvedMirrors;

    private String string;


    private TypeMappedAnnotation(AnnotationTypeMapping mapping, ClassLoader classLoader,
                                 Object source, Object rootAttributes,
                                 BiFunction<Method, Object, Object> valueExtractor, int aggregateIndex) {

        this(mapping, classLoader, source, rootAttributes, valueExtractor, aggregateIndex, null);
    }

    private TypeMappedAnnotation(AnnotationTypeMapping mapping, ClassLoader classLoader,
                                 Object source, Object rootAttributes,
                                 BiFunction<Method, Object, Object> valueExtractor, int aggregateIndex,
                                 int[] resolvedRootMirrors) {

        this.mapping = mapping;
        this.classLoader = classLoader;
        this.source = source;
        this.rootAttributes = rootAttributes;
        this.valueExtractor = valueExtractor;
        this.aggregateIndex = aggregateIndex;
        this.useMergedValues = true;
        this.attributeFilter = null;
        this.resolvedRootMirrors = (resolvedRootMirrors != null ? resolvedRootMirrors :
                mapping.getRoot().getMirrorSets().resolve(source, rootAttributes, this.valueExtractor));
        this.resolvedMirrors = (getDistance() == 0 ? this.resolvedRootMirrors :
                mapping.getMirrorSets().resolve(source, this, this::getValueForMirrorResolution));
    }

    private TypeMappedAnnotation(AnnotationTypeMapping mapping, ClassLoader classLoader,
                                 Object source, Object rootAnnotation,
                                 BiFunction<Method, Object, Object> valueExtractor, int aggregateIndex,
                                 boolean useMergedValues, Predicate<String> attributeFilter,
                                 int[] resolvedRootMirrors, int[] resolvedMirrors) {

        this.classLoader = classLoader;
        this.source = source;
        this.rootAttributes = rootAnnotation;
        this.valueExtractor = valueExtractor;
        this.mapping = mapping;
        this.aggregateIndex = aggregateIndex;
        this.useMergedValues = useMergedValues;
        this.attributeFilter = attributeFilter;
        this.resolvedRootMirrors = resolvedRootMirrors;
        this.resolvedMirrors = resolvedMirrors;
    }
    @Override
    public Class<A> getType() {
        return this.mapping.getAnnotationType();
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    public int getDistance() {
        return this.mapping.getDistance();
    }

    @Override
    public int getAggregateIndex() {
        return this.aggregateIndex;
    }

    @Override
    public Object getSource() {
        return this.source;
    }

    @Override
    public MergedAnnotation<?> getRoot() {
        if(getDistance() == 0)
            return this;
        AnnotationTypeMapping rootMapping = this.mapping.getRoot();
        return new TypeMappedAnnotation<>(rootMapping, this.classLoader, this.source,
                this.rootAttributes, this.valueExtractor, this.aggregateIndex, this.resolvedRootMirrors)
    }

    @Override
    public MergedAnnotation<?> getMetaSource() {
        AnnotationTypeMapping metaSourceMapping = this.mapping.getSource();
        if(metaSourceMapping == null)
            return null;
        return new TypeMappedAnnotation<>(metaSourceMapping, this.classLoader, this.source,
                this.rootAttributes, this.valueExtractor, this.aggregateIndex, this.resolvedRootMirrors);
    }

    @Override
    public List<Class<? extends Annotation>> getMetaTypes() {
        return this.mapping.getMetaTypes();
    }

    @Override
    public boolean hasDefaultValue(String attributeName) throws NoSuchElementException {
        int attributeIndex = getAttributeIndex(attributeName, true);
        Object value = getValue(attributeIndex, true, false);
        return (value == null || this.mapping.isEquivalentToDefaultValue(attributeIndex, value, this.valueExtractor));
    }

    @Override
    public <T extends Annotation> MergedAnnotation<T> getAnnotation(String attributeName, Class<T> type) throws NoSuchElementException {
        int attributeIndex = getAttributeIndex(attributeName, true);
        Method attribute = this.mapping.getAttributes().get(attributeIndex);
        return getRequiredValue(attributeIndex, attributeName);
    }

    @Override
    public <T extends Annotation> MergedAnnotation<T>[] getAnnotationArray(String attributeName, Class<T> type) throws NoSuchElementException {
        return new MergedAnnotation[0];
    }

    @Override
    public <T> Optional<T> getDefaultValue(String attributeName, Class<T> type) {
        return Optional.empty();
    }

    @Override
    protected <T> T getAttributeValue(String attributeName, Class<T> type) {
        return null;
    }

    @Override
    protected A createSynthesized() {
        return null;
    }

    private int getAttributeIndex(String attributeName, boolean required) {
        int attributeIndex = (isFiltered(attributeName) ? -1 : this.mapping.getAttributes().indexOf(attributeName));
        if (attributeIndex == -1 && required) {
            throw new NoSuchElementException("No attribute named '" + attributeName +
                    "' present in merged annotation " + getType().getName());
        }
        return attributeIndex;
    }

    private boolean isFiltered(String attributeName) {
        if(this.attributeFilter != null){
            return !this.attributeFilter.test(attributeName);
        }
        return false;
    }

    private Object getRequiredValue(int attributeIndex, String attributeName) {
        Object value = getValue(attributeIndex, Object.class);
        if(value == null){
            throw new NoSuchElementException("No element at attribute index "
                    + attributeIndex + " for name " + attributeName);
        }
        return value;
    }

    private <T> T getValue(int attributeIndex, Class<T> type) {
        Method attribute = this.mapping.getAttributes().get(attributeIndex);
        Object value = getValue(attributeIndex, true, false);
        if(value == null)
            value = attribute.getDefaultValue();
        return adapt(attribute, value, type);
    }

    private Object getValue(int attributeIndex, boolean useConventionMapping, boolean forMirrorResolution){
        AnnotationTypeMapping mapping = this.mapping;
        if (this.useMergedValues) {
            int mappedIndex = this.mapping.getAliasMapping(attributeIndex);
            if (mappedIndex == -1 && useConventionMapping) {
                mappedIndex = this.mapping.getConventionMapping(attributeIndex);
            }
            if (mappedIndex != -1) {
                mapping = mapping.getRoot();
                attributeIndex = mappedIndex;
            }
        }
        if (!forMirrorResolution) {
            attributeIndex =
                    (mapping.getDistance() != 0 ? this.resolvedMirrors : this.resolvedRootMirrors)[attributeIndex];
        }
        if(attributeIndex == -1)
            return null;
        if(mapping.getDistance() == 0){
            Method attribute = mapping.getAttributes().get(attributeIndex);
            Object result = this.valueExtractor.extract(attribute, this.rootAttributes);
            return (result != null ? result : attribute.getDefaultValue());
        }
        return getValueFromMetaAnnotation(attributeIndex, forMirrorResolution);
    }

    private Object getValueFromMetaAnnotation(int attributeIndex, boolean forMirrorResolution) {
        Object value = null;
        if(this.useMergedValues || forMirrorResolution)
            value = this.mapping.getMappedAnnotationValue(attributeIndex, forMirrorResolution);
        if(value == null){
            Method attribute = this.mapping.getAttributes().get(attributeIndex);
            value = ReflectionUtils.invokeMethod(attribute, this.mapping.getAnnotation());
        }
        return value;
    }

    private <T> T adapt(Method attribute, Object value, Class<T> type) {
        if(value == null)
            return null;
        value = adaptForAttribute(attribute, value);
        type = getAdaptType(attribute, type);
    }

    private Object adaptForAttribute(Method attribute, Object value){
        Class<?> attributeType = ClassUtils.resolvePrimitiveIfNecessary(attribute.getReturnType());
        if (attributeType.isArray() && !value.getClass().isArray()) {
            Object array = Array.newInstance(value.getClass(), 1);
            Array.set(array, 0, value);
            return adaptForAttribute(attribute, array);
        }
        if (attributeType.isAnnotation()) {
            return adaptToMergedAnnotation(value, (Class<? extends Annotation>) attributeType);
        }
        if (attributeType.isArray() && attributeType.getComponentType().isAnnotation()) {
            MergedAnnotation<?>[] result = new MergedAnnotation<?>[Array.getLength(value)];
            for (int i = 0; i < result.length; i++) {
                result[i] = adaptToMergedAnnotation(Array.get(value, i),
                        (Class<? extends Annotation>) attributeType.getComponentType());
            }
            return result;
        }
        if ((attributeType == Class.class && value instanceof String) ||
                (attributeType == Class[].class && value instanceof String[]) ||
                (attributeType == String.class && value instanceof Class) ||
                (attributeType == String[].class && value instanceof Class[])) {
            return value;
        }
        if (attributeType.isArray() && isEmptyObjectArray(value)) {
            return emptyArray(attributeType.getComponentType());
        }
        if (!attributeType.isInstance(value)) {
            throw new IllegalStateException("Attribute '" + attribute.getName() +
                    "' in annotation " + getType().getName() + " should be compatible with " +
                    attributeType.getName() + " but a " + value.getClass().getName() +
                    " value was returned");
        }
        return value;
    }

    private <T> Class<T> getAdaptType(Method attribute, Class<T> type) {
        if(type != Object.class)
            return type;
        Class<?> attributeType = attribute.getReturnType();
        if(attributeType.isAnnotation())
            return (Class<T>)MergedAnnotation.class;
        if(attributeType.isArray() && attributeType.getComponentType().isAnnotation())
            return (Class<T>)MergedAnnotation.class;
        return (Class<T>)ClassUtils.resolvePrimitiveIfNecessary(attributeType);
    }

    private Object emptyArray(Class<?> componentType) {
        Object result = EMPTY_ARRAYS.get(componentType);
        if(result == null)
            result = Array.newInstance(componentType, 0);
        return result;
    }

}
