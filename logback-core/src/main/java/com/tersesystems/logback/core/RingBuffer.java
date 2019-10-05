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
package com.tersesystems.logback.core;

import java.util.*;

/**
 * If you use this code, please consider notifying isak at du-preez dot com with a brief description
 * of your application.
 *
 * <p>This is free and unencumbered software released into the public domain. Anyone is free to
 * copy, modify, publish, use, compile, sell, or distribute this software, either in source code
 * form or as a compiled binary, for any purpose, commercial or non-commercial, and by any means.
 *
 * <p>http://www.museful.net/2012/software-development/circulararraylist-for-java
 */
public class RingBuffer<E> extends AbstractList<E> implements RandomAccess {

  private final int n; // buffer length
  private final List<E> buf; // a List implementing RandomAccess
  private int head = 0;
  private int tail = 0;

  public RingBuffer(int capacity) {
    n = capacity + 1;
    buf = new ArrayList<E>(Collections.nCopies(n, (E) null));
  }

  public int capacity() {
    return n - 1;
  }

  private int wrapIndex(int i) {
    int m = i % n;
    if (m < 0) { // java modulus can be negative
      m += n;
    }
    return m;
  }

  // This method is O(n) but will never be called if the
  // CircularArrayList is used in its typical/intended role.
  private void shiftBlock(int startIndex, int endIndex) {
    assert (endIndex > startIndex);
    for (int i = endIndex - 1; i >= startIndex; i--) {
      set(i + 1, get(i));
    }
  }

  @Override
  public int size() {
    return tail - head + (tail < head ? n : 0);
  }

  @Override
  public E get(int i) {
    if (i < 0 || i >= size()) {
      throw new IndexOutOfBoundsException();
    }
    return buf.get(wrapIndex(head + i));
  }

  @Override
  public E set(int i, E e) {
    if (i < 0 || i >= size()) {
      throw new IndexOutOfBoundsException();
    }
    return buf.set(wrapIndex(head + i), e);
  }

  @Override
  public void add(int i, E e) {
    int s = size();
    if (s == n - 1) {
      throw new IllegalStateException(
          "CircularArrayList is filled to capacity. "
              + "(You may want to remove from front"
              + " before adding more to back.)");
    }
    if (i < 0 || i > s) {
      throw new IndexOutOfBoundsException();
    }
    tail = wrapIndex(tail + 1);
    if (i < s) {
      shiftBlock(i, s);
    }
    set(i, e);
  }

  @Override
  public E remove(int i) {
    int s = size();
    if (i < 0 || i >= s) {
      throw new IndexOutOfBoundsException();
    }
    E e = get(i);
    if (i > 0) {
      shiftBlock(0, i);
    }
    head = wrapIndex(head + 1);
    return e;
  }
}
