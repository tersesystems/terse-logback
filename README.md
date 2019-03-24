# Structured Logging Example with Logback

This is a Java project that shows how to use [Logback](https://logback.qos.ch/manual/index.html) effectively for structured logging.  It should show how you configure Logback, and how you can reduce the amount of complexity in your end projects by packaging your logging appenders and configurators in a distinct project.

## Project Setup

The project is configured into several modules: `censor`, `ext`, `classic`, `example`, and `guice-example`.  The most relevant ones to start with are `classic` and `example`.

The `classic` module contains all the logback code and the appenders, and is intended to be deployed as a small helper library for your other projects, managed through Maven and an artifact manager, or just by packaging the JAR.  The `example` project depends on `classic`, and contains the "end user" experience where log levels are adjusted and JSON can be pretty printed or not.

Notably, the `example` project cannot touch the appenders directly, and has no control over the format of the JSON appender -- console and text patterns can be overridden for developer convenience.  By enforcing a [separation of concerns](https://en.wikipedia.org/wiki/Separation_of_concerns) between **logger configuration** and **logging levels**, it is easy and simple to manage appenders in one place, e.g. going from file appenders to TCP appenders, adding filters for sensitive information, or collapsing repeated log information.

The `guice-example` shows a logback factory that is exposed through a `Provider` in Guice.

This is not intended to be a drop in replacement or a straight library dependency.  You will want to modify this to your own tastes.

## What is Structured Logging?

It's logging in JSON.  Technically, you could be logging in another structure like XML or JSON, but almost everyone uses JSON.  It's been around for [a while](https://www.kartar.net/2015/12/structured-logging/).  Technically, since there are several JSON objects all in one file / stream, this is called "newline delimited JSON" or [NDJSON](http://ndjson.org/) or [jsonlines](http://jsonlines.org/).  In this project, both text and JSON formats are rendered independently, but if you only output JSON it's not a huge deal, because you can read JSON logs as text with a special log viewer such as [jl](https://github.com/koenbollen/jl/blob/master/README.md).

Semantically, a log entry typically has multiple pieces of information associated with it, described as "high cardinality" by observability geeks.  Structured logging means that the cardinality goes from "closed" -- you can only log things that you have defined fields for -- to "open", where you can add arbitrary fields and objects to your log entry as long as it's JSON.

Structured logging means that you can add more context to logs and do more with them without having to do regexes.  As [Honeycomb](https://honeycomb.io) [describes it](
https://www.honeycomb.io/blog/you-could-have-invented-structured-logging/):

> Structured logging is really all about giving yourself — and your team — a logging API to help you provide consistent context in events. An unstructured logger accepts strings. A structured logger accepts a map, hash, or dictionary that describes all the attributes you can think of for an event.

Logs are [different from events](https://www.honeycomb.io/blog/how-are-structured-logs-different-from-events/).  All events can be represented as logs, but not all logs are events.  Many logs are only portions of events.  An event is a conceptual abstraction and a log is one possible representation of that abstraction.

Logs are also different from metrics.  A metric represents a single number.  You can extract metrics from logs, but it's a very expensive way of going about it.

## Adding Context

There is a question of what you want to add when you log.  This is a matter of taste, but in general you should log so that you [create a consistent narrative](https://www.honeycomb.io/blog/event-foo-constructing-a-coherent-narrative/).  As previously mentioned, a log may indicate a portion of an event, so you want to log where doing so would help tell a story of what happened afterwards.

There are some things you should [always add to an event](https://www.honeycomb.io/blog/event-foo-what-should-i-add-to-an-event/), such as who is talking to your service, what they're asking, business relevant fields, additional context around your service / environment, response time and particulars. You should add units to your field names when you measure a quantity, i.e. `response_time_ms`, and add a "human readable" version of internal information if available.

You should add [context to your logs](https://www.honeycomb.io/blog/event-foo-moar-context-better-events/) that helps differentiate it from its peers, so you never have to guess where the source of a log is coming from.

Adding a [correlation id](https://blog.rapid7.com/2016/12/23/the-value-of-correlation-ids/) helps you [design for results](https://www.honeycomb.io/blog/event-foo-designing-for-results/) and tie your logs into a coherent event.  You don't need to use a UUID: a [flake id](https://github.com/boundary/flake) will probably be better for you.  I'm using [idem](https://github.com/mguenther/idem/) here, but most things will work.

So, we know what structured logging is now.  What does it look like in SLF4J?

## Adding Structure to Logging

SLF4J doesn't have specific support for structured logging, but [logstash-logback-encoder](https://github.com/logstash/logstash-logback-encoder/tree/logstash-logback-encoder-5.2#logback-json-encoder) does.  It's complete and comprehensive, but buried in a section called [Event specific custom fields](https://github.com/logstash/logstash-logback-encoder/tree/logstash-logback-encoder-5.2#event-specific-custom-fields).

Event specific custom fields are implemented in two ways: through [`net.logstash.logback.argument.StructuredArguments`](https://github.com/logstash/logstash-logback-encoder/blob/logstash-logback-encoder-5.2/src/main/java/net/logstash/logback/argument/StructuredArguments.java), which adds structured information through parameters, and [`net.logstash.logback.marker.Markers`](https://github.com/logstash/logstash-logback-encoder/blob/logstash-logback-encoder-5.2/src/main/java/net/logstash/logback/marker/Markers.java), which adds structured information through the `org.slf4j.Marker` API.

### StructuredArguments

`StructuredArguments` write out both to the text appenders and to the JSON appenders.  There is extra "key information" added to the JSON, and you see the value show up in the message.

```java
package example;

import org.slf4j.Logger;

import static net.logstash.logback.argument.StructuredArguments.*;
import static org.slf4j.LoggerFactory.*;

public class ClassWithStructuredArguments {
    private final Logger logger = getLogger(getClass());

    public void logValue(String correlationId) {
        if (logger.isInfoEnabled()) {
            logger.info("id is {}", value("correlationId", correlationId));
        }
    }

    public void logNameAndValue(String correlationId) {
        logger.info("id is {}", keyValue("correlationId", correlationId));
    }

    public void logNameAndValueWithFormat(String correlationId) {
        logger.info("id is {}", keyValue("correlationId", correlationId, "{0}=[{1}]"));
    }

    public void doThings(String correlationId) {
        logValue(correlationId);
        logNameAndValue(correlationId);
        logNameAndValueWithFormat(correlationId);
    }

    public static void main(String[] args) {
        String correlationId = IdGenerator.getInstance().generateCorrelationId();
        ClassWithStructuredArguments classWithStructuredArguments = new ClassWithStructuredArguments();
        classWithStructuredArguments.doThings(correlationId);
    }
}
```

This produces the following output in text:

```text
2019-01-20T23:24:40.004+0000 [INFO ] example.ClassWithStructuredArguments in main - id is FXtylIyzDbj9rfs7BRCAAA
2019-01-20T23:24:40.006+0000 [INFO ] example.ClassWithStructuredArguments in main - id is correlationId=FXtylIyzDbj9rfs7BRCAAA
2019-01-20T23:24:40.006+0000 [INFO ] example.ClassWithStructuredArguments in main - id is correlationId=[FXtylIyzDbj9rfs7BRCAAA]
```

and in JSON:

```json
{"@timestamp":"2019-01-20T23:24:40.004+00:00","@version":"1","message":"id is FXtylIyzDbj9rfs7BRCAAA","logger_name":"example.ClassWithStructuredArguments","thread_name":"main","level":"INFO","level_value":20000,"correlationId":"FXtylIyzDbj9rfs7BRCAAA"}
{"@timestamp":"2019-01-20T23:24:40.006+00:00","@version":"1","message":"id is correlationId=FXtylIyzDbj9rfs7BRCAAA","logger_name":"example.ClassWithStructuredArguments","thread_name":"main","level":"INFO","level_value":20000,"correlationId":"FXtylIyzDbj9rfs7BRCAAA"}
{"@timestamp":"2019-01-20T23:24:40.006+00:00","@version":"1","message":"id is correlationId=[FXtylIyzDbj9rfs7BRCAAA]","logger_name":"example.ClassWithStructuredArguments","thread_name":"main","level":"INFO","level_value":20000,"correlationId":"FXtylIyzDbj9rfs7BRCAAA"}
```

### Markers

If you want to add more context and don't want it to show up in the message, you can use [`net.logstash.logback.marker.Markers`](https://github.com/logstash/logstash-logback-encoder/blob/logstash-logback-encoder-5.2/src/main/java/net/logstash/logback/marker/Markers.java) instead.

```java
package example;

import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class ClassWithMarkers {
    private final Logger logger = getLogger(getClass());

    public void doThingsWithMarker(String correlationId) {
        LogstashMarker logstashMarker = Markers.append("correlationId", correlationId);
        logger.info(logstashMarker, "log with marker explicitly");
    }

    public static void main(String[] args) {
        String correlationId = IdGenerator.getInstance().generateCorrelationId();
        ClassWithMarkers classWithMarkers = new ClassWithMarkers();
        classWithMarkers.doThingsWithMarker(correlationId);
    }
}
```

This produces the following text:

```text
2019-01-20T23:26:50.351+0000 [INFO ] example.ClassWithMarkers in main - log with marker explicitly
```

and the following JSON:

```json
{"@timestamp":"2019-01-20T23:26:50.351+00:00","@version":"1","message":"log with marker explicitly","logger_name":"example.ClassWithMarkers","thread_name":"main","level":"INFO","level_value":20000,"correlationId":"FXtylIy0T878gCNIdfWAAA"}
```

If you don't want to pass through anything at all, and instead use a proxy logger, you can use `com.tersesystems.logback.context.logstash.LogstashLogger`, which applies it under the hood.  Adding state to the logger is one of those useful tricks that can make life easier, as long as you implement `org.slf4j.Logger` and don't expose your logger to the world.  This is discussed in the "Logging with Injected Context" section.

## Controlling Logging

There are reasons why you would not want to log information you may normally log.

The historical reason for not logging is that there is a construction cost involved in creating parameters.  This is still true in a way today -- CPU and memory are not typically constraints for logging statements, but there are storage costs involved in producing logs.  Accumulated logs must be parsed and searched, making queries slower.

There is a `com.tersesystems.logback.ext.ProxyConditionalLogger` class that will apply preconditions to loggers, and so the logging will only happen when the preconditions are met:

```java
package example;

import com.tersesystems.logback.ext.*;
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import static org.slf4j.LoggerFactory.getLogger;

public class ClassWithConditionalLogger {

    private ClassWithConditionalLogger() {
    }

    private final Logger logger = getLogger(getClass());

    private void doStuff() {
        // Set up conditional logger to only log if this is my machine:
        final ConditionalLogger conditionalLogger = new ProxyConditionalLogger(logger, this::isDevelopmentEnvironment);

        String correlationId = IdGenerator.getInstance().generateCorrelationId();
        LogstashMarker context = Markers.append("correlationId", correlationId);

        // ProxyConditionalLogger will only log if this is my machine
        Logger conditionalLoggerAsNormalLogger = (Logger) conditionalLogger;
        conditionalLoggerAsNormalLogger.info("This will still only log if it's my machine");

        // Log only if the level is info and the above conditions are met AND it's tuesday
        conditionalLogger.ifInfo(this::objectIsNotTooLargeToLog, stmt -> {
            // Log very large thing in here...
            stmt.apply(context, "log if INFO && user.name == wsargent && objectIsNotTooLargeToLog()");
        });
    }

    private Boolean objectIsNotTooLargeToLog() {
        return true; // object is not too big
    }

    private Boolean isDevelopmentEnvironment(Level level) {
        return "wsargent".equals(System.getProperty("user.name"));
    }

    public static void main(String[] args) {
        ClassWithConditionalLogger classWithExtendedLoggers = new ClassWithConditionalLogger();
        classWithExtendedLoggers.doStuff();
    }
}
```

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

## Instrumenting Logging Code with Byte Buddy

If you have library code that doesn't pass around `ILoggerFactory` and doesn't let you add information to logging, then you can get around this by instrumenting the code with [Byte Buddy](https://bytebuddy.net/).  Using Byte Buddy, you can do fun things like override `Security.setSystemManager` with [your own implementation](https://tersesystems.com/blog/2016/01/19/redefining-java-dot-lang-dot-system/), so using Byte Buddy to decorate code with `enter` and `exit` logging statements is relatively straightforward.

There's two different ways to do it.  You can use interception, which gives you a straightforward method delegation model, or you can use `Advice`, which rewrites the bytecode inline before the JVM gets to it.  Either way, you can write to a logger without touching the class itself, and you can modify which classes and methods you touch.  

I like this approach better than the annotation or aspect-oriented programming approaches, because it is completely transparent to the code and gives the same performance as inline code.  I use a `ThreadLocal` logger here, as it gives me more control over logging capabilities than using MDC would, but there are many options available.

```java
package example;

import com.tersesystems.logback.bytebuddy.ClassAdviceRewriter;
import com.tersesystems.logback.bytebuddy.InfoLoggingInterceptor;
import com.tersesystems.logback.bytebuddy.ThreadLocalLogger;
import net.bytebuddy.ByteBuddy;
import static net.bytebuddy.agent.builder.AgentBuilder.*;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.StringMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * Use ByteBuddy to add logging to classes that don't have it.
 */
public class ClassWithByteBuddy {

    // This is a class we're going to wrap entry and exit methods around.
    public static class SomeLibraryClass {
        public void doesNotUseLogging() {
            System.out.println("Logging sucks, I use println");
        }
    }

    // We can do this by intercepting the class and putting stuff around it.
    static class Interception {
        // Do it through wrapping
        public SomeLibraryClass instrumentClass() throws IllegalAccessException, InstantiationException {
            Class<SomeLibraryClass> offendingClass = SomeLibraryClass.class;
            String offendingMethodName = "doesNotUseLogging";

            return new ByteBuddy()
                    .subclass(offendingClass)
                    .method(ElementMatchers.named(offendingMethodName))
                    .intercept(MethodDelegation.to(new InfoLoggingInterceptor()))
                    .make()
                    .load(offendingClass.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                    .getLoaded()
                    .newInstance();
        }

        public void doStuff() throws IllegalAccessException, InstantiationException {
            SomeLibraryClass someLibraryClass = this.instrumentClass();
            someLibraryClass.doesNotUseLogging();
        }
    }

    // This is a class we're going to redefine completely.
    public static class SomeOtherLibraryClass {
        public void doesNotUseLogging() {
            System.out.println("I agree, I don't use logging either");
        }
    }

    static class AgentBased {
        public static void premain() {
            try {
                String className = "SomeOtherLibraryClass";
                String methodName = "doesNotUseLogging";

                // The debugging listener shows what classes are being picked up by the instrumentation
                Listener.Filtering debuggingListener = new Listener.Filtering(
                        new StringMatcher(className, StringMatcher.Mode.CONTAINS),
                        Listener.StreamWriting.toSystemOut());

                // This gives a bit of a speedup when going through classes...
                RawMatcher ignoreMatcher = new RawMatcher.ForElementMatchers(ElementMatchers.nameStartsWith("net.bytebuddy.").or(isSynthetic()), any(), any());

                // Create and install the byte buddy remapper
                new AgentBuilder.Default()
                        .with(new AgentBuilder.InitializationStrategy.SelfInjection.Eager())
                        .ignore(ignoreMatcher)
                        .with(debuggingListener)
                        .type(ElementMatchers.nameContains(className))
                        .transform((builder, type, classLoader, module) ->
                                builder.visit(Advice.to(ClassAdviceRewriter.class).on(named(methodName)))
                        )
                        .installOnByteBuddyAgent();
            } catch (RuntimeException e) {
                System.out.println("Exception instrumenting code : " + e);
                e.printStackTrace();
            }
        };

        public void doStuff() {
            // No code change necessary here, you can wrap completely in the agent...
            SomeOtherLibraryClass someOtherLibraryClass = new SomeOtherLibraryClass();
            someOtherLibraryClass.doesNotUseLogging();
        }
    }

    public static void main(String[] args) throws Exception {
        // Helps if you install the byte buddy agents before anything else at all happens...
        ByteBuddyAgent.install();
        AgentBased.premain();

        Logger logger = LoggerFactory.getLogger(ClassWithByteBuddy.class);
        ThreadLocalLogger.setLogger(logger);

        new Interception().doStuff();
        new AgentBased().doStuff();
    }
}
```

Provides (with the bytebuddy instrumentation debug output included):

```text
[Byte Buddy] DISCOVERY example.ClassWithByteBuddy$SomeOtherLibraryClass [sun.misc.Launcher$AppClassLoader@18b4aac2, null, loaded=false]
[Byte Buddy] TRANSFORM example.ClassWithByteBuddy$SomeOtherLibraryClass [sun.misc.Launcher$AppClassLoader@18b4aac2, null, loaded=false]
[Byte Buddy] COMPLETE example.ClassWithByteBuddy$SomeOtherLibraryClass [sun.misc.Launcher$AppClassLoader@18b4aac2, null, loaded=false]
[INFO ] e.ClassWithByteBuddy - entering: example.ClassWithByteBuddy$SomeLibraryClass.doesNotUseLogging()
Logging sucks, I use println
[INFO ] e.ClassWithByteBuddy - exit: example.ClassWithByteBuddy$SomeLibraryClass.doesNotUseLogging(), response = null
[INFO ] e.ClassWithByteBuddy - entering: example.ClassWithByteBuddy$SomeOtherLibraryClass.doesNotUseLogging()
I agree, I don't use logging either
[INFO ] e.ClassWithByteBuddy - exiting: example.ClassWithByteBuddy$SomeOtherLibraryClass.doesNotUseLogging()
```

## Dependency Injection with Guice

Finally, if you're using a DI framework like Guice, you can leverage some of the contextual code in [Sangria](https://tavianator.com/announcing-sangria/) to do some of the gruntwork for you.  For example, here's how you configure a `Logger` instance in Guice:

```java
package example;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.spi.InjectionPoint;
import com.tavianator.sangria.contextual.ContextSensitiveBinder;
import com.tavianator.sangria.contextual.ContextSensitiveProvider;
import com.tersesystems.logback.context.logstash.LogstashContext;
import com.tersesystems.logback.context.logstash.LogstashLoggerFactory;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

public class GuiceAssistedLogging {

    public static class MyClass {
        private final Logger logger;

        @Inject
        MyClass(Logger logger) {
            this.logger = logger;
        }

        public void doStuff() {
            logger.info("hello world!");
        }
    }

    @Singleton
    static class Slf4jLoggerProvider implements ContextSensitiveProvider<Logger> {
        private final ILoggerFactory loggerFactory;

        @Inject
        Slf4jLoggerProvider(ILoggerFactory loggerFactory) {
            this.loggerFactory = loggerFactory;
        }

        @Override
        public Logger getInContext(InjectionPoint injectionPoint) {
            return loggerFactory.getLogger(injectionPoint.getDeclaringType().getRawType().getName());
        }

        @Override
        public Logger getInUnknownContext() {
            return loggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        }
    }

    static class ILoggerFactoryProvider implements Provider<ILoggerFactory> {
        @Override
        public ILoggerFactory get() {
            // This would be hooked up to @RequestScoped in a real application
            LogstashContext context = LogstashContext.create("threadName", Thread.currentThread().getName());
            return LogstashLoggerFactory.create().withContext(context);
        }
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                install(new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(ILoggerFactory.class).toProvider(ILoggerFactoryProvider.class);
                        ContextSensitiveBinder.create(binder())
                                .bind(Logger.class)
                                .toContextSensitiveProvider(Slf4jLoggerProvider.class);
                    }
                });
            }
        });

        MyClass instance = injector.getInstance(MyClass.class);
        // Assume this is running in an HTTP request that is @RequestScoped
        instance.doStuff();
    }

}
```

which yields:

```json
{"@timestamp":"2019-01-27T00:19:08.628+00:00","@version":"1","message":"hello world!","logger_name":"example.GuiceAssistedLogging$MyClass","thread_name":"main","level":"INFO","level_value":20000,"threadName":"main"}
```

If you are using a Servlet based API, then you can piggyback of Guice's [servlet extensions](https://github.com/google/guice/wiki/Servlets) and then integrate the logging context as part of the [`CDI / JSR 299 / @RequestScoped`](https://docs.oracle.com/javaee/6/tutorial/doc/gjbbk.html).  I have not tried this myself.

## Logback Specific Things

This section deals with the specific configuration in `terse-logback/classic`.

Logback doesn't come with a default `logback.xml` file, and the [configuration page](https://logback.qos.ch/manual/configuration.html#auto_configuration) is written at a very low level that is not very useful for people.  The example has been written so that it doesn't "overwhelm" with too much detail, but in rough order of initialization:

* Logback XML with Custom Actions
* Loading Typesafe Config
* Log Levels and Properties through Typesafe Config
* High Performance Async Appenders
* Sensible Joran (Logback XML) Configuration

### Logback XML with Custom Actions

The entry point of the system is a `logback.xml` file which has custom actions added to it to do additional configuration, `TypesafeConfigAction` and `SetLoggerLevelsAction`.

This approach is not as fancy as using a service loader pattern, but there are issues integrating into web frameworks, as those frameworks may look directly for XML files and skip service loader patterns.  Using a `logback.xml` file is the most well known pattern, and Joran makes adding custom actions fairly easy.

### Loading Typesafe Config

The `TypesafeConfigAction` will search in a variety of places for configuration using [standard fallback behavior](https://github.com/lightbend/config#standard-behavior) for Typesafe Config, which gives a richer experience to end users.

```java
Config config = systemProperties        // Look for a property from system properties first...
        .withFallback(file)          // if we don't find it, then look in an explicitly defined file...
        .withFallback(testResources) // if not, then if logback-test.conf exists, look for it there...
        .withFallback(resources)     // then look in logback.conf...
        .withFallback(reference)     // and then finally in logback-reference.conf.
        .resolve();                  // Tell config that we want to use ${?ENV_VAR} type stuff.
```

The configuration is then placed in the `LoggerContext` which is available to all of Logback.

```java
lc.putObject(ConfigConstants.TYPESAFE_CONFIG_CTX_KEY, config);
```

And then all properties are made available to Logback:

```java
for (Map.Entry<String, ConfigValue> propertyEntry : properties) {
    String key = propertyEntry.getKey();
    String value = propertyEntry.getValue().unwrapped().toString();
    lc.putProperty(key, value);
}
```

Technically, I think that it's possible to use an [`ImplicitAction`](https://logback.qos.ch/manual/onJoran.html#implicit) to fallback to resolving to the `Config` if no property is found, but that would require instantiating a `Configurator` which again doesn't work so well with frameworks.

### Log Levels and Properties through Typesafe Config

Configuration of properties and setting log levels is done through [Typesafe Config](https://github.com/lightbend/config#overview).  

Here's the `logback.conf` from the example application.  It's in Human-Optimized Config Object Notation or [HOCON](https://github.com/lightbend/config/blob/master/HOCON.md).

You can also censor text at both the message and at the JSON level.  Censoring information from messages is part of a defense in depth strategy, and should not be relied on.

```hocon
# Set logger levels here.
levels = {
    # Override the default root log level with ROOT_LOG_LEVEL environment variable, if defined...
    ROOT = ${?ROOT_LOG_LEVEL}

    # You can set a logger with a simple package name.
    example = DEBUG

    # You can also do nested overrides here.
    deeply.nested {
        package = TRACE
    }
}

censor {
    regex += """hunter2""" // http://bash.org/?244321
    replacement = "*******"
    json.keys += "password" // adding password key will remove the key/value pair entirely
}

# Overrides the properties from logback-reference.conf
properties {
    # Overwrite text file on every run.
    textfile {
        append = false
    }

    # Override the color code in console for info statements
    highlight {
        info = "black"
    }
}

# You can also include settings from other places
include "myothersettings"
```

For tests, there's a `logback-test.conf` that will override (rather than completely replace) any settings that you have in `logback.conf`:

```hocon
levels {
  example = TRACE
}

properties {
  textfile {
    location = "log/test/application-test.log"
    append = false
  }

  jsonfile {
    location = "log/test/application-test.json"
    prettyprint = true
  }
}
```

There is also a `logback-reference.conf` file that handles the default configuration for the appenders, and those settings can be overridden.  They are written out individually in the encoder configuration so I won't go over it here.


Note that appender logic is not available here.  If you need to update the appenders, you should release a new version of the classic library and get your projects updated.

Using Typesafe Config is not a requirement -- the point here is to show that there are more options to configuring Logback than using a straight XML file.

### High Performance Async Appenders

The JSON and Text file appenders are wrapped in [LMAX Disruptor async appenders](https://github.com/logstash/logstash-logback-encoder#async-appenders).  

This example comes preconfigured with a [shutdown hook](https://logback.qos.ch/manual/configuration.html#stopContext) to ensure the async appenders empty their queues before the application shuts down.

To my knowledge, the logstash async appenders have not been benchmarked against Log4J2, but async logging is ridiculously performant, and [will never be the bottleneck in your application](https://www.sitepoint.com/which-java-logging-framework-has-the-best-performance/#conclusions).  

In general, you should only be concerned about the latency or throughput of your logging framework when you have sat down and done the math on how much logging it would take to stress out the system, asked about your operational requirements, and determined the operational costs, including IO and [rate limits](https://segment.com/blog/bob-loblaws-log-blog/#the-case-of-the-missing-logs), and a budget for logging.  Logging doesn't come for free.

### Sensible Joran (Logback XML) Configuration

The [XML configuration](https://logback.qos.ch/manual/configuration.html#syntax) for the main file is in `terse-logback.xml` and is as follows:

```xml
<configuration>
    <newRule pattern="*/typesafeConfig"
             actionClass="com.tersesystems.logback.TypesafeConfigAction"/>

    <newRule pattern="*/setLoggerLevels"
             actionClass="com.tersesystems.logback.SetLoggerLevelsAction"/>

    <typesafeConfig />

    <jmxConfigurator />

    <conversionRule conversionWord="terseHighlight" converterClass="com.tersesystems.logback.TerseHighlightConverter" />

    <conversionRule conversionWord="censor" converterClass="com.tersesystems.logback.censor.CensoringMessageConverter" />

    <turboFilter class="ch.qos.logback.classic.turbo.MarkerFilter">
        <Name>TRACER_FILTER</Name>
        <Marker>TRACER</Marker>
        <OnMatch>ACCEPT</OnMatch>
    </turboFilter>

    <!-- give the async appenders time to shutdown -->
    <shutdownHook class="ch.qos.logback.core.hook.DelayingShutdownHook">
        <delay>${shutdownHook.delay}</delay>
    </shutdownHook>

    <include resource="terse-logback/appenders/console-appenders.xml"/>
    <include resource="terse-logback/appenders/jsonfile-appenders.xml"/>
    <include resource="terse-logback/appenders/textfile-appenders.xml"/>

    <root>
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="ASYNCJSONFILE"/>
        <appender-ref ref="ASYNCTEXTFILE"/>
    </root>

    <!-- Set the logger levels at the very end -->
    <setLoggerLevels/>
</configuration>
```

All the encoders have been configured to use UTC as the timezone, and are packaged individually using [file inclusion](https://logback.qos.ch/manual/configuration.html#fileInclusion) for ease of use.

#### Console

The console appender uses the following XML configuration:

```xml
<included>

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${console.encoder.pattern}</pattern>
        </encoder>
        <withJansi>${console.withJansi}</withJansi>
    </appender>

</included>
```

with the HOCON settings as follows:

```hocon
console {
  withJansi = true # allow colored logging on windows
  encoder {
    pattern = "[%terseHighlight(%-5level)] %logger{15} - %censoredMessage%n%xException{10}"
  }
}
```

The console appender uses colored logging for the log level, but you can override config to set the colors you want for which levels.  Jansi is included so that Windows can benefit from colored logging as well.

The console does not use async logging, because it has to co-exist with `System.out.println` and `System.err.println` messages, and so must appear time-ordered with them.

#### Text

The text encoder uses the following configuration:

```xml
<included>
    <appender name="TEXTFILE" class="ch.qos.logback.core.FileAppender">
        <file>${textfile.location}</file>
        <append>${textfile.append}</append>

        <!--
          This quadruples logging throughput (in theory) https://logback.qos.ch/manual/appenders.html#FileAppender
         -->
        <immediateFlush>${textfile.immediateFlush}</immediateFlush>

        <encoder>
            <pattern>${textfile.encoder.pattern}</pattern>
            <outputPatternAsHeader>${textfile.encoder.outputPatternAsHeader}</outputPatternAsHeader>
        </encoder>
    </appender>

    <!--
      https://github.com/logstash/logstash-logback-encoder/tree/logstash-logback-encoder-5.2#async-appenders
    -->
    <appender name="ASYNCTEXTFILE" class="net.logstash.logback.appender.LoggingEventAsyncDisruptorAppender">
        <appender-ref ref="TEXTFILE" />
    </appender>>
</included>
```

with the HOCON settings as:

```hocon
textfile {
  location = log/application.log
  append = true
  immediateFlush = true

  rollingPolicy {
    fileNamePattern = "log/application.log.%d{yyyy-MM-dd}"
    maxHistory = 30
  }

  encoder {
    outputPatternAsHeader = true
    pattern = "%date{yyyy-MM-dd'T'HH:mm:ss.SSSZZ,UTC} [%-5level] %logger in %thread - %censor(%msg)%n%xException"
  }
}
```

Colored logging is not used in the file-based appender, because some editors tend to show ANSI codes specifically.

#### JSON

The JSON encoder uses [`net.logstash.logback.encoder.LogstashEncoder`](https://github.com/logstash/logstash-logback-encoder/tree/logstash-logback-encoder-5.2#encoders--layouts) with pretty print options.  

The XML is as follows:

```xml
<included>

    <appender name="JSONFILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${jsonfile.location}</file>
        <append>${jsonfile.append}</append>

        <!--
          This quadruples logging throughput (in theory) https://logback.qos.ch/manual/appenders.html#FileAppender
         -->
        <immediateFlush>${jsonfile.immediateFlush}</immediateFlush>

        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${jsonfile.rollingPolicy.fileNamePattern}</fileNamePattern>
            <maxHistory>${jsonfile.rollingPolicy.maxHistory}</maxHistory>
        </rollingPolicy>

        <!--
          Take out the \ because you cannot have - and - next to each other:
          https://github.com/logstash/logstash-logback-encoder/tree/logstash-logback-encoder-5.2#encoders-\-layouts
        -->
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <!-- don't include the properties from typesafe config -->
            <includeContext>${jsonfile.encoder.includeContext}</includeContext>
            <!-- UTC is the best server consistent timezone -->
            <timeZone>${jsonfile.encoder.timeZone}</timeZone>
            
            <!-- https://github.com/logstash/logstash-logback-encoder/tree/logstash-logback-encoder-5.2#customizing-json-factory-and-generator -->
            <if predicate='p("jsonfile.prettyprint").contains("true")'>
                <then>
                    <!-- Pretty print for better end user experience. -->
                    <jsonGeneratorDecorator class="com.tersesystems.logback.censor.CensoringPrettyPrintingJsonGeneratorDecorator"/>
                </then>
                <else>
                    <jsonGeneratorDecorator class="com.tersesystems.logback.censor.CensoringJsonGeneratorDecorator"/>
                </else>
            </if>
        </encoder>
    </appender>

    <!--
      https://github.com/logstash/logstash-logback-encoder/tree/logstash-logback-encoder-5.2#async-appenders
    -->
    <appender name="ASYNCJSONFILE" class="net.logstash.logback.appender.LoggingEventAsyncDisruptorAppender">
        <appender-ref ref="JSONFILE" />
    </appender>>
</included>
```

with the following HOCON configuration:

```hocon
jsonfile {
  location = "log/application.json"
  append = true
  immediateFlush = true
  prettyprint = false

  rollingPolicy {
    fileNamePattern = "log/application.json.%d{yyyy-MM-dd}"
    maxHistory = 30
  }

  encoder {
    includeContext = false
    timeZone = "UTC"
  }
}
```

If you want to modify the format of the JSON encoder, you should use [`LoggingEventCompositeJsonEncoder`](https://github.com/logstash/logstash-logback-encoder/tree/logstash-logback-encoder-5.2#composite-encoderlayout).  The level of detail in `LoggingEventCompositeJsonEncoder` is truly astounding and it's a powerful piece of work in its own right.

## Further Reading

### APIs

SLF4J is essentially the assembly language of Java logging at this point, so if you want to use something else it had better wrap or interoperate with SLF4J.

There are various wrappers and APIs on top of SLF4J:

* [Godaddy Logger](https://github.com/godaddy/godaddy-logger)
* [LogMachine](https://github.com/UnquietCode/LogMachine)
* [structlog4j](https://github.com/jacek99/structlog4j)
* [slf4j-fluent](https://github.com/ffissore/slf4j-fluent)

I have not used these personally.  I usually roll my own code when I need something on top of SLF4J, because the wrappers tend to set their own encoders on top.

### Logback Encoders and Appenders

There's a useful blog post on [writing your own appender](https://logz.io/blog/lessons-learned-writing-new-logback-appender/) for [logzio](https://github.com/logzio/logzio-logback-appender).

There are also additional encoders and console appenders in [concurrent-build-logger](https://github.com/takari/concurrent-build-logger).
  
### Other Blog Posts

* [Logging Best Practices](https://www.loomsystems.com/blog/single-post/2017/01/26/9-logging-best-practices-based-on-hands-on-experience)
* [OWASP Logging Cheat Sheet](https://www.owasp.org/index.php/Logging_Cheat_Sheet)
* [Woofer: logging in (best) practices](https://orange-opensource.github.io/woofer/logging-code/): Spring Boot 
* [A whole product concern logging implementation](http://stevetarver.github.io/2016/04/20/whole-product-logging.html)
* [Level up logs and ELK - Logging best practices with Logback ](https://looking4q.blogspot.com/2018/09/level-up-logs-and-elk-logging-best.html)
* [Extending logstash-logback-encoder](https://zenidas.wordpress.com/recipes/extending-logstash-logback-encoder/)
* [There is more to logging than meets the eye](https://allegro.tech/2015/10/there-is-more-to-logging-than-meets-the-eye.html)

## Release

I can never remember how to release projects, so I'm using [Kordamp Gradle Plugins](https://aalmiray.github.io/kordamp-gradle-plugins/) to do most of the work.  I've added some properties to deal with signing artifacts with gpg2 and a Yubikey 4 and staging on Bintray.

To publish to Maven Local repository, use the [builtin](https://docs.gradle.org/current/userguide/publishing_maven.html#publishing_maven:install):

```bash
./gradlew publishToMavenLocal
```

To stage on Bintray:

```bash
HISTCONTROL=ignoreboth ./gradlew clean bintrayUpload -Pversion=x.x.x -Psigning.gnupg.passphrase=123456 --info
```

If it [all goes south](https://dzone.com/articles/why-i-never-use-maven-release) then it may be time to move to [something else](https://axelfontaine.com/blog/dead-burried.html) rather than `maven-release-plugin`.
