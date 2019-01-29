package com.tersesystems.logback.proxy;

import org.slf4j.Marker;

import java.util.function.Consumer;

public interface LazyLogger {

    void trace(Consumer<LoggerStatement> lc);

    void trace(Marker marker, Consumer<LoggerStatement> lc);
    
    void debug(Consumer<LoggerStatement> lc);

    void debug(Marker marker, Consumer<LoggerStatement> lc);
    
    void info(Consumer<LoggerStatement> lc);

    void info(Marker marker, Consumer<LoggerStatement> lc);

    void warn(Consumer<LoggerStatement> lc);

    void warn(Marker marker, Consumer<LoggerStatement> lc);

    void error(Consumer<LoggerStatement> lc);

    void error(Marker marker, Consumer<LoggerStatement> lc);
    
}