package com.tersesystems.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.jmx.MBeanUtil;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.status.ErrorStatus;
import com.udojava.jmx.wrapper.JMXBean;
import com.udojava.jmx.wrapper.JMXBeanOperation;
import com.udojava.jmx.wrapper.JMXBeanParameter;
import com.udojava.jmx.wrapper.JMXBeanWrapper;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * Class for setting log levels on the fly.
 */
@JMXBean(description = "MXBean for managing Logback")
public class LogbackMXBean implements LoggerContextListener {

    private static ObjectName objectName;
    private final LoggerContext loggingContext;

    static {
        try {
            objectName = new ObjectName("com.tersesystems.logback:type=LogbackMXBean,name=Logback");
        } catch (Exception e) {
            // impossible to have error here.
        }
    }

    private boolean started;

    public LogbackMXBean(LoggerContext lc) {
        started = true;
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

    public static LogbackMXBean register(LoggerContext lc) {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            if (!MBeanUtil.isRegistered(mbs, objectName)) {
                LogbackMXBean logbackMXBean = new LogbackMXBean(lc);
                JMXBeanWrapper wrappedBean = new JMXBeanWrapper(logbackMXBean);
                mbs.registerMBean(wrappedBean, objectName);
                return logbackMXBean;
            } else {
                return null;
            }
        } catch (Exception e) {
            lc.getStatusManager().add(new ErrorStatus("Cannot register bean!", e));
        }
        return null;
    }

    public static void unregister() {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            if (MBeanUtil.isRegistered(mbs, objectName)) {
                mbs.unregisterMBean(objectName);
            }
        } catch (Exception e) {
            // not a lot of point to showing exception here.
            //lc.getStatusManager().add(new ErrorStatus("Cannot register bean!", e));
        }
    }

    @Override
    public boolean isResetResistant() {
        return true;
    }

    @Override
    public void onStart(LoggerContext context) {
        started = true;
    }

    @Override
    public void onReset(LoggerContext context) {

    }

    public void onStop(LoggerContext context) {
        LogbackMXBean.unregister();
        started = false;
    }

    @Override
    public void onLevelChange(Logger logger, Level level) {

    }
}
