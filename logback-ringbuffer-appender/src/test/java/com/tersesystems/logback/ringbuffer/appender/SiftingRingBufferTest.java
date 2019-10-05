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

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.joran.spi.JoranException;
import com.tersesystems.logback.classic.Utils;
import com.tersesystems.logback.classic.sift.DiscriminatingMarkerFactory;
import com.tersesystems.logback.core.RingBuffer;
import java.net.URL;
import org.junit.jupiter.api.Test;
import org.slf4j.Marker;

public class SiftingRingBufferTest {

  @Test
  public void testSiftingRingBuffer() throws JoranException {
    DiscriminatingMarkerFactory markerFactory =
        DiscriminatingMarkerFactory.create(this::discriminate);
    Marker marker = markerFactory.createMarker();

    LoggerContext loggerContext = createLoggerContext();
    Logger logger = loggerContext.getLogger("com.example.Foo");
    logger.info(marker, "this is pretty ordinary");
    logger.info(marker, "this is SPECIAL");

    RingBuffer<ILoggingEvent> defaultRingBuffer = getRingBuffer(loggerContext, "SIFT", "default");
    assertThat(defaultRingBuffer.size()).isEqualTo(1);

    RingBuffer<ILoggingEvent> specialRingBuffer = getRingBuffer(loggerContext, "SIFT", "SPECIAL");
    assertThat(specialRingBuffer.size()).isEqualTo(1);
  }

  private RingBuffer<ILoggingEvent> getRingBuffer(
      LoggerContext context, String appenderName, String key) {
    return Utils.create(context).<ILoggingEvent>getRingBuffer(appenderName, key).get();
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
