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
import org.jctools.queues.MessagePassingQueue;
import org.jctools.queues.MpmcArrayQueue;

/** An implementation of ring buffer using a multi-producer/multi-consumer array queue. */
public class MessagePassingQueueRingBuffer extends RingBufferBase {

  // https://psy-lob-saw.blogspot.com/2015/01/mpmc-multi-multi-queue-vs-clq.html
  private MessagePassingQueue<ILoggingEvent> queue;
  private volatile boolean started;

  @Override
  public void start() {
    queue = new MpmcArrayQueue<ILoggingEvent>(getCapacity());
    this.started = true;
  }

  @Override
  public void stop() {
    queue = null;
    started = false;
  }

  @Override
  public boolean isStarted() {
    return started;
  }

  @Override
  public int capacity() {
    return queue.capacity();
  }

  @Override
  public boolean relaxedOffer(ILoggingEvent e) {
    return queue.relaxedOffer(e);
  }

  @Override
  public ILoggingEvent relaxedPoll() {
    return queue.relaxedPoll();
  }

  @Override
  public ILoggingEvent relaxedPeek() {
    return queue.relaxedPeek();
  }

  @Override
  public int drain(Consumer<ILoggingEvent> c, int limit) {
    return queue.drain(c::accept, limit);
  }

  @Override
  public int fill(Supplier<ILoggingEvent> s, int limit) {
    return queue.fill(s::get, limit);
  }

  @Override
  public int drain(Consumer<ILoggingEvent> c) {
    return queue.drain(c::accept);
  }

  @Override
  public int fill(Supplier<ILoggingEvent> s) {
    return queue.fill(s::get);
  }

  @Override
  public void drain(Consumer<ILoggingEvent> c, WaitStrategy wait, ExitCondition exit) {
    queue.drain(c::accept, wait::idle, exit::keepRunning);
  }

  @Override
  public void fill(Supplier<ILoggingEvent> s, WaitStrategy wait, ExitCondition exit) {
    queue.fill(s::get, wait::idle, exit::keepRunning);
  }

  @Override
  public int size() {
    return queue.size();
  }

  @Override
  public boolean isEmpty() {
    return queue.isEmpty();
  }

  @Override
  public void clear() {
    queue.clear();
  }

  @Override
  public boolean offer(ILoggingEvent e) {
    return queue.offer(e);
  }

  @Override
  public ILoggingEvent poll() {
    return queue.poll();
  }

  @Override
  public ILoggingEvent peek() {
    return queue.peek();
  }
}
