package example;

import org.slf4j.Logger;

import static net.logstash.logback.argument.StructuredArguments.*;
import static org.slf4j.LoggerFactory.*;

public class ClassWithStructuredArguments {
    private final Logger logger = getLogger(getClass());

    public void doThings(String correlationId) {
        if (logger.isInfoEnabled()) {
            logger.info("id is {}", value("correlationId", correlationId));
            logger.info("id is {}", keyValue("correlationId", correlationId));
            logger.info("id is {}", keyValue("correlationId", correlationId, "{0}=[{1}]"));
        }
    }

    public static void main(String[] args) {
        ClassWithStructuredArguments classWithStructuredArguments = new ClassWithStructuredArguments();
        classWithStructuredArguments.doThings("12345");
    }
}