# Structured Logging Example with Logback

This is a Java project that shows how to use [Logback](https://logback.qos.ch/manual/index.html) effectively for structured logging.

There isn't too much code here, but it should show how you configure Logback, and how you can reduce the amount of complexity in your end projects.

This is not intended to be a drop in replacement or a straight library dependency.  You will want to modify this to your own tastes.

## What is Structured Logging?

It's logging in JSON.  Technically, you could be logging in another structure like XML or JSON, but almost everyone uses JSON.  It's been around for [a while](https://www.kartar.net/2015/12/structured-logging/).

Logging JSON means that you can add more context to logs and do more with them without having to do regexes.  As [Honeycomb](https://honeycomb.io) [describes it](
https://www.honeycomb.io/blog/you-could-have-invented-structured-logging/):

> Structured logging is really all about giving yourself — and your team — a logging API to help you provide consistent context in events. An unstructured logger accepts strings. A structured logger accepts a map, hash, or dictionary that describes all the attributes you can think of for an event.

Structured logs are [different from events](https://www.honeycomb.io/blog/how-are-structured-logs-different-from-events/).  All events can be represented as structured logs, but not all structured logs are events.  Many logs are only portions of events.  An event is a conceptual abstraction and a structured log is one possible representation of that abstraction.

## Adding Context 

There is a question of what you want to add when you log.  This is a matter of taste, but in general you should log so that you [create a consistent narrative](https://www.honeycomb.io/blog/event-foo-constructing-a-coherent-narrative/).  As previously mentioned, a log may indicate a portion of an event, so you want to log where doing so would help tell a story of what happened afterwards.

There are some things you should [always add to an event](https://www.honeycomb.io/blog/event-foo-what-should-i-add-to-an-event/), such as who is talking to your service, what they're asking, business relevant fields, additional context around your service / environment, response time and particulars. You should add units to your field names when you measure a quantity, i.e. `response_time_ms`, and add a "human readable" version of internal information if available.

You should add [context to your logs](https://www.honeycomb.io/blog/event-foo-moar-context-better-events/) that helps differentiate it from its peers, so you never have to guess where the source of a log is coming from.

Adding a [correlation id](https://blog.rapid7.com/2016/12/23/the-value-of-correlation-ids/) helps you [design for results](https://www.honeycomb.io/blog/event-foo-designing-for-results/).  You don't need to use a UUID: a [flake id](https://github.com/boundary/flake) will probably be better for you.  I'm using [idem](https://github.com/mguenther/idem/) here, but most things will work.

So, we know what structured logging is now.  What does it look like in SLF4J?

## Adding Structure to Logging

SLF4J doesn't have specific support for structured logging, but [logstash-logback-encoder](https://github.com/logstash/logstash-logback-encoder/tree/logstash-logback-encoder-5.2#logback-json-encoder) does.  It's complete and comprehensive, but buried in a section called [Event specific custom fields](https://github.com/logstash/logstash-logback-encoder/tree/logstash-logback-encoder-5.2#event-specific-custom-fields).

Event specific custom fields aren implemented in two ways: through [`net.logstash.logback.argument.StructuredArguments`](https://github.com/logstash/logstash-logback-encoder/blob/logstash-logback-encoder-5.2/src/main/java/net/logstash/logback/argument/StructuredArguments.java), which adds structured information through parameters, and [`net.logstash.logback.marker.Markers`](https://github.com/logstash/logstash-logback-encoder/blob/logstash-logback-encoder-5.2/src/main/java/net/logstash/logback/marker/Markers.java), which adds structured information through the `org.slf4j.Marker` API.

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

## Avoid MDC

Avoid [Mapped Diagnostic Context](https://logback.qos.ch/manual/mdc.html).  MDC is a well known way of adding context to logging, but there are several things that make it problematic.  

MDC does not deal well with multi-threaded applications which may pass execution between several threads.  Code that uses `CompletableFuture` and `ExecutorService` may not work reliably with MDC.  A child thread does not automatically inherit a copy of the mapped diagnostic context of its parent.  MDC also breaks silently: when MDC assumptions are violated, there is no indication that the wrong contextual information is being displayed.

There are numerous workarounds, but it's safer and easier to use an explicit context as a field or parameter.

## Logback Specific Things

This section deals with the specific configuration in `terse-logback/classic`.

Logback doesn't come with a default `logback.xml` file, and the [configuration page](https://logback.qos.ch/manual/configuration.html#auto_configuration) is written at a very low level that is not very useful for people.  The example has been written so that it doesn't "overwhelm" with too much detail, but in rough order of initialization:

* Custom Service Loader
* Setting Log Levels through JMX
* Log Levels and Properties through Typesafe Config
* High Performance Async Appenders
* Sensible Joran (Logback XML) Configuration

### Service Loader

The entry point of the system is the `TerseLogbackConfigurator`, which is set up through the `META-INF/services` [service loader pattern](https://docs.oracle.com/javase/tutorial/ext/basics/spi.html).
  
### Setting Log Levels through JMX

`TerseLogbackConfigurator` sets up the Logback MXBean, determines what XML file to load for configuration, loads the Typesafe Config options and makes them available to Logback's `LoggingContext`.

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
    <jmxConfigurator />
    <logbackMXBean />

    <conversionRule conversionWord="coloredLevel" converterClass="com.tersesystems.logback.ColoredLevel" />

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
    pattern = "%coloredLevel %logger{15} - %message%n%xException{10}"
  }
}
```

The console appender uses colored logging for the log level, just to demonstrate how you can create your own custom conversion rules.  Jansi is included so that Windows can benefit from colored logging as well.  

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
    pattern = "%date{yyyy-MM-dd'T'HH:mm:ss.SSSZZ,UTC} [%-5level] %logger in %thread - %message%n%xException"
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

            <!-- Pretty print for better end user experience. -->
            <if condition='p("jsonfile.prettyprint").contains("true")'>
                <then>
                    <!-- https://github.com/logstash/logstash-logback-encoder/tree/logstash-logback-encoder-5.2#customizing-json-factory-and-generator -->
                    <jsonGeneratorDecorator class="net.logstash.logback.decorate.PrettyPrintingJsonGeneratorDecorator"/>
                </then>
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

I have not used these personally, and I tend to roll my own when I need something on top of SLF4J.

### Logback Encoders and Appenders

There's a useful blog post on [writing your own appender](https://logz.io/blog/lessons-learned-writing-new-logback-appender/) for [logzio](https://github.com/logzio/logzio-logback-appender).

There are also additional encoders and console appenders in [concurrent-build-logger](https://github.com/takari/concurrent-build-logger).
