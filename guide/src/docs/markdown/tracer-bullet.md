
## Tracer Bullet Logging

The `AppLogger` makes reference to a tracer, but doesn't go into detail.  

Using a context also allows you the option to do "tracing bullet" logging, where some extra context, such as a query parameter in an HTTP request, could cause a logger to log at a lower level than it would normally do to a special marker.  You can use this for debugging on the fly without changing logger levels, or use it for random sampling of some number of operations.

Defining the following turbo filter in `logback.xml`:

```xml
<turboFilter class="ch.qos.logback.classic.turbo.MarkerFilter">
  <Name>TRACER_FILTER</Name>
  <Marker>TRACER</Marker>
  <OnMatch>ACCEPT</OnMatch>
</turboFilter>
```

and adding it to an existing marker and wrapping it in a context logger, you can get:

```java
package example;

public class ClassWithTracer {

    // Add tracer to the context, and return a logger that covers over the context.
    private AppLogger getContextLogger(Request request) {
        final AppContext context;
        if (request.queryStringContains("trace")) {
            context = request.context().withTracer();
        } else {
            context = request.context();
        }
        return AppLoggerFactory.create(context).getLogger(getClass());
    }

    public void doThings(Request request) {
        AppLogger logger = getContextLogger(request);

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
```

which gives the following output:

```text
2019-01-26T18:40:39.088+0000 [TRACE] example.ClassWithTracer in main - This log message is only shown if the request has trace in the query string!
```

```json
{"@timestamp":"2019-01-26T18:40:39.088+00:00","@version":"1","message":"This log message is only shown if the request has trace in the query string!","logger_name":"example.ClassWithTracer","thread_name":"main","level":"TRACE","level_value":5000,"tags":["TRACER"],"correlationId":"FX1UlmU3VfqlX0qxArsAAA"}
```