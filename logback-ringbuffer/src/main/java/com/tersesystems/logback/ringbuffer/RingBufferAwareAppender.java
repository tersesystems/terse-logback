/*
 * SPDX-License-Identifier: CC0-1.0
 *
 * Copyright 2018-2020 Will Sargent.
 *
 * Licensed under the CC0 Public Domain Dedication;
 * You may obtain a copy of the License at
 *
 *  http://creativecommons.org/publicdomain/zero/1.0/
 */

package com.tersesystems.logback.ringbuffer;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import ch.qos.logback.core.spi.FilterReply;
import com.tersesystems.logback.core.DefaultAppenderAttachable;
import java.util.function.Function;

/** A ringbuffer aware appender that will append to ringbuffer on a FilterReply DENY. */
public class RingBufferAwareAppender extends AbstractRingBufferAppender<ILoggingEvent>
    implements DefaultAppenderAttachable<ILoggingEvent> {

  private final AppenderAttachableImpl<ILoggingEvent> aai = new AppenderAttachableImpl<>();

  // Provide a transform function we can override in subclasses
  // This gives us the option of doing encoding or isolating event logic without requiring
  // anything at base level
  protected Function<ILoggingEvent, ILoggingEvent> transformFunction =
      e -> {
        e.prepareForDeferredProcessing();
        return e;
      };

  @Override
  public AppenderAttachableImpl<ILoggingEvent> appenderAttachableImpl() {
    return aai;
  }

  public Function<ILoggingEvent, ILoggingEvent> getTransformFunction() {
    return transformFunction;
  }

  public void setTransformFunction(Function<ILoggingEvent, ILoggingEvent> transformFunction) {
    this.transformFunction = transformFunction;
  }

  @Override
  public void start() {
    super.start();
  }

  protected void append(ILoggingEvent event) {
    FilterReply filterChainDecision = getFilterChainDecision(event);
    if (filterChainDecision == FilterReply.DENY) {
      appendToRingBuffer(event);
    } else {
      aai.appendLoopOnAppenders(event);
    }
  }

  protected void appendToRingBuffer(ILoggingEvent e) {
    Function<ILoggingEvent, ILoggingEvent> tf = getTransformFunction();
    ILoggingEvent bufferEvent = tf.apply(e);
    getRingBuffer().relaxedOffer(bufferEvent);
  }
}
