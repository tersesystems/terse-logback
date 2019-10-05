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
package com.tersesystems.logback.classic.ringbuffer;

import ch.qos.logback.classic.spi.ILoggingEvent;

/** A cyclic buffer appender that adds the event itself to the ring buffer. */
public class IdentityRingBufferAppender
    extends AbstractRingBufferAppender<ILoggingEvent, ILoggingEvent> {
  @Override
  protected void append(ILoggingEvent eventObject) {
    if (!isStarted()) {
      return;
    }
    getRingBuffer().add(eventObject);
  }
}
