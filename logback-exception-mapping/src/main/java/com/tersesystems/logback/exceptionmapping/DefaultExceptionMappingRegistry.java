/*
 * SPDX-License-Identifier: CC0-1.0
 *
 * Copyright 2018-2019 Will Sargent.
 *
 * Licensed under the CC0 Public Domain Dedication;
 * You may obtain a copy of the License at
 *
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */
package com.tersesystems.logback.exceptionmapping;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

public class DefaultExceptionMappingRegistry implements ExceptionMappingRegistry {
    private Consumer<Exception> exceptionHandler;
    private Map<String, ExceptionMapping> classNameToMappings;

    public DefaultExceptionMappingRegistry(Consumer<Exception> exceptionHandler) {
        this.classNameToMappings = new ConcurrentHashMap<>();
        this.exceptionHandler = exceptionHandler;
    }

    //---------------------
    // register maps

    @Override
    public void register(ClassLoader classLoader, Map<String, List<String>> mappers) {
        mappers.forEach((className, methodNames) ->
                register(classLoader, className, methodNames.toArray(new String[0]))
        );
    }

    @Override
    public void register(Map<String, List<String>> mappers) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        mappers.forEach((className, methodNames) ->
                register(classLoader, className, methodNames.toArray(new String[0]))
        );
    }

    //---------------------
    // register methodNames

    @Override
    public void register(String className, String... methodNames) {
        register(ClassLoader.getSystemClassLoader(), className, methodNames);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void register(ClassLoader classLoader, String className, String... methodNames) {
        try {
            Class<? extends Throwable> clazz = (Class<? extends Throwable>) classLoader.loadClass(className);
            register(clazz, methodNames);
        } catch (Exception e) {
            exceptionHandler.accept(e);
        }
    }

    @Override
    public <E extends Throwable> void register(Class<E> exceptionClass, String... propertyNames) {
        register(new BeanExceptionMapping(exceptionClass.getName(), asList(propertyNames), exceptionHandler));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends Throwable> void register(Class<E> exceptionClass, Function<E, List<ExceptionProperty>> f) {
        register(new FunctionExceptionMapping(exceptionClass.getName(), (Function<Throwable, List<ExceptionProperty>>) f));
    }

    @Override
    public void register(String className, Function<Throwable, List<ExceptionProperty>> f) {
        register(new FunctionExceptionMapping(className, f));
    }

    //---------------------
    // register ExceptionMapping

    @Override
    public void register(ExceptionMapping mapping) {
        classNameToMappings.put(mapping.getName(), mapping);
    }

    //---------------------
    // apply

    @Override
    public List<ExceptionProperty> apply(Throwable e) {
        Stream<Class<?>> classStream = new ExceptionHierarchyIterator(e.getClass()).stream();
        List<ExceptionMapping> exceptionMappings = classStream.map(Class::getName)
                .filter(className -> classNameToMappings.containsKey(className))
                .map(className -> classNameToMappings.get(className))
                .collect(Collectors.toList());

        List<ExceptionProperty> exceptionProperties = new ArrayList<>();
        for (ExceptionMapping exceptionMapping : exceptionMappings) {
            exceptionProperties.addAll(exceptionMapping.apply(e));
        }
        return exceptionProperties;
    }

    @Override
    public Iterator<ExceptionMapping> iterator() {
        return classNameToMappings.values().iterator();
    }

    @Override
    public ExceptionMapping get(String name) {
        return classNameToMappings.get(name);
    }

    @Override
    public boolean contains(ExceptionMapping exceptionMapping) {
        return classNameToMappings.containsKey(exceptionMapping.getName());
    }

    @Override
    public boolean remove(ExceptionMapping exceptionMapping) {
        return classNameToMappings.remove(exceptionMapping.getName()) != null;
    }

    @Override
    public boolean remove(String name) {
        return classNameToMappings.remove(name) != null;
    }

}
