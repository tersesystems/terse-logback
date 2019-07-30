package com.tersesystems.logback.ringbuffer.event;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.spi.FilterReply;
import com.tersesystems.logback.classic.ILoggingEventFactory;
import com.tersesystems.logback.classic.LoggingEventFactory;
import com.tersesystems.logback.ringbuffer.MarkerRingBufferTurboFilter;
import com.tersesystems.logback.ringbuffer.RingBuffer;
import com.tersesystems.logback.ringbuffer.RingBufferAware;
import com.tersesystems.logback.ringbuffer.RingBufferMarkerFactory;
import net.logstash.logback.encoder.CompositeJsonEncoder;
import net.logstash.logback.encoder.LogstashEncoder;
import net.logstash.logback.marker.Markers;
import org.slf4j.Marker;

import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * This turbofilter appends buffered diagnostic events via logstash marker to the triggering event.
 *
 * Intended for use with LogstashEncoder, will not show anything on console.
 */
public class MarkerEventRingBufferTurboFilter extends MarkerRingBufferTurboFilter {

    private ILoggingEventFactory<ILoggingEvent> loggingEventFactory;

    private CompositeJsonEncoder<ILoggingEvent> encoder;

    private String fieldName;

    public CompositeJsonEncoder<ILoggingEvent> getEncoder() {
        return encoder;
    }

    public void setEncoder(CompositeJsonEncoder<ILoggingEvent> encoder) {
        this.encoder = encoder;
    }

    public ILoggingEventFactory<ILoggingEvent> getLoggingEventFactory() {
        return loggingEventFactory;
    }

    public void setLoggingEventFactory(ILoggingEventFactory<ILoggingEvent> loggingEventFactory) {
        this.loggingEventFactory = loggingEventFactory;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public void start() {
        if (this.loggingEventFactory == null) {
            this.loggingEventFactory = new LoggingEventFactory();
        }
        if (fieldName == null) {
            fieldName = "diagnosticEvents";
        }
        if (encoder == null) {
            this.encoder = new LogstashEncoder();
        }
        super.start();
    }

    protected String encodeEventToJson(ILoggingEvent event) {
        byte[] bytes = getEncoder().encode(event);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String msg, Object[] params, Throwable t) {
        if (isDumpTriggered(marker)) {
            // If triggered, log the event ourselves and pass down a deny.
            RingBuffer<ILoggingEvent> ringBuffer = getRingBuffer(marker);
            try {
                // Defer so it happens behind an async appender if possible
                Marker joinedMarker = Markers.defer(() -> {
                    // This is a kludgy way to create an array but appendArray / List<LogstashMarker> doesn't nest
                    String eventJson = ringBuffer.stream().map(this::encodeEventToJson).collect(Collectors.joining(","));
                    return Markers.appendRaw(getFieldName(), "[" + eventJson + "]");
                }).and(marker);
                ILoggingEventFactory<ILoggingEvent> loggingEventFactory = getLoggingEventFactory();
                ILoggingEvent event = loggingEventFactory.create(joinedMarker, logger, level, msg, params, t);
                logger.callAppenders(event);
            } finally {
                ringBuffer.clear();
            }
            return FilterReply.DENY;
        } else if (isRecordable(marker, logger, level)) {
            record(marker, logger, level, msg, params, t);
        }
        return FilterReply.NEUTRAL;
    }

    private boolean isRecordable(Marker marker, Logger logger, Level level) {
        // If the marker is going on a statement that's going to be logged in normal processing,
        // then don't allow it.
        if (level.isGreaterOrEqual(logger.getEffectiveLevel())) {
            return false;
        }
        return marker instanceof RingBufferMarkerFactory.RecordMarker;
    }

    private boolean isDumpTriggered(Marker marker) {
        return marker instanceof RingBufferMarkerFactory.TriggerMarker;
    }

    protected void record(Marker marker, Logger logger, Level level, String msg, Object[] params, Throwable t) {
        ILoggingEvent le = loggingEventFactory.create(marker, logger, level, msg, params, t);
        RingBuffer<ILoggingEvent> ringBuffer = getRingBuffer(marker);
        ringBuffer.append(le);
    }

    @SuppressWarnings("unchecked")
    protected RingBuffer<ILoggingEvent> getRingBuffer(Marker marker) {
        return ((RingBufferAware<ILoggingEvent>) marker).getRingBuffer();
    }

}
