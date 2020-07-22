package com.tersesystems.logback.allocrate;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import java.util.concurrent.atomic.AtomicLong;
import jvm_alloc_rate_meter.MeterThread;
import org.slf4j.Marker;

public class AllocationRateThresholdTurboFilter extends TurboFilter {

  private static final AllocationRateProducer rateProducer = AllocationRateProducer.getInstance();

  /** The default threshold is an allocation rate of 1000 MB/sec. */
  public long DEFAULT_THRESHOLD_RATE = 10000000;

  private long threshold = DEFAULT_THRESHOLD_RATE;

  private Level level = Level.TRACE;

  public void setLevel(String level) {
    this.level = Level.toLevel(level);
  }

  @Override
  public FilterReply decide(
      Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
    if (rateProducer.getAllocationRate() < threshold || level.isGreaterOrEqual(this.level)) {
      return FilterReply.NEUTRAL;
    } else {
      return FilterReply.DENY;
    }
  }

  public long getThreshold() {
    return threshold;
  }

  public void setThreshold(long threshold) {
    this.threshold = threshold;
  }
}
