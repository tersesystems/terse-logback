# Unique ID Appenders

```xml
<configuration>

    <conversionRule conversionWord="uniqueId" converterClass="com.tersesystems.logback.uniqueid.UniqueIdConverter" />

    <appender name="DECORATE_WITH_UNIQUEID" class="com.tersesystems.logback.uniqueid.UniqueIdEventAppender">
        <appender class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>%-5relative %-5level %uniqueId %logger{35} - %msg%n</pattern>
            </encoder>
        </appender>
    </appender>

    <root level="TRACE">
        <appender-ref ref="DECORATE_WITH_UNIQUEID"/>
    </root>
</configuration>
```