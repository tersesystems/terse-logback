# Unique ID Appenders

Not only can we compose appenders together, but we can also decorate the logging event at the same time. This involves using the decorator pattern to add extra information to data.

So, imagine that we want to extend ILoggingEvent so that we can include a unique id along with it.

```java
public interface IUniqueIdLoggingEvent extends ILoggingEvent {
    String uniqueId();
}
 
 
public class UniqueIdLoggingEvent extends ProxyLoggingEvent implements IUniqueIdLoggingEvent {
    private final String uniqueId;
    UniqueIdLoggingEvent(ILoggingEvent delegate, String uniqueId) {
        super(delegate);
        this.uniqueId = uniqueId;
    }
 
    @Override
    public String uniqueId() {
        return this.uniqueId;
    }
}
```

We can then use it as follows:

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

