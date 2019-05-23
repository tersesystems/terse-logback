package example;

import ch.qos.logback.classic.spi.ILoggingEvent;

public interface ICorrelationLoggingEvent extends ILoggingEvent {
    String correlationId();
}
