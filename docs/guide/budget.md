# Budget Aware Logging

There are instances where logging may be overly chatty, and will log more than necessary.  

Rather than hunt down all the individual loggers and whitelist or blacklist the lot of them, you can assign a 
budget that will budget INFO messages to 5 statements a second.

This is easy to do with the `logback-budget` module, which uses an internal [circuit breaker](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/concurrent/CircuitBreaker.html) to regulate the flow of messages.

## Installation

Add the library dependency using [com.tersesystems.logback:logback-budget](https://mvnrepository.com/artifact/com.tersesystems.logback/logback-budget).

## Usage

The time unit corresponds to the text value of `java.util.concurrent.TimeUnit` i.e. `nanoseconds`, `microseconds`, `milliseconds`, `seconds`, `minutes`, `hours`, `days`, case-insensitive.

```xml
<configuration>

    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <filter class="ch.qos.logback.core.filter.EvaluatorFilter">
            <evaluator class="com.tersesystems.logback.budget.BudgetEvaluator">
                <budgetRule>
                    <name>INFO</name>
                    <threshold>5</threshold>
                    <interval>1</interval>
                    <timeUnit>seconds</timeUnit>
                </budgetRule>
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

## Turbo Filter

You can also apply the budget rule as a turbo filter if you want to have the rule apply across all appenders, using `com.tersesystems.logback.budget.BudgetTurboFilter`.

```xml
<configuration>
    
    <turboFilter class="com.tersesystems.logback.budget.BudgetTurboFilter">
        <budgetRule>
            <name>INFO</name>
            <threshold>5</threshold>
            <interval>1</interval>
            <timeUnit>second</timeUnit>
        </budgetRule>
        <OnMismatch>DENY</OnMismatch>
        <OnMatch>NEUTRAL</OnMatch>
    </turboFilter>
    
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%-5relative %-5level %logger{35} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="TRACE">
        <appender-ref ref="STDOUT"/>
    </root>

</configuration>
```
