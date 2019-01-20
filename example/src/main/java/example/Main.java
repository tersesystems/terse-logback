package example;

import net.logstash.logback.marker.LogstashMarker;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.logstash.logback.argument.StructuredArguments.keyValue;
import static net.logstash.logback.marker.Markers.append;
import static org.slf4j.Logger.ROOT_LOGGER_NAME;

/**
 * Example program that uses terse-logback configuration.
 */
public class Main {

    public static void main(String[] args) {
        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();

        Logger rootLogger = loggerFactory.getLogger(ROOT_LOGGER_NAME);
        Logger exampleLogger = loggerFactory.getLogger("example");
        Logger nestedLogger = loggerFactory.getLogger("deeply.nested.package");

        rootLogger.info("Logging at info level using root logger");
        rootLogger.debug("Logging at debug level using root logger");

        exampleLogger.info("Logging at info level using example logger");
        exampleLogger.debug("Logging at debug level using example logger");

        nestedLogger.trace("Logging at trace level using deeply nested logger");

        // Determining what to add to your context is a big question in itself, see
        // https://www.honeycomb.io/blog/event-foo-what-should-i-add-to-an-event/

        // Add extra JSON fields using StructuredArguments and Markers
        // https://github.com/logstash/logstash-logback-encoder#event-specific-custom-fields
        IdGenerator idgen = IdGenerator.getInstance();
        String correlationId = idgen.generateCorrelationId();
        LogstashMarker context = append("correlationId", correlationId);

        if (exampleLogger.isDebugEnabled()) {
            exampleLogger.debug(context, "This is a message with context that will show up in JSON");
        }

        if (nestedLogger.isTraceEnabled()) {
            //  Add "name":"value" to the JSON output and add name=[value] to the formatted message using a custom format.
            nestedLogger.trace(context, "log message {}", keyValue("name", "value", "{0}=[{1}]"));
        }
    }
}

