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
package com.tersesystems.logback.bytebuddy;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;

public class MethodInfoLookup {

    private final ConcurrentMap<String, Set<MethodInfo>> classNameToMethods = new ConcurrentHashMap<>();

    static MethodInfoLookup getInstance() {
        return SingletonHolder.instance;
    }

    static class SingletonHolder {
       public static MethodInfoLookup instance = new MethodInfoLookup();
    }

    public void add(String className, MethodInfo methodInfo) {
        Set<MethodInfo> infos = classNameToMethods.computeIfAbsent(className, k -> new HashSet<>());
        infos.add(methodInfo);
    }

    public Optional<MethodInfo> find(String className, String methodName, String descriptor) {
        Set<MethodInfo> infos = classNameToMethods.computeIfAbsent(className, k -> new HashSet<>());
        return infos.stream().filter(matchingInfo(methodName, descriptor)).findFirst();
    }

    private Predicate<MethodInfo> matchingInfo( String methodName, String descriptor) {
        return info -> Objects.equals(info.methodName, methodName)
                && Objects.equals(descriptor, info.descriptor);
    }
}

