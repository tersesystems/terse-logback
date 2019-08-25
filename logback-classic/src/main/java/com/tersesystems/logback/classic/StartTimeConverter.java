package com.tersesystems.logback.classic;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class StartTimeConverter extends ClassicConverter {
    @Override
    public String convert(ILoggingEvent event) {
        long startTimeInMillis = StartTime.from(event).toEpochMilli();
        return Long.toString(startTimeInMillis);
    }
}
