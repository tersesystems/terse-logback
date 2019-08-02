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
package com.tersesystems.logback.ringbuffer.event;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.read.ListAppender;
import com.tersesystems.logback.ringbuffer.RingBuffer;
import com.tersesystems.logback.ringbuffer.RingBufferAware;
import com.tersesystems.logback.ringbuffer.RingBufferMarkerFactory;
import net.logstash.logback.encoder.LogstashEncoder;
import org.junit.jupiter.api.Test;
import org.slf4j.Marker;

import java.net.URL;
import java.nio.charset.StandardCharsets;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

public class MarkerEventRingBufferTurboFilterTest {

    @Test
    public void testWithDump() throws JoranException {
        LoggerContext loggerFactory = createLoggerFactory();

        RingBufferMarkerFactory markerFactory = new RingBufferMarkerFactory(10);
        Marker recordMarker = markerFactory.createRecordMarker();
        Marker dumpMarker = markerFactory.createTriggerMarker();

        Logger logger = loggerFactory.getLogger("com.example.Test");
        logger.debug(recordMarker, "debug one");
        logger.debug(recordMarker, "debug two");
        logger.debug(recordMarker, "debug three");
        logger.debug(recordMarker, "debug four");
        logger.error(dumpMarker, "Dump all the messages");

        ListAppender<ILoggingEvent> listAppender = getListAppender(loggerFactory);
        assertThat(listAppender.list.size()).isEqualTo(1);

        //System.out.println(dumpAsJson(listAppender.list.get(0), loggerFactory));
    }

    String dumpAsJson(ILoggingEvent loggingEvent, Context context) {
        LogstashEncoder logstashEncoder = new LogstashEncoder();
        logstashEncoder.setContext(context);
        logstashEncoder.start();
        return new String(logstashEncoder.encode(loggingEvent), StandardCharsets.UTF_8);
    }

    LoggerContext createLoggerFactory() throws JoranException {
        LoggerContext context = new LoggerContext();
        URL resource = getClass().getResource("/logback-with-eventmarker-ringbuffer.xml");
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);
        configurator.doConfigure(resource);
        return context;
    }

    ListAppender<ILoggingEvent> getListAppender(LoggerContext context) {
        Logger root = context.getLogger(Logger.ROOT_LOGGER_NAME);
        return (ListAppender<ILoggingEvent>) requireNonNull(root.getAppender("LIST"));
    }

    @SuppressWarnings("unchecked")
    RingBuffer<LoggingEvent> getRingBuffer(Marker marker) {
        return ((RingBufferAware<LoggingEvent>) marker).getRingBuffer();
    }
}
