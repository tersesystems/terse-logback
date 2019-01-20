# Structured Logging Example with Logback

This is a Java project that shows how to use Logback effectively for structured logging.

There isn't too much code here, but it should show how you configure Logback, and how you can reduce the amount of complexity in your end projects.

This is not intended to be a drop in replacement or a straight library dependency.  You will want to modify this to your own tastes.

## What is Structured Logging?

It's logging in JSON.  Technically, you could be logging in XML, but everyone uses JSON.  It's been around for [a while](https://www.kartar.net/2015/12/structured-logging/).

Logging JSON means that you can add more context to logs and do more with them without having to do regexes.  As [Honeycomb](https://honeycomb.io) [describes it](
https://www.honeycomb.io/blog/you-could-have-invented-structured-logging/):

> Structured logging is really all about giving yourself — and your team — a logging API to help you provide consistent context in events. An unstructured logger accepts strings. A structured logger accepts a map, hash, or dictionary that describes all the attributes you can think of for an event.

Structured logs are [different from events](https://www.honeycomb.io/blog/how-are-structured-logs-different-from-events/).  All events can be represented as structured logs, but not all structured logs are events.  Many logs are only portions of events.  An event is a conceptual abstraction and a structured log is one possible representation of that abstraction.

## Adding Context 

There is a question of what you want to add when you log.  This is a matter of taste, but in general you should log so that you [create a consistent narrative](https://www.honeycomb.io/blog/event-foo-constructing-a-coherent-narrative/).  As previously mentioned, a log may indicate a portion of an event, so you want to log where doing so would help tell a story of what happened afterwards.

There are some things you should [always add to an event](https://www.honeycomb.io/blog/event-foo-what-should-i-add-to-an-event/), such as who is talking to your service, what they're asking, business relevant fields, additional context around your service / environment, response time and particulars. You should add units to your field names when you measure a quantity, i.e. `response_time_ms`, and add a "human readable" version of internal information if available.

You should add [context to your logs](https://www.honeycomb.io/blog/event-foo-moar-context-better-events/) that helps differentiate it from its peers, so you never have to guess where the source of a log is coming from.

Adding a [correlation id](https://blog.rapid7.com/2016/12/23/the-value-of-correlation-ids/) helps you [design for results](https://www.honeycomb.io/blog/event-foo-designing-for-results/).  You don't need to use a UUID: a flake id like [FauxFlake](https://github.com/rholder/fauxflake) will probably be better.

So, we know what structured logging is now.  What does it look like in SLF4J?

## Adding Structure to Logging

SLF4J doesn't have specific support for structured logging, but [logstash-logback-encoder]() does.

These are handled through [`net.logstash.logback.argument.StructuredArguments`](https://github.com/logstash/logstash-logback-encoder/blob/logstash-logback-encoder-5.2/src/main/java/net/logstash/logback/argument/StructuredArguments.java), which handles [name / value pairs](https://github.com/logstash/logstash-logback-encoder/tree/logstash-logback-encoder-5.2#event-specific-custom-fields)

`StructuredArguments` write out both to the text appenders and to the JSON appenders.  There is extra "key information" added to the JSON.

```java
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
```

This produces the following output in text:

```text
[INFO] e.ClassWithStructuredArguments - id is 12345
[INFO] e.ClassWithStructuredArguments - id is correlationId=12345
[INFO] e.ClassWithStructuredArguments - id is correlationId=[12345]
```

and in JSON:

```json
{
  "@timestamp" : "2019-01-20T03:08:14.008+00:00",
  "@version" : "1",
  "message" : "id is 12345",
  "logger_name" : "example.ClassWithStructuredArguments",
  "thread_name" : "main",
  "level" : "INFO",
  "level_value" : 20000,
  "correlationId" : "12345"
}
{
  "@timestamp" : "2019-01-20T03:08:14.010+00:00",
  "@version" : "1",
  "message" : "id is correlationId=12345",
  "logger_name" : "example.ClassWithStructuredArguments",
  "thread_name" : "main",
  "level" : "INFO",
  "level_value" : 20000,
  "correlationId" : "12345"
}
{
  "@timestamp" : "2019-01-20T03:08:14.011+00:00",
  "@version" : "1",
  "message" : "id is correlationId=[12345]",
  "logger_name" : "example.ClassWithStructuredArguments",
  "thread_name" : "main",
  "level" : "INFO",
  "level_value" : 20000,
  "correlationId" : "12345"
}
```

If you want to add more context and don't want it to show up in the text logs, you can use `net.logstash.logback.marker.LogstashMarker` instead:

```java

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
```

This produces the following text:

```text
[INFO] e.ClassWithMarkers - id is whatever
```

and the following JSON:

```json
{
  "@timestamp" : "2019-01-20T03:07:48.500+00:00",
  "@version" : "1",
  "message" : "id is whatever",
  "logger_name" : "example.ClassWithMarkers",
  "thread_name" : "main",
  "level" : "INFO",
  "level_value" : 20000,
  "foo" : "bar",
  "correlationId" : "12345"
}
```

## Avoid MDC

Avoid [Mapped Diagnostic Context](https://logback.qos.ch/manual/mdc.html).  MDC is a well known way of adding context to logging, but there are several things that make it problematic.

MDC does not deal well with multi-threaded applications which may pass execution between several threads.  Code that uses `CompletableFuture` and `ExecutorService` may not work reliably with MDC.  A child thread does not automatically inherit a copy of the mapped diagnostic context of its parent.  There are numerous workarounds, but it's safer and easier to use an explicit context as a field or parameter.

MDC breaks silently.  When MDC assumptions are violated, there is no indication that the wrong contextual information is being displayed.

MDC also is a flat map of keys and values, which does not allow for richer context that may be nested.

## Logback Specific Things

This section deals with the specific configuration in `terse-logback/classic`.

Logback doesn't come with a default `logback.xml` file, and the configuration page is written at a very low level that is not very useful for people.  The example has been written so that it doesn't "overwhelm" with too much detail, but in rough order of initialization:

* Custom Service Loader
* Setting Log Levels through JMX
* Log Levels and Properties through Typesafe Config
* High Performance Async Appenders
* Sensible Console, Text and JSON Encoders

### Service Loader

The entry point of the system is the `TerseLogbackConfigurator`, which is set up through the `META-INF/services` service loader pattern.
  
TerseLogbackConfigurator sets up the Logback MXBean, determines what XML file to load for configuration, loads the Typesafe Config options and makes them available to Logback's `LoggingContext`.

### Setting Log Levels through JMX

The [JMX Configurator](https://logback.qos.ch/manual/jmxConfig.html) lets you change a logger's level, but makes you type out the level.  The `LogbackMXBean` will let you change a logger's level without having to enter the level specifically.  

You can use [VisualVM](https://visualvm.github.io/) or another JMX client to connect to the MBean Console.

### Log Levels and Properties through Typesafe Config

Configuration of properties and setting log levels is done through [Typesafe Config](https://github.com/lightbend/config#overview).  

Here's the `logback.conf` from the example application.  It's in Human-Optimized Config Object Notation or [HOCON](https://github.com/lightbend/config/blob/master/HOCON.md).

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

# Overrides the properties from logback-reference.conf
properties {
    textfile {
        append = false
    }
}

# You can also include settings from other places
include "myothersettings"
```

There is a `logback-reference.conf` file that handles the default configuration for the appenders, and those settings can be overridden.

Note that appender logic is not available here.  If you need to update the appenders, you should release a new version of the classic library and get your projects updated.

Using Typesafe Config is not a requirement -- the point here is to show that there are more options to configuring Logback than using a straight XML file.

### High Performance Async Appenders

The JSON and Text file appenders are wrapped in [LMAX Disruptor async appenders](https://github.com/logstash/logstash-logback-encoder#async-appenders).  

This example comes preconfigured with a [shutdown hook](https://logback.qos.ch/manual/configuration.html#stopContext) to ensure the async appenders empty their queues before the application shuts down.

To my knowledge, the logstash async appenders have not been benchmarked against Log4J2, but in general async logging is **ridiculously good enough**, and [will never be the bottleneck in your application](https://www.sitepoint.com/which-java-logging-framework-has-the-best-performance/#conclusions).  

> You should not factor in "fast enough" into your logging framework until you have sat down and done the math on how much logging it would take to stress out the system, and then you should go ask your ops team about the operational costs of keeping all those logs.

### Sensible Console, Text and JSON Encoders

All the encoders have been configured to use UTC as the timezone, and are packaged individually using [file inclusion](https://logback.qos.ch/manual/configuration.html#fileInclusion) for ease of use.

The console appender uses colored logging for the log level, just to demonstrate how you can create your own custom conversion rules.  Jansi is included so that Windows can benefit from colored logging as well.  It uses `"%coloredLevel %logger{15} - %message%n%xException{10}"` as the pattern.

The text encoder uses `"%date{yyyy-MM-dd'T'HH:mm:ss.SSSZZ,UTC} [%-5level] %logger in %thread - %message%n%xException"` as the pattern. Colored logging is not used in the file-based appender, because some editors tend to show ANSI codes specifically.

The JSON encoder uses [`net.logstash.logback.encoder.LogstashEncoder`](https://github.com/logstash/logstash-logback-encoder/tree/logstash-logback-encoder-5.2#encoders--layouts) with no modifications.  If you want to modify the format of the JSON encoder, you should use [`LoggingEventCompositeJsonEncoder`](https://github.com/logstash/logstash-logback-encoder/tree/logstash-logback-encoder-5.2#composite-encoderlayout).  The level of detail in `LoggingEventCompositeJsonEncoder` is truly astounding and it's a powerful piece of work in its own right.


