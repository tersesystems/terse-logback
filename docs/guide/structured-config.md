# Structured Config

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

    <appender name="selector-with-unique-id" class="com.tersesystems.logback.uniqueid.UniqueIdComponentAppender">
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

The JSON encoder uses [`net.logstash.logback.encoder.LogstashEncoder`](https://github.com/logstash/logstash-logback-encoder/tree/logstash-logback-encoder-6.3#encoders--layouts) with pretty print options.

The XML is as follows:

```xml
<included>
    <appender name="ASYNC_JSONFILE" class="net.logstash.logback.appender.LoggingEventAsyncDisruptorAppender">
        <filter class="com.tersesystems.logback.core.EnabledFilter">
            <enabled>${jsonfile.enabled}</enabled>
        </filter>
        <appender class="ch.qos.logback.core.rolling.RollingFileAppender">
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

            <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
                <providers>
                    <pattern>
                        <pattern>
                            { "id": "%uniqueId" }
                        </pattern>
                    </pattern>
                    <sequence/>
                    <timestamp>
                        <!-- UTC is the best server consistent timezone -->
                        <timeZone>${jsonfile.encoder.timeZone}</timeZone>
                        <timestampPattern>${jsonfile.encoder.timestampPattern}</timestampPattern>
                    </timestamp>
                    <version/>
                    <message/>
                    <loggerName/>
                    <threadName/>
                    <logLevel/>
                    <stackHash/>
                    <mdc/>
                    <logstashMarkers/>
                    <arguments/>

                    <provider class="com.tersesystems.logback.exceptionmapping.json.ExceptionArgumentsProvider">
                        <fieldName>exception</fieldName>
                    </provider>

                    <stackTrace>
                        <!--
                          https://github.com/logstash/logstash-logback-encoder#customizing-stack-traces
                        -->
                        <throwableConverter class="net.logstash.logback.stacktrace.ShortenedThrowableConverter">
                            <rootCauseFirst>${jsonfile.shortenedThrowableConverter.rootCauseFirst}</rootCauseFirst>
                            <inlineHash>${jsonfile.shortenedThrowableConverter.inlineHash}</inlineHash>
                        </throwableConverter>
                    </stackTrace>
                </providers>

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

See [Application Logging in Java: Putting it all together](https://tersesystems.com/blog/2019/06/23/application-logging-in-java-part-10/) for more details.