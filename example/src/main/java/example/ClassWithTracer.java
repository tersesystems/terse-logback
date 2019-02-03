package example;

import com.tersesystems.logback.proxy.LogstashMarkerContext;
import com.tersesystems.logback.proxy.MarkerContext;
import com.tersesystems.logback.proxy.ProxyContextLogger;
import com.tersesystems.logback.TracerFactory;
import com.tersesystems.logback.proxy.ProxyContextLoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import static net.logstash.logback.marker.Markers.append;
import static org.slf4j.LoggerFactory.getLogger;

public class ClassWithTracer {

    // Add a TRACER marker to the request, and use a proxy context wrapper
    private Logger getContextLogger(Request request) {
        final TracerFactory tracerFactory = TracerFactory.getInstance();
        final Marker marker;
        if (request.queryStringContains("trace")) {
            marker = tracerFactory.createTracer(request.context().asMarker());
        } else {
            marker = request.context().asMarker();
        }
        return ProxyContextLoggerFactory.create(marker).getLogger(getClass().getName());
    }

    public void doThings(Request request) {
        Logger logger = getContextLogger(request);

        // This class is not logged at a TRACE level, so this should not show under
        // normal circumstances...
        if (logger.isTraceEnabled()) {
            logger.trace("This log message is only shown if the request has trace in the query string!");
        }
    }

    public static void main(String[] args) {
        ClassWithTracer classWithTracer = new ClassWithTracer();

        // run it without the trace flag
        Request request = new Request("foo=bar");
        classWithTracer.doThings(request);

        // run it WITH the trace flag
        Request requestWithTrace = new Request("foo=bar&trace=on");
        classWithTracer.doThings(requestWithTrace);
    }
}

class Request {
    private final MarkerContext context;
    private final String queryString;

    Request(String queryString) {
        String correlationId = IdGenerator.getInstance().generateCorrelationId();
        this.context = LogstashMarkerContext.create(append("correlationId", correlationId));
        this.queryString = queryString;
    }

    public MarkerContext context() {
        return context;
    }

    public boolean queryStringContains(String key) {
        return (queryString.contains(key));
    }
}
