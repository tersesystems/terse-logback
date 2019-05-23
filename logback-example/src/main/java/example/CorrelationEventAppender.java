package example;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.tersesystems.logback.EnrichingAppender;

public class CorrelationEventAppender extends EnrichingAppender<ILoggingEvent, ICorrelationLoggingEvent> {

    private final IdGenerator idGenerator = IdGenerator.getInstance();

    @Override
    protected ICorrelationLoggingEvent enrichEvent(ILoggingEvent eventObject) {
        return new CorrelationLoggingEvent(eventObject, idGenerator.generateCorrelationId());
    }
}

