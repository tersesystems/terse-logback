# Composite Appender

The composite appender presents a single appender and appends to several appenders.  It is very useful for referring to a list of appenders by a single name.

## Installation

Add the library dependency using [com.tersesystems.logback:logback-core](https://mvnrepository.com/artifact/com.tersesystems.logback/logback-core).

## Usage

```xml
<configuration debug="true">

    <appender name="CONSOLE" class="ch.qos.logback.core.read.ListAppender">
    </appender>

    <appender name="FILE" class="ch.qos.logback.core.read.ListAppender">
    </appender>

    <appender name="CONSOLE_AND_FILE" class="com.tersesystems.logback.core.CompositeAppender">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </appender>

    <root level="TRACE">
        <appender-ref ref="CONSOLE_AND_FILE"/>
    </root>
</configuration>
```

You can leverage nesting to keep your filtering logic under control. For example, you may want to have several things happen when you hit an error in your logs. Appenders will always write when they receive an event, unless they are filtered.

Using nesting, you can declare the filter once, and have the child appenders "inherit" that filter:

<configuration>
    <newRule pattern="*/player"
           actionClass="com.tersesystems.logback.audio.PlayerAction"/>
 
    <!-- Filter is on the appender chain -->
    <appender name="ERROR-APPENDER" class="com.tersesystems.logback.CompositeAppender">
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
 
        <appender class="ch.qos.logback.core.FileAppender">
            <file>error.log</file>
            <encoder>
                <pattern>%date - %message</pattern>
            </encoder>
        </appender>
 
        <appender class="com.tersesystems.logback.audio.AudioAppender">
            <player class="com.tersesystems.logback.audio.ResourcePlayer">
                <resource>/error.ogg</resource>
            </player>
        </appender>
    </appender>
 
    <root level="TRACE">
        <appender-ref ref="ALL-APPENDER"/>
        <appender-ref ref="TRACE-APPENDER"/>
        <appender-ref ref="DEBUG-APPENDER"/>
        <appender-ref ref="INFO-APPENDER"/>
        <appender-ref ref="WARN-APPENDER"/>
        <appender-ref ref="ERROR-APPENDER"/>
    </root>
</configuration>

This makes your appender logic much cleaner.

See [Application Logging in Java: Appenders](https://tersesystems.com/blog/2019/05/27/application-logging-in-java-part-5/) for more details.
