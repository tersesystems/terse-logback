package com.tersesystems.logback.ringbuffer;

import ch.qos.logback.core.spi.ContextAwareBase;

public abstract class RingBufferBase extends ContextAwareBase implements RingBuffer {
  private int capacity = 100;

  private String name;

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  public int getCapacity() {
    return capacity;
  }

  public void setCapacity(int capacity) {
    this.capacity = capacity;
  }
}
