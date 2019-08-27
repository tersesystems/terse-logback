package com.tersesystems.logback.sigar.functions;

import com.tersesystems.logback.sigar.SigarRuntimeException;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class SigarMemoryFunction implements SigarFunction<Mem> {
    @Override
    public Mem apply(Sigar sigar) {
        try {
            return sigar.getMem();
        } catch (SigarException e) {
            throw new SigarRuntimeException(e);
        }
    }
}
