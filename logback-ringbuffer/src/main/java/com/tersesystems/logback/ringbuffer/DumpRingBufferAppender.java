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
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import com.tersesystems.logback.core.DefaultAppenderAttachable;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Function;
import java.util.function.Supplier;

/** This class dumps the ring buffer when called. */
public class DumpRingBufferAppender extends UnsynchronizedAppenderBase<ILoggingEvent>
    implements RingBufferAttachable, DefaultAppenderAttachable<ILoggingEvent> {

  private final AppenderAttachableImpl<ILoggingEvent> aai = new AppenderAttachableImpl<>();

  private RingBufferContextAware ringBuffer;

  // Provide a transform function that you can override.
  protected Function<ILoggingEvent, ILoggingEvent> transformFunction = Function.identity();

  public RingBufferContextAware getRingBuffer() {
    return ringBuffer;
  }

  public void setRingBuffer(RingBufferContextAware ringBuffer) {
    this.ringBuffer = ringBuffer;
  }

  public Function<ILoggingEvent, ILoggingEvent> getTransformFunction() {
    return transformFunction;
  }

  public void setTransformFunction(Function<ILoggingEvent, ILoggingEvent> transformFunction) {
    this.transformFunction = transformFunction;
  }

  protected Supplier<RingBuffer.ExitCondition> exitConditionSupplier;

  protected Supplier<RingBuffer.WaitStrategy> waitStrategySupplier;

  @Override
  public void start() {
    if (this.ringBuffer == null) {
      addError("Null ring buffer!");
      return;
    }

    if (this.exitConditionSupplier == null) {
      exitConditionSupplier = () -> () -> !ringBuffer.isEmpty();
    }

    if (this.waitStrategySupplier == null) {
      waitStrategySupplier =
          () ->
              idleCounter -> {
                if (idleCounter > 200) {
                  LockSupport.parkNanos(1L);
                } else if (idleCounter > 100) {
                  Thread.yield();
                }
                return idleCounter + 1;
              };
    }
    super.start();
  }

  @Override
  protected void append(ILoggingEvent event) {
    // Ignore the incoming event, and dump the ring buffer contents out to the
    // given appenders.
    RingBufferContextAware ringBuffer = getRingBuffer();
    Function<ILoggingEvent, ILoggingEvent> f = getTransformFunction();
    ringBuffer.drain(
        e -> this.aai.appendLoopOnAppenders(f.apply(e)),
        waitStrategySupplier.get(),
        exitConditionSupplier.get());
  }

  @Override
  public AppenderAttachableImpl<ILoggingEvent> appenderAttachableImpl() {
    return this.aai;
  }
}
