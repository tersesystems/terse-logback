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

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.read.ListAppender;
import java.net.URL;
import org.junit.jupiter.api.Test;

public class RingBufferTest {
  @Test
  public void testWithDebug() throws JoranException {
    LoggerContext loggerContext = createLoggerContext();

    Logger logger = loggerContext.getLogger("com.example.Test");
    logger.debug("debug stuff");

    ListAppender<ILoggingEvent> listAppender = getListAppender(loggerContext);
    assertThat(listAppender.list.size()).isEqualTo(0);

    RingBuffer ringBuffer = getRingBuffer(loggerContext);
    assertThat(ringBuffer.size()).isEqualTo(1);
  }

  @Test
  public void testWithDebugAndInfo() throws JoranException {
    LoggerContext loggerContext = createLoggerContext();

    Logger logger = loggerContext.getLogger("com.example.Test");
    logger.debug("debug stuff");
    logger.info("info stuff");

    ListAppender<ILoggingEvent> listAppender = getListAppender(loggerContext);
    assertThat(listAppender.list.size()).isEqualTo(1);

    RingBuffer ringBuffer = getRingBuffer(loggerContext);
    assertThat(ringBuffer.size()).isEqualTo(1);
  }

  @Test
  public void testWithDebugAndError() throws JoranException {
    LoggerContext loggerContext = createLoggerContext();

    Logger logger = loggerContext.getLogger("com.example.Test");
    logger.debug("debug stuff");

    Logger dumpLogger = loggerContext.getLogger("DUMP_LOGGER");
    dumpLogger.error("Dump all the messages");

    ListAppender<ILoggingEvent> listAppender = getListAppender(loggerContext);
    assertThat(listAppender.list.size()).isEqualTo(1); // contains debug message

    RingBuffer ringBuffer = getRingBuffer(loggerContext);
    assertThat(ringBuffer.size()).isEqualTo(0);
  }

  @Test
  public void testWithDebugAndInfoAndError() throws JoranException {
    LoggerContext loggerContext = createLoggerContext();

    Logger logger = loggerContext.getLogger("com.example.Test");
    logger.debug("debug stuff");
    logger.info("info stuff");
    logger.error("error stuff");

    RingBuffer ringBuffer = getRingBuffer(loggerContext);
    ListAppender<ILoggingEvent> listAppender = getListAppender(loggerContext);
    assertThat(listAppender.list.size()).isEqualTo(2);
    assertThat(ringBuffer.size()).isEqualTo(1);
  }

  @SuppressWarnings("unchecked")
  private RingBuffer getRingBuffer(LoggerContext loggerContext) {
    return RingBufferUtils.getRingBuffer(loggerContext, "RINGBUFFER");
  }

  @Test
  public void testWithDump() throws JoranException {
    LoggerContext loggerFactory = createLoggerContext();

    Logger logger = loggerFactory.getLogger("com.example.Test");
    logger.debug("debug one");
    logger.debug("debug two");
    logger.debug("debug three");
    logger.debug("debug four");

    // The dump logger does not render its own messages, but triggers a dump of ringbuffer
    Logger dumpLogger = loggerFactory.getLogger("DUMP_LOGGER");
    dumpLogger.error("Dump all the messages");

    ListAppender<ILoggingEvent> listAppender = getListAppender(loggerFactory);
    assertThat(listAppender.list.size()).isEqualTo(4);
  }

  //  @Test
  //  public void testWithContextDrivenDump() throws JoranException {
  //    LoggerContext loggerFactory = createContextLoggerContext();
  //
  //    // Context has this set to ["example1.Test", "example2.Test", "example3"]
  //    Logger logger1 = loggerFactory.getLogger("example1.Test");
  //    Logger logger2 = loggerFactory.getLogger("example2.Test");
  //    Logger logger3 = loggerFactory.getLogger("example3.Test");
  //    Logger logger4 = loggerFactory.getLogger("example4.Test");
  //    logger1.debug("debug one");
  //    logger2.debug("debug two");
  //    logger3.debug("debug three");
  //    logger4.debug("debug four"); // should not be put in ring buffer
  //    logger1.error("Dump all the messages");
  //
  //    ListAppender<ILoggingEvent> listAppender = getListAppender(loggerFactory);
  //    assertThat(listAppender.list.size()).isEqualTo(4);
  //  }

  //  LoggerContext createContextLoggerContext() throws JoranException {
  //    LoggerContext context = new LoggerContext();
  //    URL resource = getClass().getResource("/logback-with-config-threshold-ringbuffer.xml");
  //    JoranConfigurator configurator = new JoranConfigurator();
  //    configurator.setContext(context);
  //    configurator.doConfigure(resource);
  //    return context;
  //  }

  LoggerContext createLoggerContext() throws JoranException {
    LoggerContext context = new LoggerContext();
    URL resource = getClass().getResource("/logback-ringbuffer.xml");
    JoranConfigurator configurator = new JoranConfigurator();
    configurator.setContext(context);
    configurator.doConfigure(resource);
    return context;
  }

  ListAppender<ILoggingEvent> getListAppender(LoggerContext context) {
    Logger listLogger = context.getLogger("LIST_LOGGER");
    return (ListAppender<ILoggingEvent>) requireNonNull(listLogger.getAppender("LIST"));
  }
}
