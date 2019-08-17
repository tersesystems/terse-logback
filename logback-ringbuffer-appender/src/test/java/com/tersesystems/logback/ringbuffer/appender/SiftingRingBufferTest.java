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
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.sift.AppenderTracker;
import com.tersesystems.logback.classic.LogbackUtils;
import com.tersesystems.logback.classic.sift.DiscriminatingMarkerFactory;
import com.tersesystems.logback.core.RingBuffer;
import org.junit.jupiter.api.Test;
import org.slf4j.Marker;

import java.net.URL;
import java.util.Optional;

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

        SiftingAppender siftingAppender = LogbackUtils.getSiftingAppender(loggerContext, "SIFT").get();
        AppenderTracker<ILoggingEvent> appenderTracker = siftingAppender.getAppenderTracker();
        assertThat(appenderTracker.getComponentCount()).isEqualTo(2);

        RingBuffer<ILoggingEvent> defaultRingBuffer = getRingBuffer("SIFT", "default").get();
        assertThat(defaultRingBuffer.size()).isEqualTo(1);

        RingBuffer<ILoggingEvent> specialRingBuffer= getRingBuffer("SIFT", "SPECIAL").get();
        assertThat(specialRingBuffer.size()).isEqualTo(1);
    }

    private Optional<RingBuffer<ILoggingEvent>> getRingBuffer(String appenderName, String key) {
        return LogbackUtils.getSiftingAppender(appenderName)
                .flatMap(a -> LogbackUtils.getAppenderByKey(a, key))
                .flatMap(LogbackUtils::getRingBuffer);
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
