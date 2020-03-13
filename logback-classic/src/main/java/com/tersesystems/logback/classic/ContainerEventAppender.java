package com.tersesystems.logback.classic;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.tersesystems.logback.core.DecoratingAppender;

/**
 * This appender decorates the out of the box logging event with a component system, which allows
 * extra attributes to be added to the event.
 */
public class ContainerEventAppender
    extends DecoratingAppender<ILoggingEvent, IContainerLoggingEvent> {
  @Override
  protected IContainerLoggingEvent decorateEvent(ILoggingEvent eventObject) {
    return new ContainerProxyLoggingEvent(eventObject);
  }
}
