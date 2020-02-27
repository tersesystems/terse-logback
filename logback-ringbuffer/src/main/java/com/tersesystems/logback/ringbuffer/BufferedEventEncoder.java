package com.tersesystems.logback.ringbuffer;

import ch.qos.logback.classic.layout.TTLLLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.spi.ContextAwareBase;

/** A processed event encoder */
public class BufferedEventEncoder<E> extends ContextAwareBase implements Encoder<E> {
  private boolean started;

  private Encoder<E> encoder;

  public Encoder<E> getEncoder() {
    return encoder;
  }

  public void setEncoder(Encoder<E> encoder) {
    this.encoder = encoder;
  }

  @Override
  public byte[] headerBytes() {
    return new byte[0];
  }

  @Override
  public byte[] encode(E event) {
    if (event instanceof BufferedLoggingEvent) {
      BufferedLoggingEvent bufferedLoggingEvent = (BufferedLoggingEvent) event;
      return bufferedLoggingEvent.getEncodedData();
    } else {
      return this.encoder.encode(event);
    }
  }

  @Override
  public byte[] footerBytes() {
    return new byte[0];
  }

  @Override
  public void start() {
    if (this.encoder == null) {
      addInfo("Null encoder, using the default TTLLLayout");
      this.encoder = defaultEncoder();
      this.encoder.start();
      return;
    }
    started = true;
  }

  @SuppressWarnings("unchecked")
  LayoutWrappingEncoder<E> defaultEncoder() {
    LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
    encoder.setContext(getContext());
    TTLLLayout layout = new TTLLLayout();
    layout.setContext(getContext());
    layout.start();
    encoder.setLayout(layout);
    // Work around for the encoder needing a parent that's an appender :-/
    if (this.parent instanceof Appender) {
      encoder.setParent((Appender<ILoggingEvent>) this.parent);
    }
    return (LayoutWrappingEncoder<E>) encoder;
  }

  @Override
  public void stop() {
    this.encoder = null;
    started = false;
  }

  @Override
  public boolean isStarted() {
    return this.started;
  }

  Object parent;

  public void setParent(Object parent) {
    this.parent = parent;
  }
}
