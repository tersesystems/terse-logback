package com.tersesystems.logback.ringbuffer;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import ch.qos.logback.core.spi.FilterReply;
import com.tersesystems.logback.core.DefaultAppenderAttachable;
import java.util.function.Function;

/** A ringbuffer aware appender that will append to ringbuffer on a FilterReply DENY. */
public class RingBufferAwareAppender extends AbstractRingBufferAppender<ILoggingEvent>
    implements DefaultAppenderAttachable<ILoggingEvent> {

  protected volatile boolean started = false;

  protected final BufferedLoggingEventFactory eventFactory = new BufferedLoggingEventFactory();

  private Encoder<ILoggingEvent> encoder;

  private final AppenderAttachableImpl<ILoggingEvent> aai = new AppenderAttachableImpl<>();

  // Provide a transform function we can override in subclasses
  protected Function<ILoggingEvent, ILoggingEvent> transformFunction =
      e -> {
        byte[] encodedData = getEncoder().encode(e);
        return eventFactory.create(e, encodedData);
      };

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

  public Function<ILoggingEvent, ILoggingEvent> getTransformFunction() {
    return transformFunction;
  }

  public void setTransformFunction(Function<ILoggingEvent, ILoggingEvent> transformFunction) {
    this.transformFunction = transformFunction;
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
    Function<ILoggingEvent, ILoggingEvent> tf = getTransformFunction();
    ILoggingEvent bufferEvent = tf.apply(e);
    getRingBuffer().relaxedOffer(bufferEvent);
  }
}
