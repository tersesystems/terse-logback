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
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.slf4j.Marker;

import java.net.URL;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

public class ThresholdRingBufferTurboFilterTest {
    @Test
    public void testWithDebug() throws JoranException {
        LoggerContext loggerContext = createLoggerContext();

        Logger logger = loggerContext.getLogger("com.example.Test");
        logger.debug( "debug stuff");

        RingBuffer<LoggingEvent> ringBuffer = getRingBuffer(loggerContext);
        ListAppender<ILoggingEvent> listAppender = getListAppender(loggerContext);
        assertThat(listAppender.list.size()).isEqualTo(0);
        assertThat(ringBuffer.size()).isEqualTo(1);
    }

    @Test
    public void testWithDebugAndInfo() throws JoranException {
        LoggerContext loggerContext = createLoggerContext();

        Logger logger = loggerContext.getLogger("com.example.Test");
        logger.debug( "debug stuff");
        logger.info( "info stuff");

        RingBuffer<LoggingEvent> ringBuffer = getRingBuffer(loggerContext);
        ListAppender<ILoggingEvent> listAppender = getListAppender(loggerContext);
        assertThat(listAppender.list.size()).isEqualTo(1);
        assertThat(ringBuffer.size()).isEqualTo(1);
    }

    @Test
    public void testWithDebugAndError() throws JoranException {
        LoggerContext loggerContext = createLoggerContext();

        Logger logger = loggerContext.getLogger("com.example.Test");
        logger.debug( "debug stuff");
        logger.error( "Dump all the messages");

        RingBuffer<LoggingEvent> ringBuffer = getRingBuffer(loggerContext);
        ListAppender<ILoggingEvent> listAppender = getListAppender(loggerContext);
        assertThat(listAppender.list.size()).isEqualTo(2);
        assertThat(ringBuffer.size()).isEqualTo(0);
    }

    @Test
    public void testWithDebugAndInfoAndError() throws JoranException {
        LoggerContext loggerContext = createLoggerContext();

        Logger logger = loggerContext.getLogger("com.example.Test");
        logger.debug( "debug stuff");
        logger.info( "info stuff");
        logger.error( "info stuff");

        RingBuffer<LoggingEvent> ringBuffer = getRingBuffer(loggerContext);
        ListAppender<ILoggingEvent> listAppender = getListAppender(loggerContext);
        assertThat(listAppender.list.size()).isEqualTo(3);
        assertThat(ringBuffer.size()).isEqualTo(0);
    }

    @SuppressWarnings("unchecked")
    private RingBuffer<LoggingEvent> getRingBuffer(LoggerContext loggerContext) {
        TurboFilter filter = loggerContext.getTurboFilterList().stream()
                .filter(tf -> tf instanceof RingBufferAware<?>)
                .findFirst()
                .get();
        return ((RingBufferAware<LoggingEvent>) filter).getRingBuffer();
    }

    @Test
    public void testWithDump() throws JoranException {
        LoggerContext loggerFactory = createLoggerContext();

        Logger logger = loggerFactory.getLogger("com.example.Test");
        logger.debug( "debug one");
        logger.debug( "debug two");
        logger.debug( "debug three");
        logger.debug( "debug four");
        logger.error( "Dump all the messages");

        ListAppender<ILoggingEvent> listAppender = getListAppender(loggerFactory);
        assertThat(listAppender.list.size()).isEqualTo(5);
    }

    LoggerContext createLoggerContext() throws JoranException {
        LoggerContext context = new LoggerContext();
        URL resource = getClass().getResource("/logback-with-threshold-ringbuffer.xml");
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);
        configurator.doConfigure(resource);
        return context;
    }

    ListAppender<ILoggingEvent> getListAppender(LoggerContext context) {
        Logger root = context.getLogger(Logger.ROOT_LOGGER_NAME);
        return (ListAppender<ILoggingEvent>) requireNonNull(root.getAppender("LIST"));
    }

}
