package com.tersesystems.logback.classic;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.Optional;

public class StartTimeConverter extends ClassicConverter {
    @Override
    public String convert(ILoggingEvent event) {
        Optional<String> optStartTime = StartTime.fromOptional(event).map(st -> Long.toString(st.toEpochMilli()));
        return optStartTime.orElse(null);
    }
}
