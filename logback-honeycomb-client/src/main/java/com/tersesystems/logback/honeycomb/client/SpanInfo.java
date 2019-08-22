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
package com.tersesystems.logback.honeycomb.client;

import com.google.auto.value.AutoValue;

import java.time.Duration;
import java.time.Instant;
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
    public abstract static class Builder {
        public abstract Builder setName(String name);
        public abstract Builder setSpanId(String spanId);
        public abstract Builder setParentId(String parentId);
        public abstract Builder setTraceId(String traceId);
        public abstract Builder setServiceName(String serviceName);
        public abstract Builder setDurationSupplier(Supplier<Duration> duration);
        public abstract SpanInfo build();

        /**
         * Creates a random UUID for the trace id and span id and set the name.
         *
         * @param name the span name
         * @return the configured builder.
         */
        public Builder setRootSpan(String name) {
            return this.setTraceId(UUID.randomUUID().toString())
                    .setSpanId(UUID.randomUUID().toString())
                    .setName(name);
        }

        /**
         * Builds a span info, setting the duration supplier to be {@code Duration.between(now, Instant.now())}
         */
        public SpanInfo buildNow() {
            Instant now = Instant.now();
            return this.setDurationSupplier(() -> Duration.between(now, Instant.now())).build();
        }
    }
}
