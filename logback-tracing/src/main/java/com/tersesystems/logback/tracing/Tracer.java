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
package com.tersesystems.logback.tracing;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Supplier;

public class Tracer {

    // We don't want to measure a stack more than 300 elements deep, because after that it's just no fun.
    private static final int MAX_THREAD_SIZE = 300;

    // Don't use Stack, it has synchronized methods.
    private static final ThreadLocal<Queue<SpanInfo>> threadLocal = ThreadLocal.withInitial(() -> new ArrayBlockingQueue<>(MAX_THREAD_SIZE));

    private static Queue<SpanInfo> stack() {
        return threadLocal.get();
    }

    public static Optional<SpanInfo> popSpan() {
        return Optional.ofNullable(stack().poll());
    }

    public static SpanInfo pushSpan(String name, String serviceName, Supplier<String> idGenerator) {
        Queue<SpanInfo> stack = stack();
        SpanInfo parent = stack.peek();

        SpanInfo span;
        if (parent != null) {
            span = parent.childBuilder().setName(name).buildNow();
        } else {
            span = SpanInfo.builder().setRootSpan(idGenerator, name).setServiceName(serviceName).buildNow();
        }
        stack.offer(span);
        return span;
    }

    public static Optional<SpanInfo> activeSpan() {
        Queue<SpanInfo> stack = stack();
        return !stack.isEmpty() ? Optional.ofNullable(stack.peek()) : Optional.empty();
    }

    public static void clear() {
        stack().clear();
    }

}
