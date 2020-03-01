package com.tersesystems.logback.ringbuffer;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import ch.qos.logback.core.spi.FilterAttachableImpl;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.status.WarnStatus;
import com.tersesystems.logback.core.DefaultAppenderAttachable;
import java.util.List;

/**
 * A ringbuffer aware appender that will append to ringbuffer on a FilterReply DENY.
 *
 * <p>We must extend AppenderBase here, as that's where the default component registry expects
 * complex properties (i.e. encoder) to be set, and it will get very confused if you just implement
 * Appender. So we extend AppenderBase and copy the implementation from scratch.
 */
public class RingBufferAwareAppender extends AppenderBase<ILoggingEvent>
    implements RingBufferAttachable, RingBufferAware, DefaultAppenderAttachable<ILoggingEvent> {

  protected volatile boolean started = false;

  /** The guard prevents an appender from repeatedly calling its own doAppend method. */
  private boolean guard = false;

  /** Appenders are named. */
  protected String name;

  private final FilterAttachableImpl<ILoggingEvent> fai = new FilterAttachableImpl<ILoggingEvent>();
  private final BufferedLoggingEventFactory eventFactory = new BufferedLoggingEventFactory();

  public String getName() {
    return name;
  }

  private int statusRepeatCount = 0;
  private int exceptionCount = 0;

  static final int ALLOWED_REPEATS = 5;

  private RingBuffer ringBuffer;
  private Encoder<ILoggingEvent> encoder;

  private final AppenderAttachableImpl<ILoggingEvent> aai = new AppenderAttachableImpl<>();

  @Override
  public RingBuffer getRingBuffer() {
    return this.ringBuffer;
  }

  @Override
  public void setRingBuffer(RingBuffer ringBuffer) {
    this.ringBuffer = ringBuffer;
  }

  public Encoder<ILoggingEvent> getEncoder() {
    return encoder;
  }

  public void setEncoder(Encoder<ILoggingEvent> encoder) {
    this.encoder = encoder;
  }

  @Override
  public AppenderAttachableImpl<ILoggingEvent> appenderAttachableImpl() {
    return aai;
  }

  @Override
  public void start() {
    if (this.encoder == null) {
      addError("Null encoder!");
      return;
    }
    started = true;
  }

  public void stop() {
    started = false;
  }

  protected void append(ILoggingEvent event) {
    FilterReply filterChainDecision = getFilterChainDecision(event);
    if (filterChainDecision == FilterReply.DENY) {
      appendToRingBuffer(event);
    } else {
      aai.appendLoopOnAppenders(event);
    }
  }

  protected void appendToRingBuffer(ILoggingEvent e) {
    byte[] encodedData = this.encoder.encode(e);
    BufferedLoggingEvent bufferedLoggingEvent = eventFactory.create(e, encodedData);
    this.ringBuffer.add(bufferedLoggingEvent);
  }

  public synchronized void doAppend(ILoggingEvent eventObject) {
    // WARNING: The guard check MUST be the first statement in the
    // doAppend() method.

    // prevent re-entry.
    if (guard) {
      return;
    }

    try {
      guard = true;

      if (!this.started) {
        if (statusRepeatCount++ < ALLOWED_REPEATS) {
          addStatus(
              new WarnStatus("Attempted to append to non started appender [" + name + "].", this));
        }
        return;
      }

      // ok, we now invoke derived class' implementation of append
      this.append(eventObject);

    } catch (Exception e) {
      if (exceptionCount++ < ALLOWED_REPEATS) {
        addError("Appender [" + name + "] failed to append.", e);
      }
    } finally {
      guard = false;
    }
  }

  /** Set the name of this appender. */
  public void setName(String name) {
    this.name = name;
  }

  public boolean isStarted() {
    return started;
  }

  public String toString() {
    return this.getClass().getName() + "[" + name + "]";
  }

  public void addFilter(Filter<ILoggingEvent> newFilter) {
    fai.addFilter(newFilter);
  }

  public void clearAllFilters() {
    fai.clearAllFilters();
  }

  public List<Filter<ILoggingEvent>> getCopyOfAttachedFiltersList() {
    return fai.getCopyOfAttachedFiltersList();
  }

  public FilterReply getFilterChainDecision(ILoggingEvent event) {
    return fai.getFilterChainDecision(event);
  }
}
