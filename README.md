# Structured Logging Example with Logback

This is a Java project that shows how to use [Logback](https://logback.qos.ch/manual/index.html) effectively for structured logging.  It should show how you configure Logback, and how you can reduce the amount of complexity in your end projects by packaging your logging appenders and configurators in a distinct project.

## Project Setup

The project is configured into two modules, `classic` and `example`.  The `classic` module contains all the logback code and the appenders, and is intended to be deployed as a small helper library for your other projects, managed through Maven and an artifact manager, or just by packaging the JAR.  The `example` project depends on `classic`, and contains the "end user" experience where log levels are adjusted and JSON can be pretty printed or not.

Notably, the `example` project cannot touch the appenders directly, and has no control over the format of the JSON appender -- console and text patterns can be overridden for developer convenience.  By enforcing a [separation of concerns](https://en.wikipedia.org/wiki/Separation_of_concerns) between **logger configuration** and **logging levels**, it is easy and simple to manage appenders in one place, e.g. going from file appenders to TCP appenders, adding filters for sensitive information, or collapsing repeated log information.

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

If you don't want to pass through anything at all, and instead use a proxy logger, you can use `com.tersesystems.logback.ProxyContextLogger`, which applies it under the hood.  Adding state to the logger is one of those useful tricks that can make life easier, as long as you implement `org.slf4j.Logger` and don't expose your logger to the world.

```java
import com.tersesystems.logback.ProxyContextLogger;
import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class ClassWithMarkers {
    private final Logger logger = getLogger(getClass());

    public void doThings(String correlationId) {
        LogstashMarker context = Markers.append("correlationId", correlationId);
        logger.info(context, "log with marker explicitly");
    }

    public void doThingsWithContext(String correlationId) {
        LogstashMarker context = Markers.append("correlationId", correlationId);
        Logger contextLogger = new ProxyContextLogger(context, logger);

        contextLogger.info("log with marker provided by the underlying proxy"); // no context param
    }

    public static void main(String[] args) {
        String correlationId = IdGenerator.getInstance().generateCorrelationId();
        ClassWithMarkers classWithMarkers = new ClassWithMarkers();
        classWithMarkers.doThings(correlationId);
    }
}
```

This produces the following text:

```text
2019-01-20T23:26:50.351+0000 [INFO ] example.ClassWithMarkers in main - log with marker explicitly
2019-01-20T23:26:50.353+0000 [INFO ] example.ClassWithMarkers in main - log with marker provided by the underlying proxy
```

and the following JSON:

```json
{"@timestamp":"2019-01-20T23:26:50.351+00:00","@version":"1","message":"log with marker explicitly","logger_name":"example.ClassWithMarkers","thread_name":"main","level":"INFO","level_value":20000,"correlationId":"FXtylIy0T878gCNIdfWAAA"}
{"@timestamp":"2019-01-20T23:26:50.353+00:00","@version":"1","message":"log with marker provided by the underlying proxy","logger_name":"example.ClassWithMarkers","thread_name":"main","level":"INFO","level_value":20000,"correlationId":"FXtylIy0T878gCNIdfWAAA"}
```

## Tracer Bullet Logging

Using a `ProxyContextLogger` also allows you the option to do "tracer bullet" logging, where some extra context, such as a query parameter in an HTTP request, could cause a logger to log at a lower level than it would normally do to a special marker.  You can use this for debugging on the fly without changing logger levels, or use it for random sampling of some number of operations.

Defining the following turbo filter in `logback.xml`:

```xml
<turboFilter class="ch.qos.logback.classic.turbo.MarkerFilter">
  <Name>TRACER_FILTER</Name>
  <Marker>TRACER</Marker>
  <OnMatch>ACCEPT</OnMatch>
</turboFilter>
```

and adding it to an existing marker and wrapping it in a `ProxyContextLogger`, you can get:

```java
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
```

which gives the following output:

```text
2019-01-26T18:40:39.088+0000 [TRACE] example.ClassWithTracer in main - This log message is only shown if the request has trace in the query string!
```

```json
{"@timestamp":"2019-01-26T18:40:39.088+00:00","@version":"1","message":"This log message is only shown if the request has trace in the query string!","logger_name":"example.ClassWithTracer","thread_name":"main","level":"TRACE","level_value":5000,"tags":["TRACER"],"correlationId":"FX1UlmU3VfqlX0qxArsAAA"}
```

## Avoid MDC

Avoid [Mapped Diagnostic Context](https://logback.qos.ch/manual/mdc.html).  MDC is a well known way of adding context to logging, but there are several things that make it problematic.  

MDC does not deal well with multi-threaded applications which may pass execution between several threads.  Code that uses `CompletableFuture` and `ExecutorService` may not work reliably with MDC.  A child thread does not automatically inherit a copy of the mapped diagnostic context of its parent.  MDC also breaks silently: when MDC assumptions are violated, there is no indication that the wrong contextual information is being displayed.

There are numerous workarounds, but it's safer and easier to use an explicit context as a field or parameter.

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

    <conversionRule conversionWord="censoredMessage" converterClass="com.tersesystems.logback.censor.CensoringMessageConverter" />

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
        <appender-ref ref="ASYNCCONSOLE"/>
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

    <!--
      https://github.com/logstash/logstash-logback-encoder/tree/logstash-logback-encoder-5.2#async-appenders
    -->
    <appender name="ASYNCCONSOLE" class="net.logstash.logback.appender.LoggingEventAsyncDisruptorAppender">
        <appender-ref ref="CONSOLE" />
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
    pattern = "%date{yyyy-MM-dd'T'HH:mm:ss.SSSZZ,UTC} [%-5level] %logger in %thread - %censoredMessage%n%xException"
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
            <if condition='p("jsonfile.prettyprint").contains("true")'>
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

I have not used these personally, and I usually roll my own when I need something on top of SLF4J, because the wrappers tend to set their own encoders on top.

### Logback Encoders and Appenders

There's a useful blog post on [writing your own appender](https://logz.io/blog/lessons-learned-writing-new-logback-appender/) for [logzio](https://github.com/logzio/logzio-logback-appender).

There are also additional encoders and console appenders in [concurrent-build-logger](https://github.com/takari/concurrent-build-logger).

### Best Practices

* [Logging Best Practices](https://www.loomsystems.com/blog/single-post/2017/01/26/9-logging-best-practices-based-on-hands-on-experience)
* [Logging Cheat Sheet](https://www.owasp.org/index.php/Logging_Cheat_Sheet)
