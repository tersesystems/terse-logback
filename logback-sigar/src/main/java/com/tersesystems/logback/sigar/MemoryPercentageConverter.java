package com.tersesystems.logback.sigar;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.SigarException;

public class MemoryPercentageConverter extends ClassicConverter implements SigarContextAware {
    @Override
    public String convert(ILoggingEvent event) {
        return getSigar().map(sigar -> {
            try {
                Mem mem = sigar.getMem();
                return format(mem);
            } catch (SigarException e) {
                addError("Cannot retrieve mem", e);
                return "";
            }
        }).orElseGet(() -> {
            addError("Sigar is not registered!");
            return "";
        });
    }

    private String format(Mem mem) {
        return used(mem) + " " + usedPercent(mem) + " " + total(mem);
    }

    private String used(Mem mem) {
        return "used=" + mem.getUsed();
    }

    private String usedPercent(Mem mem) {
        return "used%=" + mem.getUsedPercent();
    }

    private String total(Mem mem) {
        return "total=" + mem.getTotal();
    }
}
