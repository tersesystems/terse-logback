package com.tersesystems.logback.proxy;

import org.slf4j.Marker;

import java.util.function.*;
import java.util.Optional;

public interface ConditionalLogger {

    void ifTrace(Supplier<Boolean> condition, Consumer<LoggerStatement> lc);
    void ifTrace(Marker marker, Supplier<Boolean> condition, Consumer<LoggerStatement> lc);
    Optional<LoggerStatement> ifTrace(Supplier<Boolean> condition);
    Optional<LoggerStatement> ifTrace(Marker marker, Supplier<Boolean> condition);

    void ifDebug(Supplier<Boolean> condition, Consumer<LoggerStatement> lc);
    void ifDebug(Marker marker, Supplier<Boolean> condition, Consumer<LoggerStatement> lc);
    Optional<LoggerStatement> ifDebug(Supplier<Boolean> condition);
    Optional<LoggerStatement> ifDebug(Marker marker, Supplier<Boolean> condition);

    void ifInfo(Supplier<Boolean> condition, Consumer<LoggerStatement> lc);
    void ifInfo(Marker marker, Supplier<Boolean> condition, Consumer<LoggerStatement> lc);
    Optional<LoggerStatement> ifInfo(Supplier<Boolean> condition);
    Optional<LoggerStatement> ifInfo(Marker marker, Supplier<Boolean> condition);

    void ifWarn(Supplier<Boolean> condition, Consumer<LoggerStatement> lc);
    void ifWarn(Marker marker, Supplier<Boolean> condition, Consumer<LoggerStatement> lc);
    Optional<LoggerStatement> ifWarn(Supplier<Boolean> condition);
    Optional<LoggerStatement> ifWarn(Marker marker, Supplier<Boolean> condition);

    void ifError(Supplier<Boolean> condition, Consumer<LoggerStatement> lc);
    void ifError(Marker marker, Supplier<Boolean> condition, Consumer<LoggerStatement> lc);
    Optional<LoggerStatement> ifError(Supplier<Boolean> condition);
    Optional<LoggerStatement> ifError(Marker marker, Supplier<Boolean> condition);

}