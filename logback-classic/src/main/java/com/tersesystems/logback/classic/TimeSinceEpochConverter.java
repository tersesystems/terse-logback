package com.tersesystems.logback.classic;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class TimeSinceEpochConverter extends ClassicConverter {
    @Override
    public String convert(ILoggingEvent event) {
        return Long.toString(event.getTimeStamp());
    }
}
