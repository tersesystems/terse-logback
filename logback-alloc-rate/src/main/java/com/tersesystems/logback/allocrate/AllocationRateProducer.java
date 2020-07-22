package com.tersesystems.logback.allocrate;

import jvm_alloc_rate_meter.MeterThread;

import java.util.concurrent.atomic.AtomicLong;

public class AllocationRateProducer {

    private final MeterThread meterThread;
    private final AtomicLong allocRate;

    private AllocationRateProducer(MeterThread meterThread, AtomicLong allocRate) {
        this.meterThread = meterThread;
        this.allocRate = allocRate;
        start();
    }

    private void start() {
        if (! meterThread.isAlive()) {
            meterThread.start();
        }
    }

    public long getAllocationRate() {
        return allocRate.get();
    }

    public static AllocationRateProducer getInstance() {
        return LazyHolder.lazyInstance();
    }

    // https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
    private static final class LazyHolder {
        private static final AtomicLong allocRate = new AtomicLong();
        private static final MeterThread meterThread = new MeterThread(allocRate::set);
        private static final AllocationRateProducer producer = new AllocationRateProducer(meterThread, allocRate);

        static AllocationRateProducer lazyInstance() {
            return producer;
        }
    }

}