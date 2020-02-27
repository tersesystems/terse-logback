package com.tersesystems.logback.ringbuffer;

import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.LifeCycle;
import java.util.Queue;

public interface RingBuffer extends ContextAware, LifeCycle, Queue<BufferedLoggingEvent> {

  /** Get the name of this appender. The name uniquely identifies the appender. */
  String getName();

  /**
   * Set the name of this appender. The name is used by other components to identify this appender.
   */
  void setName(String name);
}
