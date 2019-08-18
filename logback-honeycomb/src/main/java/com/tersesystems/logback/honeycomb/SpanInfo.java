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
package com.tersesystems.logback.honeycomb;

import com.google.auto.value.AutoValue;

import java.time.Duration;
import java.util.UUID;
import java.util.function.Supplier;

@AutoValue
public abstract class SpanInfo {

    public static Builder builder() {
        return new AutoValue_SpanInfo.Builder();
    }

    public abstract Builder toBuilder();

    public abstract String spanId();

    @Nullable public abstract String parentId();

    public abstract String traceId();

    public abstract String name();

    public abstract String serviceName();

    public Duration duration() {
        return durationSupplier().get();
    }

    public abstract Supplier<Duration> durationSupplier();

    /**
     * Creates a child builder with the parent id set to the current span id,
     * a random UUID set to the span id.
     *
     * @return a child builder.
     */
    public Builder childBuilder() {
        return this.toBuilder()
                .setSpanId(UUID.randomUUID().toString())
                .setParentId(spanId());
    }

    @AutoValue.Builder
    abstract static class Builder {
        abstract Builder setName(String name);
        abstract Builder setSpanId(String spanId);
        abstract Builder setParentId(String parentId);
        abstract Builder setTraceId(String traceId);
        abstract Builder setServiceName(String serviceName);
        abstract Builder setDurationSupplier(Supplier<Duration> duration);
        abstract SpanInfo build();
    }
}
