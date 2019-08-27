package com.tersesystems.logback.sigar.functions;

import com.tersesystems.logback.sigar.SigarRuntimeException;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class SigarLoadAverageFunction implements SigarFunction<double[]> {
    @Override
    public double[] apply(Sigar sigar) {
        try {
            return sigar.getLoadAverage();
        } catch (SigarException e) {
            throw new SigarRuntimeException(e);
        }
    }
}
