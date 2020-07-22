package com.tersesystems.logback.allocrate;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class AllocationRateThresholdFilter extends Filter<ILoggingEvent> {

    private static final AllocationRateProducer rateProducer = AllocationRateProducer.getInstance();

    /** The default threshold is an allocation rate of 1000 MB/sec. */
    public long DEFAULT_THRESHOLD_RATE = 10000000;

    private long threshold = DEFAULT_THRESHOLD_RATE;

    private Level level = Level.TRACE;

    public void setLevel(String level) {
        this.level = Level.toLevel(level);
    }

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (rateProducer.getAllocationRate() < threshold || event.getLevel().isGreaterOrEqual(this.level)) {
            return FilterReply.NEUTRAL;
        } else {
            return FilterReply.DENY;
        }
    }
}
