package com.tersesystems.logback;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.UUID;

public class CorrelationEventAppender extends EnrichingAppender<ILoggingEvent, CorrelationEventAppender.ICorrelationLoggingEvent> {

    private final IdGenerator idGenerator = IdGenerator.getInstance();

    @Override
    protected ICorrelationLoggingEvent enrichEvent(ILoggingEvent eventObject) {
        return new CorrelationLoggingEvent(eventObject, idGenerator.generateCorrelationId());
    }

    public static class CorrelationLoggingEvent extends ProxyLoggingEvent implements ICorrelationLoggingEvent {

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

    public static interface ICorrelationLoggingEvent extends ILoggingEvent {
        String correlationId();
    }

    static class IdGenerator {
        private IdGenerator() {
        }

        private static class SingletonHolder {
            static final IdGenerator instance = new IdGenerator();
        }

        public static IdGenerator getInstance() {
            return SingletonHolder.instance;
        }

        public String generateCorrelationId() {
            return UUID.randomUUID().toString();
        }
    }
}

