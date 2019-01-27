package com.tersesystems.logback;

import org.slf4j.Logger;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class ProxyLogControl implements LogControl {

    private final Logger logger;
    private final Supplier<Boolean> predicate;

    public ProxyLogControl(Logger logger) {
        this.logger = logger;
        this.predicate = () -> true;
    }

    public ProxyLogControl(Logger logger, Supplier<Boolean> predicate) {
        this.logger = logger;
        this.predicate = predicate;
    }


    public void ifTrace(Consumer<Logger> lc) {
        if (logger.isTraceEnabled() && predicate.get()) {
            lc.accept(logger);
        }
    }

    public void ifTrace(Supplier<Boolean> condition, Consumer<Logger> lc) {
        if (logger.isTraceEnabled() && predicate.get() && condition.get()) {
            lc.accept(logger);
        }
    }

    public void ifDebug(Consumer<Logger> lc) {
        if (logger.isDebugEnabled() && predicate.get()) {
            lc.accept(logger);
        }
    }

    public void ifDebug(Supplier<Boolean> condition, Consumer<Logger> lc) {
        if (logger.isDebugEnabled() && predicate.get() && condition.get()) {
            lc.accept(logger);
        }
    }

    public void ifInfo(Consumer<Logger> lc) {
        if (logger.isInfoEnabled() && predicate.get()) {
            lc.accept(logger);
        }
    }

    public void ifInfo(Supplier<Boolean> condition, Consumer<Logger> lc) {
        if (logger.isInfoEnabled() && predicate.get() && condition.get()) {
            lc.accept(logger);
        }
    }

    public void ifWarn(Consumer<Logger> lc) {
        if (logger.isWarnEnabled() && predicate.get()) {
            lc.accept(logger);
        }
    }

    public void ifWarn(Supplier<Boolean> condition, Consumer<Logger> lc) {
        if (logger.isWarnEnabled() && predicate.get() && condition.get()) {
            lc.accept(logger);
        }
    }

    public void ifError(Consumer<Logger> lc) {
        if (logger.isErrorEnabled() && predicate.get()) {
            lc.accept(logger);
        }
    }

    public void ifError(Supplier<Boolean> condition, Consumer<Logger> lc) {
        if (logger.isErrorEnabled() && predicate.get() && condition.get()) {
            lc.accept(logger);
        }
    }

}