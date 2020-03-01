package com.tersesystems.logback.ringbuffer;

import static com.tersesystems.logback.ringbuffer.RingBufferConstants.RINGBUFFER_BAG;

import ch.qos.logback.classic.LoggerContext;
import java.util.Map;

public final class RingBufferUtils {

  @SuppressWarnings("unchecked")
  static RingBuffer getRingBuffer(LoggerContext loggerFactory, String name) {
    Map<String, RingBuffer> ringBufferBag =
        (Map<String, RingBuffer>) loggerFactory.getObject(RINGBUFFER_BAG);
    return ringBufferBag.get(name);
  }
}
