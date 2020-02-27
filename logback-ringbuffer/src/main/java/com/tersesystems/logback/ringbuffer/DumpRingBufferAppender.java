package com.tersesystems.logback.ringbuffer;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import com.tersesystems.logback.core.DefaultAppenderAttachable;

public class DumpRingBufferAppender extends UnsynchronizedAppenderBase<ILoggingEvent>
    implements RingBufferAttachable, DefaultAppenderAttachable<ILoggingEvent> {

  private final AppenderAttachableImpl<ILoggingEvent> aai = new AppenderAttachableImpl<>();
  private RingBuffer ringBuffer;

  // If poll is set, remove each element from the ring buffer as it's sent.
  private boolean poll = true;

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
    if (this.poll) {
      ILoggingEvent e;
      while ((e = ringBuffer.poll()) != null) {
        this.aai.appendLoopOnAppenders(e);
      }
    } else {
      for (ILoggingEvent e : ringBuffer) {
        this.aai.appendLoopOnAppenders(e);
      }
    }
  }

  @Override
  public AppenderAttachableImpl<ILoggingEvent> appenderAttachableImpl() {
    return this.aai;
  }
}
