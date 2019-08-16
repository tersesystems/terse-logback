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
import ch.qos.logback.classic.sift.SiftingAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.read.CyclicBufferAppender;
import ch.qos.logback.core.sift.AppenderTracker;
import ch.qos.logback.core.sift.DefaultDiscriminator;
import com.tersesystems.logback.classic.sift.DiscriminatingMarker;
import com.tersesystems.logback.classic.sift.DiscriminatingMarkerFactory;
import com.tersesystems.logback.core.IdentityRingBufferAppender;
import com.tersesystems.logback.core.RingBufferAppender;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.LoggerFactory;
import org.slf4j.Marker;

import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

public class SiftingRingBufferTest {

    @Test
    public void testSiftingRingBuffer() throws JoranException {
        DiscriminatingMarkerFactory markerFactory = DiscriminatingMarkerFactory.create(this::discriminate);
        Marker marker = markerFactory.createMarker();

        LoggerContext loggerContext = createLoggerContext();
        Logger logger = loggerContext.getLogger("com.example.Foo");
        logger.info(marker, "this is pretty ordinary");
        logger.info(marker, "this is SPECIAL");

        Logger rootLogger = loggerContext.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

        SiftingAppender siftingAppender = (SiftingAppender) rootLogger.getAppender("SIFT");
        AppenderTracker<ILoggingEvent> appenderTracker = siftingAppender.getAppenderTracker();
        assertThat(appenderTracker.getComponentCount()).isEqualTo(2);

        IdentityRingBufferAppender<ILoggingEvent> defaultAppender = (IdentityRingBufferAppender<ILoggingEvent>)appenderTracker.find("default");
        assertThat(defaultAppender.getRingBuffer().size()).isEqualTo(1);
        IdentityRingBufferAppender<ILoggingEvent> specialAppender = (IdentityRingBufferAppender<ILoggingEvent>) appenderTracker.find("SPECIAL");
        assertThat(specialAppender.getRingBuffer().size()).isEqualTo(1);
    }

    String discriminate(ILoggingEvent event) {
        if (event.getFormattedMessage().contains("SPECIAL")) {
            return "SPECIAL";
        } else {
            return null;
        }
    }

    LoggerContext createLoggerContext() throws JoranException {
        LoggerContext context = new LoggerContext();
        URL resource = getClass().getResource("/logback-sifting-ringbuffer-appender.xml");
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);
        configurator.doConfigure(resource);
        return context;
    }

}
