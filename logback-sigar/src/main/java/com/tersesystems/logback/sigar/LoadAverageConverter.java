package com.tersesystems.logback.sigar;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.hyperic.sigar.SigarException;

public class LoadAverageConverter extends ClassicConverter implements SigarContextAware {
    @Override
    public String convert(ILoggingEvent event) {
        return getSigar().map(sigar -> {
            try {
                double[] loadAverage = sigar.getLoadAverage();
                return format(loadAverage);
            } catch (SigarException e) {
                addError("Cannot retrieve load average", e);
                return "";
            }
        }).orElseGet(() -> {
            addError("Sigar is not registered!");
            return "";
        });
    }

    private String format(double[] loadavg) {
        StringBuilder b = new StringBuilder();
        b.append("load1min=").append(loadavg[0]);
        b.append(" ");
        b.append("load5min=").append(loadavg[1]);
        b.append(" ");
        b.append("load15min=").append(loadavg[2]);
        return b.toString();
    }
}
