# Select Appender

Different appenders are useful in different environments.

Development wants:

* Want colorized output on their consoles, with line oriented logs.  
* Would also like to be able to read through logs with debug, info and warnings in them, to track control flow.  If you have the logs seperated, that makes it harder.
* Generally don't want to run a local ELK stack or TCP appenders to see their logs.

Operations wants:

* Really want centralized logging, and a way to drill out on it.  Structured logging especially.
* May want to have everything write to STDOUT, as is case for Docker / 12 Factor Apps.
* May have duplicate logs from the underlying architecture, that need to be dedupped.
* May not want redundant / repeated messages, which developers are not as sensitive to.
* Really hate getting paged with the same error repeatedly.

Logback is not aware of different environments.  There's no out of the box way to say "in this environment I want these sets of appenders, but in this other environment I want these other sets of appenders."

Fortunately, adding this is pretty easy, by leveraging `AppenderAttachable` and pulling a key to select on:

```java
public class SelectAppender extends AppenderBase<ILoggingEvent> implements AppenderAttachable<ILoggingEvent> {
    private AppenderAttachableImpl<ILoggingEvent> aai = new AppenderAttachableImpl<ILoggingEvent>();
    private String appenderKey;

    @Override
    protected void append(ILoggingEvent eventObject) {
        Appender<ILoggingEvent> appender = aai.getAppender(appenderKey);
        if (appender == null) {
            addError("No appender found for appenderKey " + appenderKey);
        } else {
            appender.doAppend(eventObject);
        }
    }

    // ...
}
```

The logback appenders under selection must have the name defined as an element, because Logback only looks for the name attribute at the top level, but otherwise they're the same.  Here, we select the set of appenders we want based on the `LOGBACK_ENVIRONMENT` environment variable.

```xml
<configuration>
    <appender name="SELECT" class="com.tersesystems.logback.SelectAppender">
        <appenderKey>${LOGBACK_ENVIRONMENT}</appenderKey>

        <appender class="com.tersesystems.logback.CompositeAppender">
            <name>test</name>
            <appender class="ch.qos.logback.core.read.ListAppender">
                <name>test-list</name>
            </appender>
        </appender>

        <appender class="com.tersesystems.logback.CompositeAppender">
            <name>development</name>
            <appender class="ch.qos.logback.core.ConsoleAppender">
                <name>development-console</name>
                <encoder>
                    <pattern>%-5relative %-5level %logger{35} - %msg%n</pattern>
                </encoder>
            </appender>
        </appender>

        <appender class="com.tersesystems.logback.CompositeAppender">
            <name>staging</name>
            <appender class="ch.qos.logback.core.ConsoleAppender">
                <name>staging-console</name>
                <encoder>
                    <pattern>%-5relative %-5level %logger{35} - %msg%n</pattern>
                </encoder>
            </appender>

            <appender class="ch.qos.logback.core.FileAppender">
                <name>staging-file</name>
                <file>file.log</file>
                <encoder>
                    <pattern>%-5relative %-5level %logger{35} - %msg%n</pattern>
                </encoder>
            </appender>
        </appender>
    </appender>

    <root level="TRACE">
        <appender-ref ref="SELECT"/>
    </root>

</configuration>
```

This is a much cleaner way to organize appenders than putting Janino logic into the configuration.

