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

import ch.qos.logback.classic.layout.TTLLLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;

/**
 * An appender that uses an encoder to serialize an event, and then appends it to a ringbuffer.
 *
 * <p>This is a good option if you want the encoded data (just plain bytes) and don't want to hang
 * on to possible references in the JVM (which may cause memory leaks depending on what is
 * referenced and the speed of the queue). The downside is that you do lose the ability to do
 * additional inspection of events.
 */
public class EncodingRingBufferAppender extends AbstractRingBufferAppender<ILoggingEvent> {

  protected Encoder<ILoggingEvent> encoder;
  private BufferedLoggingEventFactory eventFactory = new BufferedLoggingEventFactory();

  public Encoder<ILoggingEvent> getEncoder() {
    return encoder;
  }

  public void setEncoder(Encoder<ILoggingEvent> encoder) {
    this.encoder = encoder;
  }

  public void start() {
    super.start();
    if (this.encoder == null) {
      // Following from BasicConfigurator
      LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
      encoder.setContext(getContext());
      TTLLLayout layout = new TTLLLayout();
      layout.setContext(getContext());
      layout.start();
      encoder.setLayout(layout);
      encoder.start();
    }
  }

  public void stop() {
    this.encoder = null;
    super.stop();
  }

  @Override
  protected void append(ILoggingEvent e) {
    if (!isStarted()) {
      return;
    }

    if (e instanceof LoggingEvent) {
      byte[] encodedData = this.encoder.encode(e);
      BufferedLoggingEvent bufferedEvent = eventFactory.create((LoggingEvent) e, encodedData);
      appendToBuffer(bufferedEvent);
    } else {
      addWarn("Event is not an instance of LoggingEvent!");
    }
  }
}
