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
package com.tersesystems.logback.context.logstash;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import net.logstash.logback.composite.loggingevent.LogstashMarkersJsonProvider;
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;
import org.junit.Test;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class LogstashLoggerTest {

    static class Foo {
        private final org.slf4j.Logger logger;

        Foo(org.slf4j.Logger logger) {
            this.logger = logger;
        }

        public void doThat() {
            logger.info("hello world");
        }

        public void doThatWithMarker() {
            LogstashMarker marker = Markers.append("key2", "value2");
            logger.info(marker, "hello world with marker");
        }
    }

    @Test
    public void testContext() throws Exception {
        LogstashLogger logger = LogstashLoggerFactory.create().getLogger(Foo.class);
        ListAppender<ILoggingEvent> listAppender = addAppender(logger);

        LogstashContext context = LogstashContext.create("context1", "value1");
        Logger proxyLogger = new LogstashLogger(context, logger);

        // call method under test
        Foo foo = new Foo(proxyLogger);
        foo.doThat();

        ILoggingEvent event = listAppender.list.get(0);
        String actual = serializeMarker(event);
        assertThat(actual).isEqualTo("{\"context1\":\"value1\"}");
    }

    @Test
    public void testContextWithMarker() throws Exception {
        LogstashLogger logger = LogstashLoggerFactory.create().getLogger(Foo.class);
        ListAppender<ILoggingEvent> listAppender = addAppender(logger);

        LogstashContext context1 = LogstashContext.create("context1", "value1");
        Logger proxyLogger = new LogstashLogger(context1, logger);

        // call method under test
        Foo foo = new Foo(proxyLogger);
        foo.doThatWithMarker();

        LogstashMarker contextMarker = Markers.appendEntries(Collections.singletonMap("context1", "value1"));
        ILoggingEvent event = listAppender.list.get(0);
        String actual = serializeMarker(event);
        assertThat(actual).isEqualTo("{\"context1\":\"value1\",\"key2\":\"value2\"}");
    }

    @Test
    public void testContextBuilding() throws Exception {
        LogstashContext context1 = LogstashContext.create("context1", "value1");
        LogstashLogger proxyLogger = LogstashLoggerFactory.create(context1).getLogger(Foo.class);
        ListAppender<ILoggingEvent> listAppender = addAppender(proxyLogger);

        LogstashContext context2 = LogstashContext.create("context2", "value2");
        LogstashLogger loggerWithBothContext = proxyLogger.withContext(context2);
        Foo foo = new Foo(loggerWithBothContext);
        foo.doThat();

        ILoggingEvent event = listAppender.list.get(0);
        String actual = serializeMarker(event);
        assertThat(actual).isEqualTo("{\"context2\":\"value2\",\"context1\":\"value1\"}");
    }

    @Test
    public void testContextMerge() throws Exception {
        LogstashLogger logger = LogstashLoggerFactory.create().getLogger(Foo.class);
        ListAppender<ILoggingEvent> listAppender = addAppender(logger);

        LogstashContext context1 = LogstashContext.create("context1", "value1");
        LogstashContext context2 = LogstashContext.create("context2", "value2");
        LogstashLogger logger1 = new LogstashLogger(context1, logger);
        LogstashLogger logger2 = new LogstashLogger(context2, logger1);

        // call method under test
        Foo foo = new Foo(logger2);
        foo.doThat();

        // Check that the output includes the content of contexts and markers.
        ILoggingEvent event = listAppender.list.get(0);
        String actual = serializeMarker(event);
        assertThat(actual).isEqualTo("{\"context1\":\"value1\",\"context2\":\"value2\"}");
    }

    private ListAppender<ILoggingEvent> addAppender(LogbackLoggerAware logstashLogger) {
        ch.qos.logback.classic.Logger underlyingLogger = logstashLogger.getLogbackLogger().get();
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        underlyingLogger.addAppender(listAppender);
        return listAppender;
    }

    private String serializeMarker(ILoggingEvent event) throws IOException {
        LogstashMarkersJsonProvider provider = new LogstashMarkersJsonProvider();
        StringWriter writer = new StringWriter();
        JsonFactory factory = new MappingJsonFactory();
        JsonGenerator generator = factory.createGenerator(writer);
        generator.writeStartObject();
        assertThat(writer.toString()).isEqualTo("");
        provider.writeTo(generator, event);
        generator.writeEndObject();
        generator.flush();
        return writer.toString();
    }
}
