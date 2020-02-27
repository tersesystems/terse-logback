package com.tersesystems.logback.classic.encoder;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.tersesystems.logback.core.pattern.PatternLayoutEncoderBase;

/**
 * Create a pattern layout encoder that doesn't require that the parent is an appender.
 *
 * <p>This allows for encoders that can take encoders and so on.
 */
public class PatternLayoutEncoder extends PatternLayoutEncoderBase<ILoggingEvent> {

  @Override
  public void start() {
    PatternLayout patternLayout = new PatternLayout();
    patternLayout.setContext(context);
    patternLayout.setPattern(getPattern());
    patternLayout.setOutputPatternAsHeader(outputPatternAsHeader);
    patternLayout.start();
    this.layout = patternLayout;
    super.start();
  }
}
