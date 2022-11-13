# Budget Aware Logging

There are instances where loggers may be overly chatty, and will log more than necessary.  Rather than hunt down all the individual loggers and whitelist or blacklist the lot of them, you may want to assign a budget that will budget INFO messages to 5 statements a second.

This is easy to do with the `logback-budget` module, which uses an internal [circuit breaker](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/concurrent/CircuitBreaker.html) to regulate the flow of messages.

## Installation

Add the library dependency using [https://mvnrepository.com/artifact/com.tersesystems.logback/logback-budget](https://mvnrepository.com/artifact/com.tersesystems.logback/logback-budget).

## Usage

```xml
<configuration>

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

See [Application Logging in Java: Filters](https://tersesystems.com/blog/2019/06/15/application-logging-in-java-part-9/) for more details.
