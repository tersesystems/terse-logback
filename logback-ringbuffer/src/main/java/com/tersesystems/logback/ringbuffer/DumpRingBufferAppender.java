package com.tersesystems.logback.ringbuffer;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import com.tersesystems.logback.core.DefaultAppenderAttachable;

import java.util.function.Function;

public abstract class DumpRingBufferAppender extends UnsynchronizedAppenderBase<ILoggingEvent>
    implements RingBufferAttachable, DefaultAppenderAttachable<ILoggingEvent> {

  private final AppenderAttachableImpl<ILoggingEvent> aai = new AppenderAttachableImpl<>();
  private RingBuffer ringBuffer;

  // If poll is set, remove each element from the ring buffer as it's sent.
  private boolean poll = true;

  // Provide a transform function that you can override.
  protected Function<ILoggingEvent, ILoggingEvent> transformFunction = Function.identity();

  public boolean isPoll() {
    return poll;
  }

  public void setPoll(boolean poll) {
    this.poll = poll;
  }

  public RingBuffer getRingBuffer() {
    return ringBuffer;
  }

  public void setRingBuffer(RingBuffer ringBuffer) {
    this.ringBuffer = ringBuffer;
  }

  public Function<ILoggingEvent, ILoggingEvent> getTransformFunction() {
    return transformFunction;
  }

  public void setTransformFunction(Function<ILoggingEvent, ILoggingEvent> transformFunction) {
    this.transformFunction = transformFunction;
  }

  @Override
  public void start() {
    if (this.ringBuffer == null) {
      addError("Null ring buffer!");
      return;
    }
    super.start();
  }

  @Override
  protected void append(ILoggingEvent event) {
    // Ignore the incoming event, and dump the ring buffer contents out to the
    // given appenders.
    RingBuffer ringBuffer = getRingBuffer();
    Function<ILoggingEvent, ILoggingEvent> f = getTransformFunction();
    if (this.poll) {
      ILoggingEvent e;
      while ((e = f.apply(ringBuffer.poll())) != null) {
        this.aai.appendLoopOnAppenders(e);
      }
    } else {
      for (ILoggingEvent e : ringBuffer) {
        this.aai.appendLoopOnAppenders(f.apply(e));
      }
    }
  }

  @Override
  public AppenderAttachableImpl<ILoggingEvent> appenderAttachableImpl() {
    return this.aai;
  }

}
