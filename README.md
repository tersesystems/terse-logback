[<img src="https://img.shields.io/travis/tersesystems/terse-logback.svg"/>](https://travis-ci.org/tersesystems/terse-logback) 
[ ![Download](https://api.bintray.com/packages/tersesystems/maven/terse-logback/images/download.svg?version=0.1.6) ](https://bintray.com/tersesystems/maven/terse-logback/0.1.6/link)

# Structured Logging Example with Logback

This is a Java project that shows how to use [Logback](https://logback.qos.ch/manual/index.html) effectively for structured logging.  It should show how you configure Logback, and how you can reduce the amount of complexity in your end projects by packaging your logging appenders and configurators in a distinct project.

## Blog Posts

* [Application Logging in Java: Creating a Logging Framework](https://tersesystems.com/blog/2019/04/23/application-logging-in-java-part-1/)
* [Application Logging in Java: Adding Configuration](https://tersesystems.com/blog/2019/05/05/application-logging-in-java-part-2/)
* [Application Logging in Java: Converters](https://tersesystems.com/blog/2019/05/11/application-logging-in-java-part-3/)
* [Application Logging in Java: Markers](https://tersesystems.com/blog/2019/05/18/application-logging-in-java-part-4/)
* [Application Logging in Java: Appenders](https://tersesystems.com/blog/2019/05/27/application-logging-in-java-part-5/)
* [Application Logging in Java: Logging Costs](https://tersesystems.com/blog/2019/06/03/application-logging-in-java-part-6/)
* [Application Logging in Java: Encoders](https://tersesystems.com/blog/2019/06/09/application-logging-in-java-part-7/)
* [Application Logging in Java: Tracing 3rd Party Code](https://tersesystems.com/blog/2019/06/11/application-logging-in-java-part-8/)
* [Application Logging in Java: Filters](https://tersesystems.com/blog/2019/06/15/application-logging-in-java-part-9/)
* [Application Logging in Java: Putting it all together](https://tersesystems.com/blog/2019/06/23/application-logging-in-java-part-10/)

## Project Setup

The project is configured into several modules.  The most relevant ones to start with is `structured-config` and `example`.

The `structured-config` module contains all the logback code and the appenders, and is intended to be deployed as a small helper library for your other projects, managed through Maven and an artifact manager, or just by packaging the JAR.  

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

When you're using structured logging, you'll inevitably have to pass around the `LogstashMarker` or `StructuredArgument` with it so that you can add context to your logging.  In the past, the recommended way to do this was MDC.

Avoid [Mapped Diagnostic Context](https://logback.qos.ch/manual/mdc.html).  MDC is a well known way of adding context to logging, but there are several things that make it problematic.  

MDC does not deal well with multi-threaded applications which may pass execution between several threads.  Code that uses `CompletableFuture` and `ExecutorService` may not work reliably with MDC.  A child thread does not automatically inherit a copy of the mapped diagnostic context of its parent.  MDC also breaks silently: when MDC assumptions are violated, there is no indication that the wrong contextual information is being displayed.

## Instrumenting Logging Code with Byte Buddy

If you have library code that doesn't pass around `ILoggerFactory` and doesn't let you add information to logging, then you can get around this by instrumenting the code with [Byte Buddy](https://bytebuddy.net/).  Using Byte Buddy, you can do fun things like override `Security.setSystemManager` with [your own implementation](https://tersesystems.com/blog/2016/01/19/redefining-java-dot-lang-dot-system/), so using Byte Buddy to decorate code with `enter` and `exit` logging statements is relatively straightforward.

There's two different ways to do it.  You can use interception, which gives you a straightforward method delegation model, or you can use `Advice`, which rewrites the bytecode inline before the JVM gets to it.  Either way, you can write to a logger without touching the class itself, and you can modify which classes and methods you touch.  

I like this approach better than the annotation or aspect-oriented programming approaches, because it is completely transparent to the code and gives the same performance as inline code.  I use a `ThreadLocal` logger here, as it gives me more control over logging capabilities than using MDC would, but there are many options available.

With Interception:

```java
public class InterceptionTest {
       
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
                   .intercept(MethodDelegation.to(new TraceLoggingInterceptor()))
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

   public static void main(String[] args) throws InstantiationException, IllegalAccessException {
       // Helps if you install the byte buddy agents before anything else at all happens...
       ByteBuddyAgent.install();

       Logger logger = LoggerFactory.getLogger(InterceptionTest.class);
       ThreadLocalLogger.setLogger(logger);

       new Interception().doStuff();
   }
}
```

Provides (with the bytebuddy instrumentation debug output included):

```text
438   TRACE c.t.l.bytebuddy.InterceptionTest - entering: com.tersesystems.logback.bytebuddy.InterceptionTest$SomeLibraryClass.doesNotUseLogging()
Logging sucks, I use println
440   TRACE c.t.l.bytebuddy.InterceptionTest - exit: com.tersesystems.logback.bytebuddy.InterceptionTest$SomeLibraryClass.doesNotUseLogging() => response=[null]
```

With Class Advice:

```java
public class AgentBasedTest {

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

                // Create and install the byte buddy remapper
                new AgentBuilder.Default()
                        .disableClassFormatChanges()
                        //.with(debuggingListener)
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

        Logger logger = LoggerFactory.getLogger(AgentBasedTest.class);
        ThreadLocalLogger.setLogger(logger);

        new AgentBased().doStuff();
    }
}
```

produces:

```text
391   TRACE c.t.l.bytebuddy.AgentBasedTest - entering: com.tersesystems.logback.bytebuddy.AgentBasedTest$SomeOtherLibraryClass.doesNotUseLogging() with arguments=[]
I agree, I don't use logging either
395   TRACE c.t.l.bytebuddy.AgentBasedTest - exiting: com.tersesystems.logback.bytebuddy.AgentBasedTest$SomeOtherLibraryClass.doesNotUseLogging() with arguments=[] => returnType=void
```

## Censoring Sensitive Information

There may be sensitive information that you don't want to show up in the logs.  You can get around this by passing your information through a censor.  This is a custom bit of code written for Logback, but it's not too complex.

There are two rules and a converter that are used in Logback to define and reference censors: `CensorAction`, `CensorRefAction` and the `censor` converter.

```xml
<configuration>
    <newRule pattern="*/censor"
             actionClass="com.tersesystems.logback.censor.CensorAction"/>

    <newRule pattern="*/censor-ref"
             actionClass="com.tersesystems.logback.censor.CensorRefAction"/>

    <conversionRule conversionWord="censor" converterClass="com.tersesystems.logback.censor.CensorConverter" />
    
    <!-- ... -->
</configuration>
```

The `CensorAction` defines a censor that can be referred to by the `CensorRef` action and the `censor` conversionWord, using the censor name.  The default implementation is the regex censor, which will look for a regular expression and replace it with the replacement text defined:

```xml
<configuration>
    <censor name="censor-name1" class="com.tersesystems.logback.censor.RegexCensor">
        <replacementText>[CENSORED BY CENSOR1]</replacementText>
        <regex>hunter1</regex>
    </censor>

    <censor name="censor-name2" class="com.tersesystems.logback.censor.RegexCensor">
        <replacementText>[CENSORED BY CENSOR2]</replacementText>
        <regex>hunter2</regex>
    </censor>
</configuration>
```

Once you have the censors defined, you can use the censor word by specifying the target as defined in the [pattern encoder format](https://logback.qos.ch/manual/layouts.html#conversionWord), and adding the name as the option list using curly braces, i.e. `%censor(%msg){censor-name1}`.  If you don't define the censor, then the first available censor will be picked. 

```xml
<configuration>
    <appender name="TEST1" class="ch.qos.logback.core.FileAppender">
        <file>file1.log</file>
        <encoder>
            <pattern>%censor(%msg){censor-name1}%n</pattern>
        </encoder>
    </appender>

    <appender name="TEST2" class="ch.qos.logback.core.FileAppender">
        <file>file2.log</file>    
        <encoder>
            <pattern>%censor(%msg){censor-name2}%n</pattern>
        </encoder>
    </appender>
</configuration>
```

If you are working with a componentized framework, you'll want to use the `censor-ref` action instead.  Here's an example using logstash-logback-encoder.

```xml
<configuration>
    <appender name="TEST3" class="ch.qos.logback.core.FileAppender">
        <file>file3.log</file>
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <jsonGeneratorDecorator class="com.tersesystems.logback.censor.CensoringJsonGeneratorDecorator">
                <censor-ref ref="json-censor"/>
            </jsonGeneratorDecorator>
        </encoder>
    </appender>
</configuration>
```

In this case, `CensoringJsonGeneratorDecorator` implements the `CensorAttachable` interface and so will run message text through the censor if it exists.

## Budget Aware Logging

There are instances where loggers may be overly chatty, and will log more than necessary.  Rather than hunt down all the individual loggers and whitelist or blacklist the lot of them, you may want to assign a budget that will budget INFO messages to 5 statements a second.

This is easy to do with the `logback-budget` module, which uses an internal [circuit breaker](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/concurrent/CircuitBreaker.html) to regulate the flow of messages. 

```xml
<configuration>
    <!-- <statusListener class="ch.qos.logback.core.status.OnConsoleStatusListener" />-->

    <newRule pattern="*/budget-rule"
             actionClass="com.tersesystems.logback.budget.BudgetRuleAction"/>

    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <filter class="ch.qos.logback.core.filter.EvaluatorFilter">
            <evaluator class="com.tersesystems.logback.budget.BudgetEvaluator">
              <budget-rule name="INFO" threshold="5" interval="1" timeUnit="seconds"/>
            </evaluator>
            <OnMismatch>DENY</OnMismatch>
            <OnMatch>NEUTRAL</OnMatch>
        </filter>

        <encoder>
            <pattern>%-5relative %-5level %logger{35} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="TRACE">
        <appender-ref ref="STDOUT"/>
    </root>
    
</configuration>
```

## Logback Specific Things

This section deals with the specific configuration in `terse-logback/logback-structured-config`.

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

And then all properties are made available to Logback, either at the `local` scope or at the `context` scope.  

Properties must be strings, but you can also provide Maps and Lists to the Logback Context, through `context.getObject`.

### Log Levels and Properties through Typesafe Config

Configuration of properties and setting log levels is done through [Typesafe Config](https://github.com/lightbend/config#overview), using `TypesafeConfigAction`

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
local {

    logback.environment=production
  
    censor {
        regex = """hunter2""" // http://bash.org/?244321
        replacementText = "*******"
        json.keys += "password" // adding password key will remove the key/value pair entirely
    }

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
include "logback-reference"

levels {
  example = TRACE
}

local {
  logback.environment=test

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

Note that appender logic is not available here -- it's all defined through the `structured-config` in `logback.xml`.

Using Typesafe Config is not a requirement -- the point here is to show that there are more options to configuring Logback than using a straight XML file.

### High Performance Async Appenders

The JSON and Text file appenders are wrapped in [LMAX Disruptor async appenders](https://github.com/logstash/logstash-logback-encoder#async-appenders).  

This example comes preconfigured with a [shutdown hook](https://logback.qos.ch/manual/configuration.html#stopContext) to ensure the async appenders empty their queues before the application shuts down.

To my knowledge, the logstash async appenders have not been benchmarked against Log4J2, but async logging is ridiculously performant, and [will never be the bottleneck in your application](https://www.sitepoint.com/which-java-logging-framework-has-the-best-performance/#conclusions).  

In general, you should only be concerned about the latency or throughput of your logging framework when you have sat down and done the math on how much logging it would take to stress out the system, asked about your operational requirements, and determined the operational costs, including IO and [rate limits](https://segment.com/blog/bob-loblaws-log-blog/#the-case-of-the-missing-logs), and a budget for logging.  Logging doesn't come for free.

### Sensible Joran (Logback XML) Configuration

The [XML configuration](https://logback.qos.ch/manual/configuration.html#syntax) for the main file is in `terse-logback.xml` and is as follows:

The `UniqueIdEventAppender` is an appender that decorates `ILoggingEvent` with a unique id that can be used to correlate the same log entry across different appenders.

```xml
<configuration>

    <include resource="terse-logback/initial.xml"/>
    <include resource="terse-logback/censor.xml"/>

    <include resource="terse-logback/appenders/audio-appenders.xml"/>
    <include resource="terse-logback/appenders/console-appenders.xml"/>
    <include resource="terse-logback/appenders/jsonfile-appenders.xml"/>
    <include resource="terse-logback/appenders/textfile-appenders.xml"/>

    <appender name="development" class="com.tersesystems.logback.core.CompositeAppender">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="AUDIO"/>
        <appender-ref ref="ASYNC_TEXTFILE"/>
        <appender-ref ref="ASYNC_JSONFILE"/>
    </appender>

    <appender name="test" class="com.tersesystems.logback.core.CompositeAppender">
        <appender-ref ref="ASYNC_TEXTFILE"/>
    </appender>

    <appender name="production" class="com.tersesystems.logback.core.CompositeAppender">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="ASYNC_JSONFILE"/>
    </appender>

    <appender name="selector" class="com.tersesystems.logback.core.SelectAppender">
        <!-- Set logback.conf or logback-test.conf with "local.logback.environment=development" -->
        <appenderKey>${logback.environment}</appenderKey>

        <appender-ref ref="development"/>
        <appender-ref ref="production"/>
        <appender-ref ref="test"/>
    </appender>

    <appender name="selector-with-unique-id" class="com.tersesystems.logback.uniqueid.UniqueIdEventAppender">
        <appender-ref ref="selector"/>
    </appender>

    <root>
        <appender-ref ref="selector-with-unique-id"/>
    </root>

    <include resource="terse-logback/ending.xml" />
</configuration>
```

All the encoders have been configured to use UTC as the timezone, and are packaged individually using [file inclusion](https://logback.qos.ch/manual/configuration.html#fileInclusion) for ease of use.

#### Console

The console appender uses the following XML configuration:

```xml
<included>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <filter class="com.tersesystems.logback.EnabledFilter">
            <enabled>${console.enabled}</enabled>
        </filter>
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
    enabled = true
    withJansi = true # allow colored logging on windows
    encoder {
      pattern = "[%terseHighlight(%-5level)] %logger{15} -  %censor(%message){text-censor}%n%xException{10}"
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
        <filter class="com.tersesystems.logback.EnabledFilter">
            <enabled>${textfile.enabled}</enabled>
        </filter>
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
    </appender>
</included>
```

with the HOCON settings as:

```hocon
  // used in textfile-appenders.xml
  textfile {
    enabled = true
    location = ${properties.log.dir}/application.log
    append = true
    immediateFlush = true

    rollingPolicy {
      fileNamePattern = ${properties.log.dir}"/application.log.%d{yyyy-MM-dd}"
      maxHistory = 30
    }

    encoder {
      outputPatternAsHeader = true

      // https://github.com/logstash/logstash-logback-encoder/blob/master/src/main/java/net/logstash/logback/stacktrace/ShortenedThrowableConverter.java#L58
      // Options can be specified in the pattern in the following order:
      //   - maxDepthPerThrowable = "full" or "short" or an integer value
      //   - shortenedClassNameLength = "full" or "short" or an integer value
      //   - maxLength = "full" or "short" or an integer value
      //
      //%msg%n%stack{5,1024,10,rootFirst,regex1,regex2,evaluatorName}

      pattern = "%date{yyyy-MM-dd'T'HH:mm:ss.SSSZZ,UTC} [%-5level] %logger in %thread - %censor(%message){text-censor}%n%stack{full,full,short,rootFirst}"
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
        <filter class="com.tersesystems.logback.EnabledFilter">
            <enabled>${jsonfile.enabled}</enabled>
        </filter>
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

            <!--
              https://github.com/logstash/logstash-logback-encoder#customizing-stack-traces
            -->
            <throwableConverter class="net.logstash.logback.stacktrace.ShortenedThrowableConverter">
                <maxDepthPerThrowable>${jsonfile.shortenedThrowableConverter.maxDepthPerThrowable}
                </maxDepthPerThrowable>
                <maxLength>${jsonfile.shortenedThrowableConverter.maxLength}</maxLength>
                <shortenedClassNameLength>${jsonfile.shortenedThrowableConverter.shortenedClassNameLength}
                </shortenedClassNameLength>
                <!-- coma separated exclusion patterns -->
                <exclusions>${jsonfile.shortenedThrowableConverter.exclusions}</exclusions>
                <rootCauseFirst>${jsonfile.shortenedThrowableConverter.rootCauseFirst}</rootCauseFirst>
                <inlineHash>${jsonfile.shortenedThrowableConverter.inlineHash}</inlineHash>
            </throwableConverter>

            <!-- https://github.com/logstash/logstash-logback-encoder/tree/logstash-logback-encoder-5.2#customizing-json-factory-and-generator -->
            <!-- XXX it would be much nicer to use OGNL rather than Janino, but out of scope... -->
            <if condition='p("jsonfile.prettyprint").contains("true")'>
                <then>
                    <!-- Pretty print for better end user experience. -->
                    <jsonGeneratorDecorator
                            class="com.tersesystems.logback.censor.CensoringPrettyPrintingJsonGeneratorDecorator">
                        <censor-ref ref="json-censor"/>
                    </jsonGeneratorDecorator>
                </then>
                <else>
                    <jsonGeneratorDecorator class="com.tersesystems.logback.censor.CensoringJsonGeneratorDecorator">
                        <censor-ref ref="json-censor"/>
                    </jsonGeneratorDecorator>
                </else>
            </if>
        </encoder>
    </appender>

    <!--
      https://github.com/logstash/logstash-logback-encoder/tree/logstash-logback-encoder-5.2#async-appenders
    -->
    <appender name="ASYNCJSONFILE" class="net.logstash.logback.appender.LoggingEventAsyncDisruptorAppender">
        <appender-ref ref="JSONFILE"/>
    </appender>
</included>
```

with the following HOCON configuration:

```hocon
  // Used in jsonfile-appenders.xml
  jsonfile {
    enabled = true
    location = ${properties.log.dir}"/application.json"
    append = true
    immediateFlush = true
    prettyprint = false

    rollingPolicy {
      fileNamePattern = ${properties.log.dir}"/application.json.%d{yyyy-MM-dd}"
      maxHistory = 30
    }

    encoder {
      includeContext = false
      timeZone = "UTC"
    }

    # https://github.com/logstash/logstash-logback-encoder#customizing-stack-traces
    shortenedThrowableConverter {
      maxDepthPerThrowable = 100
      maxLength = 100
      shortenedClassNameLength = 50

      exclusions = """\$\$FastClassByCGLIB\$\$,\$\$EnhancerBySpringCGLIB\$\$,^sun\.reflect\..*\.invoke,^com\.sun\.,^sun\.net\.,^net\.sf\.cglib\.proxy\.MethodProxy\.invoke,^org\.springframework\.cglib\.,^org\.springframework\.transaction\.,^org\.springframework\.validation\.,^org\.springframework\.app\.,^org\.springframework\.aop\.,^java\.lang\.reflect\.Method\.invoke,^org\.springframework\.ws\..*\.invoke,^org\.springframework\.ws\.transport\.,^org\.springframework\.ws\.soap\.saaj\.SaajSoapMessage\.,^org\.springframework\.ws\.client\.core\.WebServiceTemplate\.,^org\.springframework\.web\.filter\.,^org\.apache\.tomcat\.,^org\.apache\.catalina\.,^org\.apache\.coyote\.,^java\.util\.concurrent\.ThreadPoolExecutor\.runWorker,^java\.lang\.Thread\.run$"""

      rootCauseFirst = true
      inlineHash = true
    }
  }
```

If you want to modify the format of the JSON encoder, you should use [`LoggingEventCompositeJsonEncoder`](https://github.com/logstash/logstash-logback-encoder/tree/logstash-logback-encoder-5.2#composite-encoderlayout).  The level of detail in `LoggingEventCompositeJsonEncoder` is truly astounding and it's a powerful piece of work in its own right.

## Audio

The audio appender uses a system beep configured through `SystemPlayer` to notify on warnings and errors, and limits excessive beeps with a budget evaluator.

The XML is as follows:

```xml
<included>

    <appender name="AUDIO-WARN" class="com.tersesystems.logback.audio.AudioAppender">
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>WARN</level>
            <onMatch>NEUTRAL</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>

        <player class="com.tersesystems.logback.audio.SystemPlayer"/>
    </appender>

    <appender name="AUDIO-ERROR" class="com.tersesystems.logback.audio.AudioAppender">
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>

        <player class="com.tersesystems.logback.audio.SystemPlayer"/>
    </appender>

    <appender name="AUDIO" class="com.tersesystems.logback.core.CompositeAppender">
        <filter class="ch.qos.logback.core.filter.EvaluatorFilter">
            <evaluator class="com.tersesystems.logback.budget.BudgetEvaluator">
                <budgetRule name="WARN" threshold="1" interval="5" timeUnit="seconds"/>
                <budgetRule name="ERROR" threshold="1" interval="5" timeUnit="seconds"/>
            </evaluator>
            <OnMismatch>DENY</OnMismatch>
            <OnMatch>NEUTRAL</OnMatch>
        </filter>

        <appender-ref ref="AUDIO-WARN"/>
        <appender-ref ref="AUDIO-ERROR"/>
    </appender>

</included>
```

## Further Reading

### APIs

SLF4J is essentially the assembly language of Java logging at this point, so if you want to use something else it had better wrap or interoperate with SLF4J.  This is a huge advantage over the historical [logging mess](https://techblog.bozho.net/the-logging-mess/).

There are various wrappers and APIs on top of SLF4J:

* [Godaddy Logger](https://github.com/godaddy/godaddy-logger)
* [LogMachine](https://github.com/UnquietCode/LogMachine)
* [structlog4j](https://github.com/jacek99/structlog4j)
* [slf4j-fluent](https://github.com/ffissore/slf4j-fluent)
* [slf4j-json-logger](https://github.com/savoirtech/slf4j-json-logger)
* [json-log-domain](https://github.com/skjolber/json-log-domain)
* [logbook](https://github.com/zalando/logbook)
* [logstage](https://izumi.7mind.io/latest/release/doc/logstage/)
* [descriptive-logger](https://github.com/thombergs/descriptive-logger)
* [flogger](https://github.com/google/flogger) has an [SLF4J backend](https://github.com/google/flogger/blob/master/slf4j/src/main/java/com/google/common/flogger/backend/slf4j/Slf4jLoggerBackend.java)

I have not used these personally.  I usually roll my own code when I need something on top of SLF4J, because a) I can and b) the wrappers generally aren't great.  By and large, they tend to conflate concepts they're not interested in, so a hardcoded appender is assumed, or the encoder and layout is merged together, or the like.  In some cases, the API will [explicitly disable SLF4J functionality](https://github.com/google/flogger/blob/master/slf4j/src/main/java/com/google/common/flogger/backend/slf4j/Slf4jLoggerBackend.java#L100) like the OFF level.

### Logback Encoders and Appenders

* [concurrent-build-logger](https://github.com/takari/concurrent-build-logger) (encoders and appenders both)
* [logzio-logback-appender](https://github.com/logzio/logzio-logback-appender)
* [logback-elasticsearch-appender](https://github.com/internetitem/logback-elasticsearch-appender)  
* [logback-more-appenders](https://github.com/sndyuk/logback-more-appenders)
* [logback-steno](https://github.com/ArpNetworking/logback-steno)
* [logslack](https://github.com/gmethvin/logslack)
  
### Other Blog Posts

#### Logback Specific

* [Lessons Learned Writing New Logback Appender](https://logz.io/blog/lessons-learned-writing-new-logback-appender/) 
* [Extending logstash-logback-encoder](https://zenidas.wordpress.com/recipes/extending-logstash-logback-encoder/)

#### Best Practices

Many of these are logback specific, but still good overall.

* [9 Logging Best Practices Based on Hands-on Experience](https://www.loomsystems.com/blog/single-post/2017/01/26/9-logging-best-practices-based-on-hands-on-experience)
* [Woofer: logging in (best) practices](https://orange-opensource.github.io/woofer/logging-code/): Spring Boot 
* [A whole product concern logging implementation](http://stevetarver.github.io/2016/04/20/whole-product-logging.html)
* [There is more to logging than meets the eye](https://allegro.tech/2015/10/there-is-more-to-logging-than-meets-the-eye.html)
* [Monitoring demystified: A guide for logging, tracing, metrics](https://techbeacon.com/enterprise-it/monitoring-demystified-guide-logging-tracing-metrics)

Stack Overflow has a couple of good tips on SLF4J and Logging:

* [When to use the different log levels](https://stackoverflow.com/questions/2031163/when-to-use-the-different-log-levels)
* [Why does the TRACE level exist, and when should I use it rather than DEBUG?](https://softwareengineering.stackexchange.com/questions/279690/why-does-the-trace-level-exist-and-when-should-i-use-it-rather-than-debug)
* [Best practices for using Markers in SLF4J/Logback](https://stackoverflow.com/questions/4165558/best-practices-for-using-markers-in-slf4j-logback)
* [Stackoverflow: Logging best practices in multi-node environment](https://stackoverflow.com/questions/43496695/java-logging-best-practices-in-multi-node-environment)

#### Level Up Logs

[Alberto Navarro](https://looking4q.blogspot.com/) has a great series

<ol>
<li><a href="http://looking4q.blogspot.com/2018/09/level-up-logs-and-elk-introduction.html">Introduction</a> (Everyone)</li>
<li><a href="http://looking4q.blogspot.com/2018/09/level-up-your-logs-and-elk-json-logs.html">JSON as logs format</a> (Everyone)</li>
<li><b><a href="http://looking4q.blogspot.com/2018/09/level-up-logs-and-elk-logging-best.html">Logging best practices with Logback</a> (Targetting Java DEVs)</b></li>
<li><a href="https://looking4q.blogspot.com/2018/11/logging-cutting-edge-practices.html">Logging cutting-edge practices</a> (Targetting Java DEVs)&nbsp;</li>
<li><a href="https://looking4q.blogspot.com/2019/01/level-up-logs-and-elk-contract-first.html">Contract first log generator</a> (Targetting Java DEVs) </li>
<li><a href="http://looking4q.blogspot.com/2018/09/level-up-logs-and-elk-elasticsearch.html">ElasticSearch VRR Estimation Strategy</a> (Targetting OPS)</li>
<li><a href="http://looking4q.blogspot.com/2018/09/level-up-logs-and-elk-vrr-java-logback.html">VRR Java + Logback configuration</a> (Targetting OPS)</li>
<li><a href="http://looking4q.blogspot.com/2018/09/level-up-logs-and-elk-vrr-filebeat.html">VRR FileBeat configuration</a> (Targetting OPS)</li>
<li><a href="http://looking4q.blogspot.com/2018/09/level-up-logs-and-elk-vrr-logstash.html">VRR Logstash configuration and Index templates</a> (Targetting OPS)</li>
<li><a href="http://looking4q.blogspot.com/2018/09/level-up-logs-and-elk-vrr-curator.html">VRR Curator configuration</a> (Targetting OPS)</li>
<li><a href="https://looking4q.blogspot.com/2018/10/level-up-logs-and-elk-logstash-grok.html">Logstash Grok, JSON Filter and JSON Input performance comparison</a> (Targetting OPS) </li>
</ol>

#### Logging Anti Patterns

Logging Anti-Patterns by [Rolf Engelhard](https://rolf-engelhard.de/):

* [Logging Anti-Patterns](http://rolf-engelhard.de/2013/03/logging-anti-patterns-part-i/)
* [Logging Anti-Patterns, Part II](http://rolf-engelhard.de/2013/04/logging-anti-patterns-part-ii/)
* [Logging Anti-Patterns, Part III](https://rolf-engelhard.de/2013/10/logging-anti-patterns-part-iii/)

#### Clean Code, clean logs

[Tomasz Nurkiewicz](https://www.nurkiewicz.com/) has a great series on logging: 

* [Clean code, clean logs: use appropriate tools (1/10)](https://www.nurkiewicz.com/2010/05/clean-code-clean-logs-use-appropriate.html)
* [Clean code, clean logs: logging levels are there for you (2/10)](https://www.nurkiewicz.com/2010/05/clean-code-clean-logs-tune-your-pattern.html)
* [Clean code, clean logs: do you know what you are logging? (3/10)](https://www.nurkiewicz.com/2010/05/clean-code-clean-logs-do-you-know-what.html)
* [Clean code, clean logs: avoid side effects (4/10)](https://www.nurkiewicz.com/2010/05/clean-code-clean-logs-avoid-side.html)
* [Clean code, clean logs: concise and descriptive (5/10)](https://www.nurkiewicz.com/2010/05/clean-code-clean-logs-concise-and.html)
* [Clean code, clean logs: tune your pattern (6/10)](https://www.nurkiewicz.com/2010/05/clean-code-clean-logs-tune-your-pattern.html)
* [Clean code, clean logs: log method arguments and return values (7/10)](https://www.nurkiewicz.com/2010/05/clean-code-clean-logs-log-method.html)
* [Clean code, clean logs: watch out for external systems (8/10)](https://www.nurkiewicz.com/2010/05/clean-code-clean-logs-watch-out-for.html)
* [Clean code, clean logs: log exceptions properly (9/10)](https://www.nurkiewicz.com/2010/05/clean-code-clean-logs-log-exceptions.html)
* [Clean code, clean logs: easy to read, easy to parse (10/10)](https://www.nurkiewicz.com/2010/05/clean-code-clean-logs-easy-to-read-easy.html)
* [Condensed 10 Tips on javacodegeeks](https://www.javacodegeeks.com/2011/01/10-tips-proper-application-logging.html)

## Release

I can never remember how to release projects, so I'm using [Kordamp Gradle Plugins](https://aalmiray.github.io/kordamp-gradle-plugins/) to do most of the work.  I've added some properties to deal with signing artifacts with gpg2 and a Yubikey 4 and staging on Bintray.

```bash
./gradlew publishToMavenLocal
```

To make sure everything works:

```bash
./gradlew check
```

License formatting:

```bash
./gradlew LicenseFormat
```

To stage on Bintray:

```bash
HISTCONTROL=ignoreboth ./gradlew clean bintrayUpload -Pversion=0.1.7 -Pbintray.enabled=true -Pbintray.dryRun=true --info
HISTCONTROL=ignoreboth ./gradlew clean bintrayUpload -Pversion=x.x.x -Pbintray.enabled=true --info
```

You will need to set up the bintray credentials before you can even compile anything (sorry about that):

In `~/.gradle/gradle.properties`

```
bintrayUsername=tersesystems

# https://bintray.com/profile/edit (API key is at the bottom)
bintrayApiKey=[CENSORED]
```