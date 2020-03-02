# Exception Mapping

Exception Mapping is done to show the important details of an exception, including the root cause in a summary format.  This is especially useful in line oriented formats, because rendering a stacktrace can take up screen real estate without providing much value.

An example will help.  Given the following program:

```java
public class Thrower {
    private static final Logger logger = LoggerFactory.getLogger(Thrower.class);

    public static void main(String[] progArgs) {
        try {
            doSomethingExceptional();
        } catch (RuntimeException e) {
            logger.error("domain specific message", e);
        }
    }

    static void doSomethingExceptional() {
        Throwable cause = new BatchUpdateException();
        throw new MyCustomException("This is my message", "one is one", "two is more than one", "three is more than two and one", cause);
    }
}

public class MyCustomException extends RuntimeException {
    public MyCustomException(String message, String one, String two, String three, Throwable cause) {
       // ...
    }
    public String getOne() { return one; }
    public String getTwo() { return two; }
    public String getThree() { return three; }
}
```

and the Logback file:

```xml
<configuration>

  <newRule pattern="*/exceptionMappings"
           actionClass="com.tersesystems.logback.exceptionmapping.ExceptionMappingRegistryAction"/>

  <newRule pattern="*/exceptionMappings/mapping"
           actionClass="com.tersesystems.logback.exceptionmapping.ExceptionMappingAction"/>

  <conversionRule conversionWord="richex" converterClass="com.tersesystems.logback.exceptionmapping.ExceptionMessageWithMappingsConverter" />

  <exceptionMappings>
    <!-- comes with default mappings for JDK exceptions, but you can add your own -->
    <mapping name="com.tersesystems.logback.exceptionmapping.MyCustomException" properties="one,two,three"/>
  </exceptionMappings>

  <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <pattern>%-5relative %-5level %logger{35} - %msg%richex{1, 10, exception=[}%n</pattern>
    </encoder>
  </appender>

  <root level="TRACE">
    <appender-ref ref="CONSOLE"/>
  </root>

</configuration>
```

Then this renders the following:

```
184   ERROR c.t.l.exceptionmapping.Thrower - domain specific message exception=[com.tersesystems.logback.exceptionmapping.MyCustomException(one="one is one" two="two is more than one" three="three is more than two and one" message="This is my message") > java.sql.BatchUpdateException(updateCounts="null" errorCode="0" SQLState="null" message="null")]
```

You can integrate exception mapping with Typesafe Config and `logstash-logback-encoder` by adding extra mappings.

For example, you can map a whole bunch of exceptions at once in HOCON, and not have to do it line by line in XML:

```xml
<configuration>
  <newRule pattern="*/exceptionMappings/configMappings"
           actionClass="com.tersesystems.logback.exceptionmapping.config.TypesafeConfigMappingsAction"/>

  <exceptionMappings>
    <!-- Or point to HOCON path -->
    <configMappings path="exceptionmappings"/>
  </exceptionMappings>
</configuration>
```

and

```hocon
exceptionmappings {
   example.MySpecialException: ["timestamp"]
}
```

and configure it in JSON using `ExceptionArgumentsProvider`:

```xml
<encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
  <providers>
    <provider class="com.tersesystems.logback.exceptionmapping.json.ExceptionArgumentsProvider">
      <fieldName>exception</fieldName>
    </provider>
  </providers>
</encoder>
```

and get the following `exception` that contains an array of exceptions and the associated properties, in this case `timestamp`:

```json
{
  "id" : "Fa6x8H0EqomdHaINzdiAAA",
  "sequence" : 3,
  "@timestamp" : "2019-07-06T03:52:48.730+00:00",
  "@version" : "1",
  "message" : "I am an error",
  "logger_name" : "example.Main$Runner",
  "thread_name" : "pool-1-thread-1",
  "level" : "ERROR",
  "stack_hash" : "233f3cf1",
  "exception" : [ {
    "name" : "example.MySpecialException",
    "properties" : {
      "message" : "Level 1",
      "timestamp" : "2019-07-06T03:52:48.728Z"
    }
  }, {
    "name" : "example.MySpecialException",
    "properties" : {
      "message" : "Level 2",
      "timestamp" : "2019-07-06T03:52:48.728Z"
    }
  }, {
    "name" : "example.MySpecialException",
    "properties" : {
      "message" : "Level 3",
      "timestamp" : "2019-07-06T03:52:48.728Z"
    }
  }, {
    "name" : "example.MySpecialException",
    "properties" : {
      "message" : "Level 4",
      "timestamp" : "2019-07-06T03:52:48.728Z"
    }
  }, {
    "name" : "example.MySpecialException",
    "properties" : {
      "message" : "Level 5",
      "timestamp" : "2019-07-06T03:52:48.728Z"
    }
  }, {
    "name" : "example.MySpecialException",
    "properties" : {
      "message" : "Level 6",
      "timestamp" : "2019-07-06T03:52:48.728Z"
    }
  }, {
    "name" : "example.MySpecialException",
    "properties" : {
      "message" : "Level 7",
      "timestamp" : "2019-07-06T03:52:48.728Z"
    }
  }, {
    "name" : "example.MySpecialException",
    "properties" : {
      "message" : "Level 8",
      "timestamp" : "2019-07-06T03:52:48.728Z"
    }
  }, {
    "name" : "example.MySpecialException",
    "properties" : {
      "message" : "Level 9",
      "timestamp" : "2019-07-06T03:52:48.728Z"
    }
  } ],
  "stack_trace" : "<#1165e3b1> example.MySpecialException: Level 9\n\tat example.Main$Runner.nestException(Main.java:56)\n\t... 9 common frames omitted\nWrapped by: <#eb336a2d> example.MySpecialException: Level 8\n\tat example.Main$Runner.nestException(Main.java:56)\n\t... 10 common frames omitted\nWrapped by: <#cc1fb404> example.MySpecialException: Level 7\n\tat example.Main$Runner.nestException(Main.java:56)\n\t... 11 common frames omitted\nWrapped by: <#2af187a0> example.MySpecialException: Level 6\n\tat example.Main$Runner.nestException(Main.java:56)\n\t... 12 common frames omitted\nWrapped by: <#7dac62d1> example.MySpecialException: Level 5\n\tat example.Main$Runner.nestException(Main.java:56)\n\t... 13 common frames omitted\nWrapped by: <#2ea4460d> example.MySpecialException: Level 4\n\tat example.Main$Runner.nestException(Main.java:56)\n\t... 14 common frames omitted\nWrapped by: <#261bed64> example.MySpecialException: Level 3\n\tat example.Main$Runner.nestException(Main.java:56)\n\t... 15 common frames omitted\nWrapped by: <#e660d440> example.MySpecialException: Level 2\n\tat example.Main$Runner.nestException(Main.java:56)\n\t... 16 common frames omitted\nWrapped by: <#233f3cf1> example.MySpecialException: Level 1\n\tat example.Main$Runner.nestException(Main.java:56)\n\tat example.Main$Runner.nestException(Main.java:57)\n\tat example.Main$Runner.nestException(Main.java:57)\n\tat example.Main$Runner.nestException(Main.java:57)\n\tat example.Main$Runner.nestException(Main.java:57)\n\tat example.Main$Runner.nestException(Main.java:57)\n\tat example.Main$Runner.nestException(Main.java:57)\n\tat example.Main$Runner.nestException(Main.java:57)\n\tat example.Main$Runner.nestException(Main.java:57)\n\tat example.Main$Runner.generateException(Main.java:51)\n\tat example.Main$Runner.doError(Main.java:44)\n\tat java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)\n\tat java.util.concurrent.FutureTask.runAndReset(FutureTask.java:308)\n\tat java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.access$301(ScheduledThreadPoolExecutor.java:180)\n\tat java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:294)\n\tat java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)\n\tat java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)\n\tat java.lang.Thread.run(Thread.java:748)\n"
}
```

This is a lot easier for structured logging parsers to grok than the associated stacktrace.

See [How to Log an Exception](https://tersesystems.com/blog/2019/06/29/how-to-log-an-exception/) and [How to Log an Exception, Part 2](https://tersesystems.com/blog/2019/07/06/how-to-log-an-exception-part-2/) for more details.
