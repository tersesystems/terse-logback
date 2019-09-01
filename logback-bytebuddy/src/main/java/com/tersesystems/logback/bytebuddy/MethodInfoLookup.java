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

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class MethodInfoLookup implements Consumer<MethodInfo> {

    private final ConcurrentLinkedQueue<MethodInfo> queue = new ConcurrentLinkedQueue<>();

    static MethodInfoLookup getInstance() {
        return SingletonHolder.instance;
    }

    static class SingletonHolder {
       static MethodInfoLookup instance = new MethodInfoLookup();
    }

    @Override
    public void accept(MethodInfo methodInfo) {
        queue.add(methodInfo);
    }

    public Optional<MethodInfo> find(String declaringType, String name, String signature) {
        return queue
                .stream()
                .filter(info -> Objects.equals(info.declaringType, declaringType)
                && Objects.equals(info.internalName, name)
                && compareSignatureAndDescriptor(signature, info.descriptor))
                .findFirst();
    }

    private boolean compareSignatureAndDescriptor(String signature, String descriptor) {
        // TODO rationalize signature vs descriptor format.
        //System.out.println("signature = " + signature + ", descriptor = " + descriptor);
        return true;
    }
}

