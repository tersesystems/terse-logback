package com.tersesystems.logback;

import ch.qos.logback.core.pattern.DynamicConverter;

public class UniqueIdConverter extends DynamicConverter<IUniqueIdLoggingEvent> {
    @Override
    public String convert(IUniqueIdLoggingEvent event) {
        return event.uniqueId();
    }
}
