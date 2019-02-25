package com.tersesystems.logback.proxy;

import org.slf4j.Marker;

import java.util.Optional;
import java.util.function.Consumer;


public interface LazyLogger {

    void trace(Consumer<LoggerStatement> lc);
    Optional<LoggerStatement> trace();

    void trace(Marker marker, Consumer<LoggerStatement> lc);
    Optional<LoggerStatement> trace(Marker marker);


    void debug(Consumer<LoggerStatement> lc);
    Optional<LoggerStatement> debug();

    void debug(Marker marker, Consumer<LoggerStatement> lc);
    Optional<LoggerStatement> debug(Marker marker);


    void info(Consumer<LoggerStatement> lc);
    Optional<LoggerStatement> info();

    void info(Marker marker, Consumer<LoggerStatement> lc);
    Optional<LoggerStatement> info(Marker marker);


    void warn(Consumer<LoggerStatement> lc);
    Optional<LoggerStatement> warn();

    void warn(Marker marker, Consumer<LoggerStatement> lc);
    Optional<LoggerStatement> warn(Marker marker);


    void error(Consumer<LoggerStatement> lc);
    Optional<LoggerStatement> error();

    void error(Marker marker, Consumer<LoggerStatement> lc);
    Optional<LoggerStatement> error(Marker marker);

}