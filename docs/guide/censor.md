# Censors

There may be sensitive information that you don't want to show up in the logs.  You can get around this by passing your information through a censor.  This is a custom bit of code written for Logback, but it's not too complex.

There are two rules and a converter that are used in Logback to define and reference censors: `CensorAction`, `CensorRefAction` and the `censor` converter.

```xml
<configuration>
    <newRule pattern="*/censor"
             actionClass="com.tersesystems.logback.censor.CensorAction"/>

    <newRule pattern="*/censor-ref"
             actionClass="com.tersesystems.logback.censor.CensorRefAction"/>

    <conversionRule conversionWord="censor" converterClass="com.tersesystems.logback.censor.CensorConverter" />

    <!-- ... -->
</configuration>
```

The `CensorAction` defines a censor that can be referred to by the `CensorRef` action and the `censor` conversionWord, using the censor name.  The default implementation is the regex censor, which will look for a regular expression and replace it with the replacement text defined:

```xml
<configuration>
    <censor name="censor-name1" class="com.tersesystems.logback.censor.RegexCensor">
        <replacementText>[CENSORED BY CENSOR1]</replacementText>
        <regex>hunter1</regex>
    </censor>

    <censor name="censor-name2" class="com.tersesystems.logback.censor.RegexCensor">
        <replacementText>[CENSORED BY CENSOR2]</replacementText>
        <regex>hunter2</regex>
    </censor>
</configuration>
```

Once you have the censors defined, you can use the censor word by specifying the target as defined in the [pattern encoder format](https://logback.qos.ch/manual/layouts.html#conversionWord), and adding the name as the option list using curly braces, i.e. `%censor(%msg){censor-name1}`.  If you don't define the censor, then the first available censor will be picked.

```xml
<configuration>
    <appender name="TEST1" class="ch.qos.logback.core.FileAppender">
        <file>file1.log</file>
        <encoder>
            <pattern>%censor(%msg){censor-name1}%n</pattern>
        </encoder>
    </appender>

    <appender name="TEST2" class="ch.qos.logback.core.FileAppender">
        <file>file2.log</file>
        <encoder>
            <pattern>%censor(%msg){censor-name2}%n</pattern>
        </encoder>
    </appender>
</configuration>
```

If you are working with a componentized framework, you'll want to use the `censor-ref` action instead.  Here's an example using logstash-logback-encoder.

```xml
<configuration>
    <appender name="TEST3" class="ch.qos.logback.core.FileAppender">
        <file>file3.log</file>
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <jsonGeneratorDecorator class="com.tersesystems.logback.censor.CensoringJsonGeneratorDecorator">
                <censor-ref ref="json-censor"/>
            </jsonGeneratorDecorator>
        </encoder>
    </appender>
</configuration>
```

In this case, `CensoringJsonGeneratorDecorator` implements the `CensorAttachable` interface and so will run message text through the censor if it exists.

See [Application Logging in Java: Converters](https://tersesystems.com/blog/2019/05/11/application-logging-in-java-part-3/) for more details.