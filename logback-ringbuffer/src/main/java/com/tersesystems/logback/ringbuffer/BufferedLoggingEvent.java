package com.tersesystems.logback.ringbuffer;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.slf4j.Marker;

public class BufferedLoggingEvent implements ILoggingEvent {

  private final String loggerName;
  private final IThrowableProxy throwableProxy;
  private final Level level;
  private final String threadName;
  private final Map<String, String> mdcPropertyMap;
  private final long timestamp;
  private final byte[] encodedData;

  public BufferedLoggingEvent(
      String loggerName,
      Level level,
      byte[] encodedData,
      long timestamp,
      String threadName,
      Map<String, String> mdcPropertyMap,
      IThrowableProxy throwableProxy) {
    this.loggerName = loggerName;
    this.level = level;
    this.encodedData = encodedData;
    this.timestamp = timestamp;
    this.threadName = threadName;
    this.mdcPropertyMap = mdcPropertyMap;
    this.throwableProxy = throwableProxy;
  }

  // Returns the results of running the event through an encoder.
  public byte[] getEncodedData() {
    return encodedData;
  }

  public String getThreadName() {
    return this.threadName;
  }

  @Override
  public Level getLevel() {
    return this.level;
  }

  @Override
  public String getMessage() {
    return null;
  }

  @Override
  public Object[] getArgumentArray() {
    return new Object[0];
  }

  @Override
  public String getFormattedMessage() {
    return new String(getEncodedData(), StandardCharsets.UTF_8);
  }

  @Override
  public String getLoggerName() {
    return this.loggerName;
  }

  @Override
  public LoggerContextVO getLoggerContextVO() {
    return null;
  }

  @Override
  public IThrowableProxy getThrowableProxy() {
    return throwableProxy;
  }

  @Override
  public StackTraceElement[] getCallerData() {
    return new StackTraceElement[0];
  }

  @Override
  public boolean hasCallerData() {
    return false;
  }

  @Override
  public Marker getMarker() {
    return null;
  }

  @Override
  public Map<String, String> getMDCPropertyMap() {
    return this.mdcPropertyMap;
  }

  @Override
  public Map<String, String> getMdc() {
    return mdcPropertyMap;
  }

  @Override
  public long getTimeStamp() {
    return this.timestamp;
  }

  @Override
  public void prepareForDeferredProcessing() {}
}
