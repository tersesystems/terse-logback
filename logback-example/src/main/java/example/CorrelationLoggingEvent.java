package example;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.tersesystems.logback.ProxyLoggingEvent;

public class CorrelationLoggingEvent extends ProxyLoggingEvent implements ICorrelationLoggingEvent {

    private final String correlationId;

    public CorrelationLoggingEvent(ILoggingEvent delegate, String correlationId) {
        super(delegate);
        this.correlationId = correlationId;
    }

    @Override
    public String correlationId() {
        return this.correlationId;
    }
}
