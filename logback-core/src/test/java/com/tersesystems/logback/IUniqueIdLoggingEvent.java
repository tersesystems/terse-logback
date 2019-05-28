package com.tersesystems.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;

public interface IUniqueIdLoggingEvent extends ILoggingEvent {
    String uniqueId();
}