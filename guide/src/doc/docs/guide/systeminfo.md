
## Exposing System Information with Sigar

In [What is Happening: Attempting to Understand Our Systems](https://www.youtube.com/watch?v=xy3w2hGijhE), there's [a slide](https://speakerdeck.com/lyddonb/what-is-happening-attempting-to-understand-our-systems?slide=133) that suggests the following information should always be available as telemetry data:

> The user (and/or company), time, machine stats (CPU, Memory, etc), software version, configuration data, the calling request, any dependent requests

The interesting bit here is the machine stats, such as CPU and memory, and how they relate to Logback.  Machine status can be very relevant when it comes to resource failures, and providing a detailed view of CPU and memory tied to logs is an interesting concept.

There's a tool, [Hyperic Sigar](https://github.com/hyperic/sigar), which is very good at exposing system metrics. 
 
Using the `logback-sigar` module, it's relatively easy to add Sigar into context using `com.tersesystems.logback.sigar.SigarAction`:

```xml
<configuration>
  <newRule pattern="*/sigar" actionClass="com.tersesystems.logback.sigar.SigarAction"/>

  <sigar/>

  <conversionRule conversionWord="cpu" converterClass="com.tersesystems.logback.sigar.CPUPercentageConverter"/>
  <conversionRule conversionWord="mem" converterClass="com.tersesystems.logback.sigar.MemoryPercentageConverter"/>
  <conversionRule conversionWord="loadavg" converterClass="com.tersesystems.logback.sigar.LoadAverageConverter"/>

  <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <pattern>[%-5level] %logger{15} %cpu %mem %loadavg - %msg%n%xException{10}</pattern>
    </encoder>
  </appender>

  <root level="INFO">
    <appender-ref ref="CONSOLE" />
  </root>
</configuration>
```

And then render the CPU, memory and load average as follows:

```text
[ERROR] c.example.Test sys=0.007594936708860759 user=0.10379746835443038 used=9269886976 used%=24.923867415973078 total=25064484864 load1min=2.18 load5min=1.5 load15min=1.07 - I am very much under load
```

Note that if you want to integrate this with Logstash `StructuredArgument` or `Markers` then you'll want to make your component implement `SigarContextAware` and then query appropriately.  There are some very fun things you can do with Sigar like add [Process Table Query Language](https://shervinasgari.blogspot.com/2011/03/api-helper-wrapper-for-processfinder-in.html) together with some feature flag stuff to do dynamic queries into the machine.
