package com.tersesystems.logback.ringbuffer;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import java.util.Map;

/**
 * This event factory creates a logging event that has already been encoded.
 *
 * <p>Note that the marker and arguments of the events are purposefully left null, because they
 * could contain references to outside objects that were not garbage collected.
 */
public class BufferedLoggingEventFactory {

  public BufferedLoggingEvent create(ILoggingEvent event, byte[] encodedData) {
    event.prepareForDeferredProcessing();
    IThrowableProxy throwableProxy = event.getThrowableProxy();
    String loggerName = event.getLoggerName();
    String threadName = event.getThreadName();
    Map<String, String> mdcPropertyMap = event.getMDCPropertyMap();
    long timestamp = event.getTimeStamp();
    Level level = event.getLevel();
    return new BufferedLoggingEvent(
        loggerName, level, encodedData, timestamp, threadName, mdcPropertyMap, throwableProxy);
  }
}
