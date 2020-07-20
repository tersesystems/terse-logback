package com.tersesystems.logback.allocrate;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import java.util.concurrent.atomic.AtomicLong;
import jvm_alloc_rate_meter.MeterThread;
import org.slf4j.Marker;

/** This class filters based on the memory allocation rate and the given */
public class AllocationRateTurboFilter extends TurboFilter {

  /** The default threshold is an allocation rate of 1000 MB/sec. */
  public long DEFAULT_THRESHOLD_RATE = 1000;

  private final AtomicLong allocRate = new AtomicLong();

  private MeterThread meterThread;

  private long threshold = DEFAULT_THRESHOLD_RATE;

  private Level level = Level.TRACE;

  @Override
  public void start() {
    super.start();
    if (meterThread == null) {
      meterThread =
          new MeterThread(
              bytesPerSec -> {
                double mbPerSec = bytesPerSec / 1e6;
                allocRate.set((long) mbPerSec);
              });
      meterThread.start();
    }
  }

  @Override
  public void stop() {
    super.stop();
    if (meterThread != null) {
      meterThread.terminate();
      meterThread = null;
    }
  }

  public void setLevel(String level) {
    this.level = Level.toLevel(level);
  }

  @Override
  public FilterReply decide(
      Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
    if (allocRate.get() < threshold || level.isGreaterOrEqual(this.level)) {
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
