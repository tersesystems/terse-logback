package com.tersesystems.logback.sigar.functions;

import com.tersesystems.logback.sigar.SigarRuntimeException;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class SigarCpuPercFunction implements SigarFunction<CpuPerc> {
    @Override
    public CpuPerc apply(Sigar sigar) {
        try {
            return sigar.getCpuPerc();
        } catch (SigarException e) {
            throw new SigarRuntimeException(e);
        }
    }
}
