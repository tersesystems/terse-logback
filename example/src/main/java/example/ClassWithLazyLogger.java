package example;

import com.tersesystems.logback.proxy.*;
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

public class ClassWithLazyLogger {

    private ClassWithLazyLogger() {
    }

    private final Logger logger = getLogger(getClass());

    private void useLogger(Logger logger) {
        logger.info("I use an INFO logger that came from a statement");
        logger.debug("If I try using DEBUG, then nothing happens!");
    }

    private void doStuff() {
        final LazyLogger lazyLogger = new ProxyLazyLogger(logger);

        String correlationId = IdGenerator.getInstance().generateCorrelationId();
        LogstashMarker context = Markers.append("correlationId", correlationId);

        // Can use an lazy info statement and a Consumer if that's easier...
        lazyLogger.info(stmt -> stmt.apply(context, "I use a consumer"));

        // Or you can use an optional logger statement:
        Optional<LoggerStatement> info = lazyLogger.info();

        // Or you can get the logging statement as a logger...
        info.map(stmt -> stmt.asLogger()).ifPresent(this::useLogger);
    }

    private Boolean objectIsNotTooLargeToLog() {
        return true; // object is not too big
    }

    private Boolean isDevelopmentEnvironment(Level level) {
        return "wsargent".equals(System.getProperty("user.name"));
    }

    public static void main(String[] args) {
        ClassWithLazyLogger classWithLazyLogger = new ClassWithLazyLogger();
        classWithLazyLogger.doStuff();
    }
}
