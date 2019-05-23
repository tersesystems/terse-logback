package com.tersesystems.logback;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class CorrelationIdConverter extends ClassicConverter {
    @Override
    public String convert(ILoggingEvent event) {
        return ((CorrelationEventAppender.ICorrelationLoggingEvent) event).correlationId();
    }
}
