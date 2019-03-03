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
package example;

import com.tersesystems.logback.context.TracerFactory;
import com.tersesystems.logback.context.Context;
import com.tersesystems.logback.context.logstash.AbstractLogstashContext;
import com.tersesystems.logback.context.logstash.AbstractLogstashLoggerFactory;
import com.tersesystems.logback.context.logstash.AbstractLogstashLogger;
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;


class AppContext extends AbstractLogstashContext<AppContext> {

    public static final String CORRELATION_ID = "correlationId";
    private final boolean tracer;

    protected AppContext(Map<?, ?> entries, boolean tracer) {
        super(entries);
        this.tracer = tracer;
    }

    public static AppContext create() {
        return new AppContext(Collections.emptyMap(), false);
    }

    public static AppContext create(Object key, Object value) {
        return new AppContext(Collections.singletonMap(key, value), false);
    }

    public Optional<String> getCorrelationId() {
        return Stream.of(entries().get(CORRELATION_ID))
                .map(cid -> (String) cid)
                .findFirst();
    }

    public AppContext withCorrelationId(String correlationId) {
        return and(AppContext.create(CORRELATION_ID, correlationId));
    }

    public AppContext withTracer() {
        return create(entries(), true);
    }

    public boolean isTracingEnabled() {
        return tracer;
    }

    @Override
    public LogstashMarker asMarker() {
        if (isTracingEnabled()) {
            return Markers.appendEntries(entries()).and(TracerFactory.getInstance().createTracer());
        } else {
            return Markers.appendEntries(entries());
        }
    }

    @Override
    public AppContext and(Context<? extends Marker, ?> otherContext) {
        boolean otherTracing = (otherContext instanceof AppContext) && ((AppContext) otherContext).isTracingEnabled();
        // XXX Same as LogstashContext -- is there a way to access this directly?
        Map<Object, Object> mergedEntries = new HashMap<>(this.entries());
        mergedEntries.putAll(otherContext.entries());
        return new AppContext(mergedEntries, this.isTracingEnabled() || otherTracing);
    }

}

class AppLogger extends AbstractLogstashLogger<AppContext, Logger, AppLogger> {

    public AppLogger(AppContext context, Logger logger) {
        super(context, logger);
    }

    @Override
    public AppLogger withContext(AppContext otherContext) {
        return new AppLogger(this.context.and(otherContext), this.logger);
    }
}

class AppLoggerFactory extends AbstractLogstashLoggerFactory<AppContext, AppLogger, ILoggerFactory, AppLoggerFactory> {

    protected AppLoggerFactory(AppContext context, ILoggerFactory loggerFactory) {
        super(context, loggerFactory);
    }

    @Override
    public AppLoggerFactory withContext(AppContext context) {
        return new AppLoggerFactory(getContext().and(context), getILoggerFactory());
    }

    @Override
    public AppLogger getLogger(String name) {
        return new AppLogger(AppContext.create(), getILoggerFactory().getLogger(name));
    }

    public static AppLoggerFactory create() {
        return create(AppContext.create());
    }

    public static AppLoggerFactory create(AppContext context) {
        return new AppLoggerFactory(context, LoggerFactory.getILoggerFactory());
    }

}