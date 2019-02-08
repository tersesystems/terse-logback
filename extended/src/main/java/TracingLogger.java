package com.tersesystems.logback.proxy;

import org.slf4j.event.Level;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;

// Borrowed from https://www.slf4j.org/extensions.html#extended_logger
public interface TracingLogger {
    /**
     * Log method entry.
     *
     * @param argArray
     *          supplied parameters
     */
    public void entry(Object... argArray);

    /**
     * Log method exit
     */
    public void exit();

    /**
     * Log method exit
     *
     * @param result
     *          The result of the method being exited
     */
    public <T> T exit(T result);
}
