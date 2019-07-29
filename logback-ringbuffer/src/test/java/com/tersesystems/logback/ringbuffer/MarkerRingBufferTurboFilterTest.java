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
package com.tersesystems.logback.ringbuffer;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.slf4j.Marker;

import java.net.URL;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

public class MarkerRingBufferTurboFilterTest {

    @Test
    public void testWithInfo() throws JoranException {
        LoggerContext loggerFactory = createLoggerFactory();

        RingBufferMarkerFactory markerFactory = new RingBufferMarkerFactory(10);
        Marker recordMarker = markerFactory.createRecordMarker();
        Marker dumpMarker = markerFactory.createTriggerMarker();

        Logger logger = loggerFactory.getLogger("com.example.Test");
        logger.info(recordMarker, "info stuff");
        logger.error(dumpMarker, "Dump all the messages");

        RingBuffer<LoggingEvent> ringBuffer = getRingBuffer(dumpMarker);
        ListAppender<ILoggingEvent> listAppender = getListAppender(loggerFactory);
        assertThat(listAppender.list.size()).isEqualTo(2);
        assertThat(ringBuffer.size()).isEqualTo(0);
    }

    @Test
    public void testWithInfoWithoutDump() throws JoranException {
        LoggerContext loggerFactory = createLoggerFactory();

        RingBufferMarkerFactory markerFactory = new RingBufferMarkerFactory(10);
        Marker recordMarker = markerFactory.createRecordMarker();
        Marker triggerMarker = markerFactory.createTriggerMarker();

        Logger logger = loggerFactory.getLogger("com.example.Test");
        logger.info(recordMarker, "info stuff");
        logger.debug(recordMarker, "debug stuff"); // debug statement never gets appended here
        logger.error("Don't dump all the messages");
        logger.error(triggerMarker,"Now dump them");

        ListAppender<ILoggingEvent> listAppender = getListAppender(loggerFactory);
        assertThat(listAppender.list.size()).isEqualTo(4);
    }

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
        assertThat(listAppender.list.size()).isEqualTo(5);
    }

    @Test
    public void testWithNoRecord() throws JoranException {
        LoggerContext loggerFactory = createLoggerFactory();
        RingBufferMarkerFactory markerFactory = new RingBufferMarkerFactory(10);
        Marker dumpMarker = markerFactory.createTriggerMarker();

        Logger logger = loggerFactory.getLogger("com.example.Test");
        logger.debug( "debug one");
        logger.debug( "debug two");
        logger.debug( "debug three");
        logger.debug( "debug four");
        logger.error(dumpMarker, "Dump all the messages");

        ListAppender<ILoggingEvent> listAppender = getListAppender(loggerFactory);
        assertThat(listAppender.list.size()).isEqualTo(1);
    }

    @Test
    public void testWithTwoFlows() throws JoranException {
        LoggerContext loggerFactory = createLoggerFactory();
        RingBufferMarkerFactory factory1 = new RingBufferMarkerFactory(10);
        RingBufferMarkerFactory factory2 = new RingBufferMarkerFactory(10);
        Marker record1 = factory1.createRecordMarker();
        Marker trigger1 = factory1.createTriggerMarker();

        Marker record2 = factory2.createRecordMarker();
        Marker trigger2 = factory2.createTriggerMarker();

        Logger logger = loggerFactory.getLogger("com.example.Test");
        logger.debug(record1, "debug one with 1st ringbuffer");
        logger.debug(record2, "debug two with 2nd ringbuffer");
        logger.debug(record2, "debug three with 2nd ringbuffer");
        logger.debug(record2, "debug four with 2nd ringbuffer");
        logger.error(trigger2, "Dump messages recorded with record2");

        ListAppender<ILoggingEvent> listAppender = getListAppender(loggerFactory);
        assertThat(listAppender.list.size()).isEqualTo(4);

        logger.error(trigger1, "Dump messages recorded with record1");
        assertThat(listAppender.list.size()).isEqualTo(6);
    }

    LoggerContext createLoggerFactory() throws JoranException {
        LoggerContext context = new LoggerContext();
        URL resource = getClass().getResource("/logback-with-marker-ringbuffer.xml");
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
