
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
