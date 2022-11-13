# Audio

The audio appender uses a system beep configured through `SystemPlayer` to notify on warnings and errors, and limits excessive beeps with a budget evaluator.

## Installation

Add the library dependency using [https://mvnrepository.com/artifact/com.tersesystems.logback/logback-audio](https://mvnrepository.com/artifact/com.tersesystems.logback/logback-audio).

## Usage

The XML is as follows:

```xml
<included>

    <appender name="AUDIO-WARN" class="com.tersesystems.logback.audio.AudioAppender">
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>WARN</level>
            <onMatch>NEUTRAL</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>

        <player class="com.tersesystems.logback.audio.SystemPlayer"/>
    </appender>

    <appender name="AUDIO-ERROR" class="com.tersesystems.logback.audio.AudioAppender">
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>

        <player class="com.tersesystems.logback.audio.SystemPlayer"/>
    </appender>

    <appender name="AUDIO" class="com.tersesystems.logback.core.CompositeAppender">
        <filter class="ch.qos.logback.core.filter.EvaluatorFilter">
            <evaluator class="com.tersesystems.logback.budget.BudgetEvaluator">
                <budgetRule name="WARN" threshold="1" interval="5" timeUnit="seconds"/>
                <budgetRule name="ERROR" threshold="1" interval="5" timeUnit="seconds"/>
            </evaluator>
            <OnMismatch>DENY</OnMismatch>
            <OnMatch>NEUTRAL</OnMatch>
        </filter>

        <appender-ref ref="AUDIO-WARN"/>
        <appender-ref ref="AUDIO-ERROR"/>
    </appender>

</included>
```

See [Application Logging in Java: Appenders](
https://tersesystems.com/blog/2019/05/27/application-logging-in-java-part-5/) for more details.
