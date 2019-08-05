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
package com.tersesystems.logback.ringbuffer.appender;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.read.ListAppender;
import net.logstash.logback.encoder.LogstashEncoder;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.nio.charset.StandardCharsets;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

public class AppenderRingBufferTurboFilterTest {

    @Test
    public void testWithDump() throws JoranException {
        LoggerContext loggerFactory = createLoggerFactory();

        // These go into the cyclic buffer because com.example.Debug is on
        Logger debugLogger = loggerFactory.getLogger("com.example.Debug");
        debugLogger.debug("debug one");
        debugLogger.debug("debug two");
        debugLogger.debug("debug three");
        debugLogger.debug("debug four");

        // These don't go into the debug appender, because the logger is not set to DEBUG level
        Logger debugOffLogger = loggerFactory.getLogger("com.example.NotDebug");
        debugOffLogger.debug("this does not get added");

        // An error statement dumps and flushes the cyclic barrier.
        Logger logger = loggerFactory.getLogger("com.example.Test");
        logger.error( "Dump all the messages");

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
        URL resource = getClass().getResource("/logback-ringbuffer-appender.xml");
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
