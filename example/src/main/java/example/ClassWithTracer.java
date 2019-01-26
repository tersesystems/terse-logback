package example;

import com.tersesystems.logback.ProxyContextLogger;
import com.tersesystems.logback.TracerFactory;
import net.logstash.logback.marker.LogstashMarker;
import org.slf4j.Logger;

import static net.logstash.logback.marker.Markers.*;
import static org.slf4j.LoggerFactory.*;

public class ClassWithTracer {

    // Add a TRACER marker to the request, and use a proxy context wrapper
    private Logger getContextLogger(Request request) {
        final TracerFactory tracerFactory = TracerFactory.getInstance();
        final LogstashMarker context;
        if (request.queryStringContains("trace")) {
            context = tracerFactory.createTracer(request.context());
        } else {
            context = request.context();
        }
        return new ProxyContextLogger(context, getLogger(getClass()));
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
    private final LogstashMarker context;
    private final String queryString;

    Request(String queryString) {
        String correlationId = IdGenerator.getInstance().generateCorrelationId();
        this.context = append("correlationId", correlationId);
        this.queryString = queryString;
    }

    public LogstashMarker context() {
        return context;
    }

    public boolean queryStringContains(String key) {
        return (queryString.contains(key));
    }
}
