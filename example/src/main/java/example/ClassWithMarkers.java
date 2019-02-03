package example;

import com.tersesystems.logback.proxy.LogstashMarkerContext;
import com.tersesystems.logback.proxy.MarkerContext;
import com.tersesystems.logback.proxy.ProxyContextLogger;
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class ClassWithMarkers {
    private final Logger logger = getLogger(getClass());

    public void doThingsWithMarker(String correlationId) {
        LogstashMarker context = Markers.append("correlationId", correlationId);
        logger.info(context, "log with marker explicitly");
    }

    public static void main(String[] args) {
        String correlationId = IdGenerator.getInstance().generateCorrelationId();
        ClassWithMarkers classWithMarkers = new ClassWithMarkers();
        classWithMarkers.doThingsWithMarker(correlationId);
    }
}