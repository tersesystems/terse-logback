package example;

import com.tersesystems.logback.context.Context;
import com.tersesystems.logback.context.ContextImpl;
import com.tersesystems.logback.context.ProxyContextLoggerFactory;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class ClassWithTracer {

    // Add a TRACER marker to the request, and use a proxy context wrapper
    private Logger getContextLogger(Request request) {
        final Context context;
        if (request.queryStringContains("trace")) {
            context = request.context().withTracer();
        } else {
            context = request.context();
        }
        return ProxyContextLoggerFactory.create(context).getLogger(getClass().getName());
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
    private final Context context;
    private final String queryString;

    Request(String queryString) {
        String correlationId = IdGenerator.getInstance().generateCorrelationId();
        this.context = ContextImpl.create("correlationId", correlationId);
        this.queryString = queryString;
    }

    public Context context() {
        return context;
    }

    public boolean queryStringContains(String key) {
        return (queryString.contains(key));
    }
}
