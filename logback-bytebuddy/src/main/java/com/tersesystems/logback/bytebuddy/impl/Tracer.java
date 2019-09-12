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
package com.tersesystems.logback.bytebuddy.impl;

import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;
import org.slf4j.Marker;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Stack;
import java.util.UUID;

import static net.logstash.logback.marker.Markers.aggregate;
import static net.logstash.logback.marker.Markers.append;

public class Tracer {

    private static LogstashMarker serviceNameMarker = Markers.empty();

    public static void setServiceName(String serviceName) {
        serviceNameMarker = append("service_name", serviceName);
    }

    private static final ThreadLocal<Stack<Span>> threadLocal = ThreadLocal.withInitial(Stack::new);

    private static Stack<Span> stack() {
        return threadLocal.get();
    }

    static Optional<Span> popSpan() {
        return Optional.ofNullable(stack().pop());
    }

    static void pushSpan(String name) {
        Span span;
        if (!stack().empty()) {
            Span parent = stack().peek();
            String traceId = parent.traceId;
            String parentId = parent.spanId;
            span = new Span(name, traceId, parentId);
        } else {
            span = new Span(name, UUID.randomUUID().toString(), UUID.randomUUID().toString());
        }
        stack().push(span);
    }

    static Marker createExitMarkers(Span span, Marker... markers) {
        LogstashMarker traceMarker = append("trace.trace_id", span.traceId);
        LogstashMarker spanMarker = append("trace.span_id", span.spanId);
        LogstashMarker durationMsMarker = append("duration_ms", span.durationMs());
        LogstashMarker nameMarker = append("name", span.name());
        LogstashMarker baseMarkers = aggregate(markers)
                .and(traceMarker)
                .and(nameMarker)
                .and(spanMarker)
                .and(serviceNameMarker)
                .and(durationMsMarker);

        if (span.parentId != null) {
            LogstashMarker parentMarker = append("trace.parent_id", span.parentId);
            return baseMarkers.and(parentMarker);
        } else {
            return baseMarkers;
        }
    }

    static class Span {
        private final Instant startTime;
        private final String spanId;
        private final String name;
        private final String traceId;
        private final String parentId;

        Span(String name, String traceId, String parentId) {
            this.name = name;
            this.traceId = traceId;
            this.parentId = parentId;
            this.startTime = Instant.now();
            this.spanId = UUID.randomUUID().toString();
        }

        public String getSpanId() {
            return spanId;
        }

        public Instant getStartTime() {
            return startTime;
        }

        public Long durationMs() {
            return Duration.between(startTime, Instant.now()).toMillis();
        }

        public String name() {
            return name;
        }
    }

}
