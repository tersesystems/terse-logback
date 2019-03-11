
## Controlling Logging

There are reasons why you would not want to log information you may normally log.

The historical reason for not logging is that there is a construction cost involved in creating parameters.  This is still true in a way today -- CPU and memory are not typically constraints for logging statements, but there are storage costs involved in producing logs.  Accumulated logs must be parsed and searched, making queries slower.

There is a `com.tersesystems.logback.ext.ProxyConditionalLogger` class that will apply preconditions to loggers, and so the logging will only happen when the preconditions are met:

```java
package example;

import com.tersesystems.logback.ext.*;
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
        ClassWithConditionalLogger classWithExtendedLoggers = new ClassWithConditionalLogger();
        classWithExtendedLoggers.doStuff();
    }
}
```
