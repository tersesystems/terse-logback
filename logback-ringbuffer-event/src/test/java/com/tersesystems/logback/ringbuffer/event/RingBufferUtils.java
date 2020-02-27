package com.tersesystems.logback.ringbuffer.event;

import ch.qos.logback.classic.LoggerContext;
import com.tersesystems.logback.ringbuffer.RingBuffer;

import java.util.Map;

import static com.tersesystems.logback.ringbuffer.RingBufferConstants.RINGBUFFER_BAG;

public final class RingBufferUtils {

  @SuppressWarnings("unchecked")
  static RingBuffer getRingBuffer(LoggerContext loggerFactory, String name) {
    Map<String, RingBuffer> ringBufferBag =
        (Map<String, RingBuffer>) loggerFactory.getObject(RINGBUFFER_BAG);
    return ringBufferBag.get(name);
  }
}
