package example;

import com.tersesystems.logback.proxy.*;
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import static org.slf4j.LoggerFactory.getLogger;

public class ClassWithConditionalLogger {

    private ClassWithConditionalLogger() {
    }

    private final Logger logger = getLogger(getClass());

    private void doStuff() {
        // Set up conditional logger to only log if this is my machine:
        final ConditionalLogger conditionalLogger = new ProxyConditionalLogger(logger, this::isDevelopmentEnvironment);

        String correlationId = IdGenerator.getInstance().generateCorrelationId();
        LogstashMarker context = Markers.append("correlationId", correlationId);

        // ProxyConditionalLogger will only log if this is my machine
        Logger conditionalLoggerAsNormalLogger = (Logger) conditionalLogger;
        conditionalLoggerAsNormalLogger.info("This will still only log if it's my machine");

        // Log only if the level is info and the above conditions are met AND it's tuesday
        conditionalLogger.ifInfo(this::objectIsNotTooLargeToLog, stmt -> {
            // Log very large thing in here...
            stmt.apply(context, "log if INFO && user.name == wsargent && objectIsNotTooLargeToLog()");
        });
    }

    private Boolean objectIsNotTooLargeToLog() {
        return true; // object is not too big
    }

    private Boolean isDevelopmentEnvironment(Level level) {
        return "wsargent".equals(System.getProperty("user.name"));
    }

    public static void main(String[] args) {
        ClassWithConditionalLogger classWithConditionalLogger = new ClassWithConditionalLogger();
        classWithConditionalLogger.doStuff();
    }
}
