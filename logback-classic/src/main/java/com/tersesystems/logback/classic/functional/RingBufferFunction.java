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
package com.tersesystems.logback.classic.functional;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.tersesystems.logback.classic.Utils;
import com.tersesystems.logback.classic.ringbuffer.HasRingBuffer;
import com.tersesystems.logback.classic.ringbuffer.RingBufferAppender;
import com.tersesystems.logback.core.RingBuffer;
import java.util.Optional;
import java.util.function.Function;

/**
 * Gets the ring buffer from the logger context, given the appender name.
 *
 * @param <E>
 */
public class RingBufferFunction<E> implements Function<String, Optional<RingBuffer<E>>> {

  private final LoggerContext context;

  public RingBufferFunction(LoggerContext context) {
    this.context = context;
  }

  @Override
  public Optional<RingBuffer<E>> apply(String appenderName) {
    GetAppenderFunction<RingBufferAppender<ILoggingEvent, E>> appenderFn =
        GetAppenderFunction.create(context);
    return appenderFn.apply(appenderName).map(HasRingBuffer::getRingBuffer);
  }

  public static <I> RingBufferFunction<I> create() {
    return create(Utils.defaultContext());
  }

  public static <I> RingBufferFunction<I> create(LoggerContext context) {
    return new RingBufferFunction<>(context);
  }
}
