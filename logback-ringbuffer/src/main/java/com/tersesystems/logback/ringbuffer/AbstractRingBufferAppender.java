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

import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterAttachableImpl;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.status.WarnStatus;
import java.util.List;

/**
 * An abstract appender that keeps an in-memory ring buffer of a given type.
 *
 * <p>Note that the filter chain decision is NOT applied here. Subclasses determine where to send
 * the event in the append method.
 *
 * <p>We must extend UnsynchronizedAppenderBase here, as that's where the default component registry
 * expects complex properties (i.e. encoder) to be set, and it will get very confused if you just
 * implement Appender.
 */
public abstract class AbstractRingBufferAppender<E> extends UnsynchronizedAppenderBase<E>
    implements RingBufferAppender<E> {

  private RingBuffer ringBuffer;

  protected boolean started = false;

  // using a ThreadLocal instead of a boolean add 75 nanoseconds per
  // doAppend invocation. This is tolerable as doAppend takes at least a few microseconds
  // on a real appender
  /** The guard prevents an appender from repeatedly calling its own doAppend method. */
  private final ThreadLocal<Boolean> guard = new ThreadLocal<Boolean>();

  /** Appenders are named. */
  protected String name;

  private final FilterAttachableImpl<E> fai = new FilterAttachableImpl<E>();

  public String getName() {
    return name;
  }

  private int statusRepeatCount = 0;
  private int exceptionCount = 0;

  static final int ALLOWED_REPEATS = 3;

  public void doAppend(E eventObject) {
    // WARNING: The guard check MUST be the first statement in the
    // doAppend() method.

    // prevent re-entry.
    if (Boolean.TRUE.equals(guard.get())) {
      return;
    }

    try {
      guard.set(Boolean.TRUE);

      if (!this.started) {
        if (statusRepeatCount++ < ALLOWED_REPEATS) {
          addStatus(
              new WarnStatus("Attempted to append to non started appender [" + name + "].", this));
        }
        return;
      }

      // ok, we now invoke derived class' implementation of append
      this.append(eventObject);

    } catch (Exception e) {
      if (exceptionCount++ < ALLOWED_REPEATS) {
        addError("Appender [" + name + "] failed to append.", e);
      }
    } finally {
      guard.set(Boolean.FALSE);
    }
  }

  protected abstract void append(E eventObject);

  /** Set the name of this appender. */
  public void setName(String name) {
    this.name = name;
  }

  public void start() {
    if (getRingBuffer() == null) {
      addError("No ring buffer set!");
      return;
    }
    started = true;
  }

  public void stop() {
    setRingBuffer(null);
    started = false;
  }

  public boolean isStarted() {
    return started;
  }

  public String toString() {
    return this.getClass().getName() + "[" + name + "]";
  }

  public void addFilter(Filter<E> newFilter) {
    fai.addFilter(newFilter);
  }

  public void clearAllFilters() {
    fai.clearAllFilters();
  }

  public List<Filter<E>> getCopyOfAttachedFiltersList() {
    return fai.getCopyOfAttachedFiltersList();
  }

  public FilterReply getFilterChainDecision(E event) {
    return fai.getFilterChainDecision(event);
  }

  @Override
  public RingBuffer getRingBuffer() {
    return ringBuffer;
  }

  @Override
  public void setRingBuffer(RingBuffer ringBuffer) {
    this.ringBuffer = ringBuffer;
  }
}
