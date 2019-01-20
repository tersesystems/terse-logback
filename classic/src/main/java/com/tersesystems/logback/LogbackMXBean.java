package com.tersesystems.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.udojava.jmx.wrapper.JMXBean;
import com.udojava.jmx.wrapper.JMXBeanOperation;
import com.udojava.jmx.wrapper.JMXBeanParameter;

/**
 * Class for setting log levels on the fly.
 */
@JMXBean(description = "MXBean for managing Logback")
public class LogbackMXBean {

    private final LoggerContext loggingContext;

    public LogbackMXBean(LoggerContext lc) {
        loggingContext = lc;
    }

    @JMXBeanOperation(name = "Set Log Level to Error", description = "Sets logger to debug")
    public String setLogLevelError(
            @JMXBeanParameter(name = "name", description = "Fully qualified logger name") String name) {
        return setLogLevel(name, Level.ERROR);
    }

    @JMXBeanOperation(name = "Set Log Level to Warn", description = "Sets logger to debug")
    public String setLogLevelWarn(
            @JMXBeanParameter(name = "name", description = "Fully qualified logger name") String name) {
        return setLogLevel(name, Level.WARN);
    }

    @JMXBeanOperation(name = "Set Log Level to Info", description = "Sets logger to debug")
    public String setLogLevelInfo(
            @JMXBeanParameter(name = "name", description = "Fully qualified logger name") String name) {
        return setLogLevel(name, Level.INFO);
    }

    @JMXBeanOperation(name = "Set Log Level to Debug", description = "Sets logger to debug")
    public String setLogLevelDebug(
            @JMXBeanParameter(name = "name", description = "Fully qualified logger name") String name) {
        return setLogLevel(name, Level.DEBUG);
    }

    @JMXBeanOperation(name = "Set Log Level to Trace", description = "Sets logger to debug")
    public String setLogLevelTrace(
            @JMXBeanParameter(name = "name", description = "Fully qualified logger name") String name) {
        return setLogLevel(name, Level.TRACE);
    }

    private String setLogLevel(String name, Level level) {
        try {
            Logger logger = loggingContext.getLogger(name);
            logger.setLevel(level);
            return String.format("logger %s was set to %s!", name, level.toString());
        } catch (Exception e) {
            return String.format("Logger %s could not be set, because %s", name, e.getMessage());
        }
    }
}
