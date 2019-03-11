
## Logging with Injected Context

When you're using structured logging, you'll inevitably have to pass around the `LogstashMarker` or `StructuredArgument` with it so that you can add context to your logging.  In the past, the recommended way to do this was MDC.

Avoid [Mapped Diagnostic Context](https://logback.qos.ch/manual/mdc.html).  MDC is a well known way of adding context to logging, but there are several things that make it problematic.  

MDC does not deal well with multi-threaded applications which may pass execution between several threads.  Code that uses `CompletableFuture` and `ExecutorService` may not work reliably with MDC.  A child thread does not automatically inherit a copy of the mapped diagnostic context of its parent.  MDC also breaks silently: when MDC assumptions are violated, there is no indication that the wrong contextual information is being displayed.

There are numerous workarounds, but it's safer and easier to use an explicit context as a field or parameter.  If you don't want to manage this in your logger directly, then the safest way is to handle it through injection, also known as using constructor parameters.

When you create an instance, you can pass in a single `org.slf4j.ILoggerFactory` instance that will create your loggers for you.  

```java
package example;

import com.tersesystems.logback.context.logstash.LogstashContext;
import com.tersesystems.logback.context.logstash.LogstashLoggerFactory;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class ClassWithContext {

    static class ObliviousToContext {
        private final Logger logger;

        public ObliviousToContext(ILoggerFactory lf) {
            this.logger = lf.getLogger(this.getClass().getName());
        }

        public void doStuff() {
            logger.info("hello world!");
        }
    }

    public static void main(String[] args) {
        // You can create objects that are oblivious to context, and just use the base
        // logstash markers...
        String correlationId = IdGenerator.getInstance().generateCorrelationId();
        LogstashContext context = LogstashContext.create("correlationId", correlationId);
        LogstashLoggerFactory loggerFactory = LogstashLoggerFactory.create(context);
        ObliviousToContext obliviousToContext = new ObliviousToContext(loggerFactory);
        obliviousToContext.doStuff();

        // Or you can create your own context and futzs with it.
        // Here we create an AppContext / AppLogger / AppLoggerFactory that lets us
        // set domain specific attributes on the context.
        AppContext appContext = AppContext.create().withCorrelationId(correlationId);
        AwareOfContext awareOfContext = new AwareOfContext(appContext);
        awareOfContext.doStuff();
    }

    private static class AwareOfContext {
        private final AppContext appContext;
        private final AppLogger logger;

        public AwareOfContext(AppContext appContext) {
            this.appContext = appContext;
            this.logger = AppLoggerFactory.create().getLogger(getClass()).withContext(appContext);
        }

        public void doStuff() {
            logger.info("My correlation id is {}", appContext.getCorrelationId().orElse("null"));
        }
    }
}
```

In the second example, an `AppContext` / `AppLogger` is used -- this is an example of domain specific methods and fields being added to the context.

```java
package example;

import com.tersesystems.logback.context.TracerFactory;
import com.tersesystems.logback.context.Context;
import com.tersesystems.logback.context.logstash.AbstractLogstashContext;
import com.tersesystems.logback.context.logstash.AbstractLogstashLoggerFactory;
import com.tersesystems.logback.context.logstash.AbstractLogstashLogger;
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;


class AppContext extends AbstractLogstashContext<AppContext> {

    public static final String CORRELATION_ID = "correlationId";
    private final boolean tracer;

    protected AppContext(Map<?, ?> entries, boolean tracer) {
        super(entries);
        this.tracer = tracer;
    }

    public static AppContext create() {
        return new AppContext(Collections.emptyMap(), false);
    }

    public static AppContext create(Object key, Object value) {
        return new AppContext(Collections.singletonMap(key, value), false);
    }

    public Optional<String> getCorrelationId() {
        return Stream.of(entries().get(CORRELATION_ID))
                .map(cid -> (String) cid)
                .findFirst();
    }

    public AppContext withCorrelationId(String correlationId) {
        return and(AppContext.create(CORRELATION_ID, correlationId));
    }

    @Override
    public AppContext withTracer() {
        return create(entries(), true);
    }

    public boolean isTracingEnabled() {
        return tracer;
    }

    @Override
    public LogstashMarker asMarker() {
        if (isTracingEnabled()) {
            return Markers.appendEntries(entries()).and(TracerFactory.getInstance().createTracer());
        } else {
            return Markers.appendEntries(entries());
        }
    }

    @Override
    public AppContext and(Context<? extends Marker, ?> otherContext) {
        boolean otherTracing = (otherContext instanceof AppContext) && ((AppContext) otherContext).isTracingEnabled();
        // XXX Same as LogstashContext -- is there a way to access this directly?
        Map<Object, Object> mergedEntries = new HashMap<>(this.entries());
        mergedEntries.putAll(otherContext.entries());
        return new AppContext(mergedEntries, this.isTracingEnabled() || otherTracing);
    }

}

class AppLogger extends AbstractLogstashLogger<AppContext, Logger, AppLogger> {

    public AppLogger(AppContext context, Logger logger) {
        super(context, logger);
    }

    @Override
    public AppLogger withContext(AppContext otherContext) {
        return new AppLogger(this.context.and(otherContext), this.logger);
    }
}

class AppLoggerFactory extends AbstractLogstashLoggerFactory<AppContext, AppLogger, ILoggerFactory, AppLoggerFactory> {

    protected AppLoggerFactory(AppContext context, ILoggerFactory loggerFactory) {
        super(context, loggerFactory);
    }

    @Override
    public AppLoggerFactory withContext(AppContext context) {
        return new AppLoggerFactory(getContext().and(context), getILoggerFactory());
    }

    @Override
    public AppLogger getLogger(String name) {
        return new AppLogger(AppContext.create(), getILoggerFactory().getLogger(name));
    }

    public static AppLoggerFactory create() {
        return create(AppContext.create());
    }

    public static AppLoggerFactory create(AppContext context) {
        return new AppLoggerFactory(context, LoggerFactory.getILoggerFactory());
    }

}
```

This style of programming does assume that you can control the instantiation of your objects, and it doesn't go into some of the details such as accumulating extra context.  Keeping a context object around so you can accumulate more context may be a good idea in some circumstances.