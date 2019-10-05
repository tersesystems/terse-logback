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

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * A <a href="https://en.wikipedia.org/wiki/Circular_buffer">circular buffer</a> that contains a
 * bounded queue of elements.
 *
 * <p>This implementation adds an append method which will remove elements from the head of the
 * queue until the appended event is successfully offered.
 *
 * @param <ElementT> the element type
 */
public class RingBuffer<ElementT> implements Queue<ElementT> {
  private final ArrayBlockingQueue<ElementT> queue;

  public RingBuffer(int capacity) {
    this.queue = new ArrayBlockingQueue<>(capacity);
  }

  /**
   * Appends event to the end of the queue using offer. If the queue is full, then the head element
   * is removed.
   *
   * @param event the event to add.
   */
  public void append(ElementT event) {
    while (!queue.offer(event)) {
      queue.poll();
    }
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
  public boolean contains(Object o) {
    return queue.contains(o);
  }

  @Override
  public Iterator<ElementT> iterator() {
    return queue.iterator();
  }

  @Override
  public Object[] toArray() {
    return queue.toArray();
  }

  @Override
  public <T> T[] toArray(T[] ts) {
    return queue.toArray(ts);
  }

  @Override
  public boolean add(ElementT e) {
    return queue.add(e);
  }

  @Override
  public boolean remove(Object o) {
    return queue.remove(o);
  }

  @Override
  public boolean containsAll(Collection<?> collection) {
    return queue.containsAll(collection);
  }

  @Override
  public boolean addAll(Collection<? extends ElementT> collection) {
    return queue.addAll(collection);
  }

  @Override
  public boolean removeAll(Collection<?> collection) {
    return queue.removeAll(collection);
  }

  @Override
  public boolean retainAll(Collection<?> collection) {
    return queue.retainAll(collection);
  }

  @Override
  public void clear() {
    queue.clear();
  }

  @Override
  public boolean offer(ElementT e) {
    return queue.offer(e);
  }

  @Override
  public ElementT remove() {
    return queue.remove();
  }

  @Override
  public ElementT poll() {
    return queue.poll();
  }

  @Override
  public ElementT element() {
    return queue.element();
  }

  @Override
  public ElementT peek() {
    return queue.peek();
  }
}
