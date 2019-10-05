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
import ch.qos.logback.classic.sift.SiftingAppender;
import com.tersesystems.logback.classic.Utils;
import com.tersesystems.logback.classic.ringbuffer.HasRingBuffer;
import com.tersesystems.logback.classic.ringbuffer.RingBufferAppender;
import com.tersesystems.logback.core.RingBuffer;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Gets a ring buffer from the logger context, given the appender name and key.
 *
 * @param <E>
 */
public class SiftingRingBufferFunction<E>
    implements BiFunction<String, String, Optional<RingBuffer<E>>> {

  private final LoggerContext context;

  public SiftingRingBufferFunction(LoggerContext context) {
    this.context = context;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Optional<RingBuffer<E>> apply(String appenderName, String key) {
    Class<RingBufferAppender> ringBufferClass = RingBufferAppender.class;
    return GetAppenderFunction.<SiftingAppender>create(context)
        .apply(appenderName)
        .flatMap(sa -> Optional.ofNullable(sa.getAppenderTracker().find(key)))
        .filter(a -> ringBufferClass.isAssignableFrom(a.getClass()))
        .map(ringBufferClass::cast)
        .map(HasRingBuffer::getRingBuffer);
  }

  public static <I> SiftingRingBufferFunction<I> create() {
    return create(Utils.defaultContext());
  }

  public static <I> SiftingRingBufferFunction<I> create(LoggerContext context) {
    return new SiftingRingBufferFunction<>(context);
  }
}
