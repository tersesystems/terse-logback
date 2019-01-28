package com.tersesystems.logback;

import org.slf4j.Logger;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ConditionalLogger {

    Supplier<Boolean> getCondition();

    void ifTrace(Consumer<LoggerStatement> lc);
    void ifTrace(Supplier<Boolean> condition, Consumer<LoggerStatement> lc);

    void ifDebug(Consumer<LoggerStatement> lc);
    void ifDebug(Supplier<Boolean> condition, Consumer<LoggerStatement> lc);

    void ifInfo(Consumer<LoggerStatement> lc);
    void ifInfo(Supplier<Boolean> condition, Consumer<LoggerStatement> lc);

    void ifWarn(Consumer<LoggerStatement> lc);
    void ifWarn(Supplier<Boolean> condition, Consumer<LoggerStatement> lc);

    void ifError(Consumer<LoggerStatement> lc);
    void ifError(Supplier<Boolean> condition, Consumer<LoggerStatement> lc);
}
