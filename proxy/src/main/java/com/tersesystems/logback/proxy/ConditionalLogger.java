package com.tersesystems.logback.proxy;

import org.slf4j.Marker;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ConditionalLogger {

    void ifTrace(Supplier<Boolean> condition, Consumer<LoggerStatement> lc);
    void ifTrace(Marker marker, Supplier<Boolean> condition, Consumer<LoggerStatement> lc);

    void ifDebug(Supplier<Boolean> condition, Consumer<LoggerStatement> lc);
    void ifDebug(Marker marker, Supplier<Boolean> condition, Consumer<LoggerStatement> lc);

    void ifInfo(Supplier<Boolean> condition, Consumer<LoggerStatement> lc);
    void ifInfo(Marker marker, Supplier<Boolean> condition, Consumer<LoggerStatement> lc);

    void ifWarn(Supplier<Boolean> condition, Consumer<LoggerStatement> lc);
    void ifWarn(Marker marker, Supplier<Boolean> condition, Consumer<LoggerStatement> lc);

    void ifError(Supplier<Boolean> condition, Consumer<LoggerStatement> lc);
    void ifError(Marker marker, Supplier<Boolean> condition, Consumer<LoggerStatement> lc);
}
