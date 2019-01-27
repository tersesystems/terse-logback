package example;

import com.tersesystems.logback.LogControl;
import com.tersesystems.logback.ProxyLogControl;
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class ClassWithLogControl {

    private ClassWithLogControl() {
    }

    private final Logger logger = getLogger(getClass());

    private void doStuff() {
        // Set up log control to filter all log statements
        final LogControl logControl = new ProxyLogControl(logger, this::isMyMachine);

        String correlationId = IdGenerator.getInstance().generateCorrelationId();
        LogstashMarker context = Markers.append("correlationId", correlationId);

        // Log only if the level is info and the given condition has been met:
        logControl.ifInfo(logger -> logger.info(context, "log if INFO && user.name == wsargent"));

        // Log only if the level is info and the above conditions are met AND it's tuesday
        logControl.ifInfo(this::objectIsNotTooLargeToLog, logger -> {
            // Log very large thing in here...
            logger.info(context, "log if INFO && user.name == wsargent && objectIsTooLargeToLog()");
        });
    }

    private Boolean objectIsNotTooLargeToLog() {
        return false; // object is too big
    }

    private Boolean isMyMachine() {
        return "wsargent".equals(System.getProperty("user.name"));
    }

    public static void main(String[] args) {
        ClassWithLogControl classWithLogControl = new ClassWithLogControl();
        classWithLogControl.doStuff();
    }
}
