# JUL to SLF4J Bridge

It's easy to assume that all Java libraries will depend on SLF4J.  But one of the oddities of Java logging is that there's a built-in logging framework called `java.util.logging` (JUL) which is rarely used but does appear in libraries such as [Guice](https://groups.google.com/g/google-guice/c/J2M64gM6Yao), [GRPC](https://github.com/grpc/grpc-java/issues/1577), and [Guava](https://github.com/google/guava/issues/829).

When errors happen in these frameworks, they may never show up in logging at all, because JUL will write out to standard output and standard error by default.

`SLF4JBridgeHandler` is a logging bridge, which is available in [jul-to-slf4j](http://www.slf4j.org/legacy.html#jul-to-slf4j).  It does the job, but it does require some custom code to be added on startup to tell JUL that the handler is SLF4J:

```java
SLF4JBridgeHandler.removeHandlersForRootLogger();
SLF4JBridgeHandler.install();
```

This isn't ideal, as it's very easy to miss that you have to add these lines of code.  Some frameworks such as [Play Framework]() are [smart enough](https://github.com/playframework/playframework/blob/master/core/play-logback/src/main/scala/play/api/libs/logback/LogbackLoggerConfigurator.scala#L86) are smart enough to handle this for you, but there are cases where you're not using those frameworks, and we'd like JUL to just work.

This isn't so easy.  JUL is very basic, and accepts configuration from system properties.  The LogManager has two system properties:

- java.util.logging.config.class
- java.util.logging.config.file

If it doesn't find either, then it looks in `${java.home}/conf/logging.properties` if you're on JDK 11.  There's no way to configure it from classpath, you have to do that [by hand](https://mkyong.com/logging/how-to-load-logging-properties-for-java-util-logging/).

There is discussion on [Stack Overflow](https://stackoverflow.com/a/11245040/5266) and the [SLF4J mailing list](https://www.mail-archive.com/slf4j-dev@qos.ch/msg00738.html) suggesting that JUL looks for `logging.properties` in the classpath.  This is incorrect -- the only way you'll see `logging.properties` is from setting `java.util.logging.config.file` or if you're overwriting `${java.home}/logging.properties`.   Here's the [source code](https://github.com/AdoptOpenJDK/openjdk-jdk/blob/master/src/java.logging/share/classes/java/util/logging/LogManager.java#L1347) so you can check for yourself.

However, since we're using Logback, we can leverage the fact that Logback searches through the classpath for `logback.xml`.  All we need is a custom action to wrap `SLF4JBridgeHandler` and we can have a code free solution.  This is what `SLF4JBridgeHandlerAction` does.  You should also configure the `LevelChangePropagator`, to [reduce the impact of logging](http://logback.qos.ch/manual/configuration.html#LevelChangePropagator), and you must make sure that the `LoggerFactory` is called before any JUL dependent code.

You should set your `logback.xml` roughly as follows:

```xml
<configuration>

    <!-- set up the rule -->
    <newRule pattern="configuration/slf4jBridgeHandler"
             actionClass="com.tersesystems.logback.classic.SLF4JBridgeHandlerAction"/>

    <!-- calls removeHandlersForRootLogger / install -->
    <slf4jBridgeHandler/>

    <!-- reset all previous level configurations of all j.u.l. loggers -->
    <contextListener class="ch.qos.logback.classic.jul.LevelChangePropagator">
        <resetJUL>true</resetJUL>
    </contextListener>

    <!-- Add Guice tracing -->
    <logger name="com.google.inject" level="TRACE"/>

    <root level="INFO">
        <appender class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>%date{H:mm:ss.SSS} [%highlight(%-5level)] %logger -  %message%ex%n</pattern>
            </encoder>
        </appender>
    </root>
</configuration>
```

As of 0.17.0, `logback-classic` has a dependency on `jul-to-slf4j` so the following will work in `build.gradle`:

```groovy
dependencies {
  implementation "com.google.inject:guice:5.0.1"
  implementation 'com.tersesystems.logback:logback-classic:0.17.0'
}
```

And then you should call `org.slf4j.LoggerFactory.getLogger` as a static final to prevent any initialization problems:

```java
package example;

import com.google.inject.*;
import org.slf4j.*;

public class App {
    // Ensure that logback.xml is parsed by LoggerFactory _before_ Guice calls JUL.
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(App.class);

    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        final Injector injector = Guice.createInjector();
        final App instance = injector.getInstance(App.class);
        logger.info(instance.getGreeting());
    }
}
```

And that should render the following:

```
19:51:45.493 [DEBUG] com.google.inject.internal.util.ContinuousStopwatch -  Module execution: 64ms
19:51:45.494 [DEBUG] com.google.inject.internal.util.ContinuousStopwatch -  Interceptors creation: 2ms
19:51:45.496 [DEBUG] com.google.inject.internal.util.ContinuousStopwatch -  TypeListeners & ProvisionListener creation: 1ms
19:51:45.511 [DEBUG] com.google.inject.internal.util.ContinuousStopwatch -  Scopes creation: 15ms
19:51:45.511 [DEBUG] com.google.inject.internal.util.ContinuousStopwatch -  Converters creation: 0ms
19:51:45.514 [DEBUG] com.google.inject.internal.util.ContinuousStopwatch -  Binding creation: 2ms
19:51:45.515 [DEBUG] com.google.inject.internal.util.ContinuousStopwatch -  Module annotated method scanners creation: 0ms
19:51:45.515 [DEBUG] com.google.inject.internal.util.ContinuousStopwatch -  Private environment creation: 0ms
19:51:45.515 [DEBUG] com.google.inject.internal.util.ContinuousStopwatch -  Injector construction: 0ms
19:51:45.515 [DEBUG] com.google.inject.internal.util.ContinuousStopwatch -  Binding initialization: 0ms
19:51:45.515 [DEBUG] com.google.inject.internal.util.ContinuousStopwatch -  Binding indexing: 0ms
19:51:45.515 [DEBUG] com.google.inject.internal.util.ContinuousStopwatch -  Collecting injection requests: 0ms
19:51:45.515 [DEBUG] com.google.inject.internal.util.ContinuousStopwatch -  Binding validation: 0ms
19:51:45.515 [DEBUG] com.google.inject.internal.util.ContinuousStopwatch -  Static validation: 0ms
19:51:45.515 [DEBUG] com.google.inject.internal.util.ContinuousStopwatch -  Instance member validation: 0ms
19:51:45.516 [DEBUG] com.google.inject.internal.util.ContinuousStopwatch -  Provider verification: 0ms
19:51:45.516 [DEBUG] com.google.inject.internal.util.ContinuousStopwatch -  Delayed Binding initialization: 0ms
19:51:45.516 [DEBUG] com.google.inject.internal.util.ContinuousStopwatch -  Static member injection: 0ms
19:51:45.516 [DEBUG] com.google.inject.internal.util.ContinuousStopwatch -  Instance injection: 0ms
19:51:45.516 [DEBUG] com.google.inject.internal.util.ContinuousStopwatch -  Preloading singletons: 0ms
19:51:45.545 [INFO ] example.App -  Hello World!
```