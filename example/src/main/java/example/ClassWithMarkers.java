package example;

import net.logstash.logback.marker.LogstashMarker;
import org.slf4j.Logger;

import static net.logstash.logback.marker.Markers.append;
import static org.slf4j.LoggerFactory.getLogger;

public class ClassWithMarkers {
    private final Logger logger = getLogger(getClass());

    private final LogstashMarker baseContext;

    public ClassWithMarkers(LogstashMarker baseContext) {
        this.baseContext = baseContext;
    }

    public void doThings(String correlationId) {
        if (logger.isInfoEnabled()) {
            // Any existing context AND the new correlation id
            LogstashMarker context = baseContext.and(append("correlationId", correlationId));
            // Use markers if you don't want any correlation id to show up in text output
            logger.info(context, "id is whatever");
        }
    }

    public static void main(String[] args) {
        LogstashMarker context = append("foo", "bar");
        ClassWithMarkers classWithMarkers = new ClassWithMarkers(context);
        classWithMarkers.doThings("12345");
    }
}