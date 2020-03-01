package com.tersesystems.logback.ringbuffer;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggingEvent;
import com.tersesystems.logback.classic.Utils;
import java.util.Map;

public class BufferedLoggingEventFactory {

  private final Utils utils;

  public BufferedLoggingEventFactory() {
    this(Utils.create());
  }

  public BufferedLoggingEventFactory(Utils utils) {
    this.utils = utils;
  }

  public BufferedLoggingEvent create(LoggingEvent event, byte[] encodedData) {
    event.prepareForDeferredProcessing();
    IThrowableProxy throwableProxy = event.getThrowableProxy();
    String loggerName = event.getLoggerName();
    String threadName = Thread.currentThread().getName();
    Map<String, String> mdcPropertyMap = event.getMDCPropertyMap();
    long timestamp = event.getTimeStamp();
    Level level = event.getLevel();
    return new BufferedLoggingEvent(
        loggerName, level, encodedData, timestamp, threadName, mdcPropertyMap, throwableProxy);
  }
}
