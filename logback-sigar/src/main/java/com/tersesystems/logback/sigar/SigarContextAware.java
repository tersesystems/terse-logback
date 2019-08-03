package com.tersesystems.logback.sigar;

import ch.qos.logback.core.spi.ContextAware;
import org.hyperic.sigar.Sigar;

import java.util.Optional;

import static com.tersesystems.logback.sigar.SigarConstants.SIGAR_CTX_KEY;

public interface SigarContextAware extends ContextAware {
    default Optional<Sigar> getSigar() {
        return Optional.ofNullable((Sigar) getContext().getObject(SIGAR_CTX_KEY));
    }
}
