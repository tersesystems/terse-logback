package com.tersesystems.logback.ringbuffer;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.jctools.queues.MessagePassingQueue;
import org.jctools.queues.MpmcArrayQueue;

public class MessagePassingQueueRingBuffer extends RingBufferBase {

  // https://psy-lob-saw.blogspot.com/2015/01/mpmc-multi-multi-queue-vs-clq.html
  private MessagePassingQueue<ILoggingEvent> queue;
  private volatile boolean started;

  @Override
  public void start() {
    queue = new MpmcArrayQueue<>(getCapacity());
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
    return queue.drain(c, limit);
  }

  @Override
  public int fill(Supplier<ILoggingEvent> s, int limit) {
    return queue.fill(s, limit);
  }

  @Override
  public int drain(Consumer<ILoggingEvent> c) {
    return queue.drain(c);
  }

  @Override
  public int fill(Supplier<ILoggingEvent> s) {
    return queue.fill(s);
  }

  @Override
  public void drain(Consumer<ILoggingEvent> c, WaitStrategy wait, ExitCondition exit) {
    queue.drain(c, wait, exit);
  }

  @Override
  public void fill(Supplier<ILoggingEvent> s, WaitStrategy wait, ExitCondition exit) {
    queue.fill(s, wait, exit);
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
