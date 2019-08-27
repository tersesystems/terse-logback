package com.tersesystems.logback.sigar.functions;

import org.hyperic.sigar.Sigar;

import java.util.function.Function;

public interface SigarFunction<T> extends Function<Sigar, T> {
}
