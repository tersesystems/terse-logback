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

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * An abstract appender that keeps an in-memory ring buffer of a given type.
 *
 * @param <EventT> the type of logging event
 */
public abstract class AbstractRingBufferAppender<EventT extends ILoggingEvent>
    extends AppenderBase<EventT> implements RingBufferAppender<EventT> {

  protected RingBuffer ringBuffer;

  public RingBuffer getRingBuffer() {
    return ringBuffer;
  }

  public void setRingBuffer(RingBuffer ringBuffer) {
    this.ringBuffer = ringBuffer;
  }

  public void start() {
    if (ringBuffer == null) {
      addError("No ring buffer set!");
      return;
    }
    super.start();
  }

  public void stop() {
    ringBuffer = null;
    super.stop();
  }

  public void reset() {
    ringBuffer.clear();
  }

  /**
   * Appends to the ring buffer, removing an element from the front if necessary.
   *
   * @param element the element to add.
   */
  protected boolean appendToBuffer(BufferedLoggingEvent element) {
    RingBuffer ringBuffer = getRingBuffer();
    return ringBuffer.add(element);
  }
}
