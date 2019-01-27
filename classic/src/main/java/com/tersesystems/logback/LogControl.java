package com.tersesystems.logback;

import org.slf4j.Logger;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface LogControl {

    public void ifTrace(Consumer<Logger> lc);
    public void ifTrace(Supplier<Boolean> condition, Consumer<Logger> lc);

    public void ifDebug(Consumer<Logger> lc);
    public void ifDebug(Supplier<Boolean> condition, Consumer<Logger> lc);

    public void ifInfo(Consumer<Logger> lc);
    public void ifInfo(Supplier<Boolean> condition, Consumer<Logger> lc);

    public void ifWarn(Consumer<Logger> lc);
    public void ifWarn(Supplier<Boolean> condition, Consumer<Logger> lc);

    public void ifError(Consumer<Logger> lc);
    public void ifError(Supplier<Boolean> condition, Consumer<Logger> lc);
}
