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

import com.google.auto.value.AutoValue;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

@AutoValue
public abstract class SpanInfo {

    public static Builder builder() {
        return new AutoValue_SpanInfo.Builder();
    }

    public abstract Builder toBuilder();

    public abstract String spanId();

    @Nullable
    public abstract String parentId();

    public abstract String traceId();

    public abstract String name();

    public abstract String serviceName();

    public abstract Instant startTime();

    public Duration duration() {
        return durationSupplier().get();
    }

    public abstract Supplier<Duration> durationSupplier();

    public abstract Supplier<String> idGenerator();


    /**
     * Creates a child builder with the parent id set to the current span id,
     * a random UUID set to the span id.
     *
     * @return a child builder.
     */
    public Builder childBuilder() {
        return this.toBuilder()
                .setSpanId(idGenerator().get())
                .setIdGenerator(idGenerator())
                .setParentId(spanId());
    }

    /**
     * Provides a function with a child that can be used as a
     * convenience wrapper, which calls {@code childBuilder().setName().buildNow()}
     * under the hood.
     *
     * {@code <pre>return parentSpanInfo.withChild("doThing", childSpan -> {
     *    return doThing(childSpan);
     * });
     * </pre>}
     *
     * @param methodName the name of the child span
     * @param childFunction the child function
     * @param <T> the type of the return value
     * @return the return value
     */
    public <T> T withChild(String methodName, Function<SpanInfo, T> childFunction) {
        return childFunction.apply(childBuilder().setName(methodName).buildNow());
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setName(String name);
        public abstract Builder setSpanId(String spanId);
        public abstract Builder setParentId(String parentId);
        public abstract Builder setTraceId(String traceId);
        public abstract Builder setIdGenerator(Supplier<String> idGenerator);
        public abstract Builder setStartTime(Instant startTime);
        public abstract Builder setServiceName(String serviceName);
        public abstract Builder setDurationSupplier(Supplier<Duration> duration);
        public abstract SpanInfo build();

        /**
         * Creates a random UUID for the trace id and span id and set the name.
         *
         * @param name the span name
         * @return the configured builder.
         */
        public Builder setRootSpan(Supplier<String> idGenerator, String name) {
            return this.setTraceId(idGenerator.get())
                    .setSpanId(idGenerator.get())
                    .setIdGenerator(idGenerator)
                    .setName(name);
        }

        public Builder startNow() {
            Instant startTime = Instant.now();
            return this
                    .setDurationSupplier(() -> Duration.between(startTime, Instant.now()))
                    .setStartTime(startTime);
        }

        /**
         * Builds a span info, setting the duration supplier to be {@code Duration.between(now, Instant.now())}
         */
        public SpanInfo buildNow() {
            return startNow().build();
        }
    }
}
