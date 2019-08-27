package com.tersesystems.logback.sigar;

public class SigarRuntimeException extends RuntimeException {

    public SigarRuntimeException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public SigarRuntimeException(Throwable throwable) {
        super(throwable);
    }

    public SigarRuntimeException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
