package com.tersesystems.logback.exceptionmapping;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ExceptionHierarchyIterator implements Iterator<Class<?>> {
    private Class<?> clazz;

    ExceptionHierarchyIterator(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public boolean hasNext() {
        return clazz != null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<?> next() {
        Class<?> oldClass = clazz;
        if (clazz != null) {
            clazz = clazz.getSuperclass();
        }
        return oldClass;
    }

    @SuppressWarnings("unchecked")
    public Stream<Class<?>> stream() {
        Spliterator spliterator = Spliterators.spliteratorUnknownSize(this, 0);
        return (Stream<Class<?>>) StreamSupport.stream(spliterator, false);
    }

    public static ExceptionHierarchyIterator create(Class<?> clazz) {
        return new ExceptionHierarchyIterator(clazz);
    }
}
