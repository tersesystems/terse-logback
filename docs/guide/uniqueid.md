# Unique ID Appenders

The unique id appender allows the logging event to carry a unique id.  When used in conjunction with `SelectAppender` or `CompositeAppender`, this allows for a log record to use the same id across different logs.

For example, in `application.log`, you'll see a single line that starts with `FfwJtsNHYSw6O0Qbm7EAAA`:

```text
FfwJtsNHYSw6O0Qbm7EAAA 2020-03-14T05:30:14.965+0000 [INFO ] play.api.db.HikariCPConnectionPool in play-dev-mode-akka.actor.default-dispatcher-7 - Creating Pool for datasource 'logging'
```

You can search for this string in `application.json` and see more detail on the log record:

```json
{"id":"FfwJtsNHYSw6O0Qbm7EAAA","relative_ns":20921024,"tse_ms":1584163814965,"start_ms":null,"@timestamp":"2020-03-14T05:30:14.965Z","@version":"1","message":"Creating Pool for datasource 'logging'","logger_name":"play.api.db.HikariCPConnectionPool","thread_name":"play-dev-mode-akka.actor.default-dispatcher-7","level":"INFO","level_value":20000}
```

See the [showcase](https://github.com/tersesystems/terse-logback-showcase) for an example.

## Installation

Add the library dependency using [https://mvnrepository.com/artifact/com.tersesystems.logback/logback-uniqueid-appender](https://mvnrepository.com/artifact/com.tersesystems.logback/logback-uniqueid-appender).

## Usage

```xml
<appender name="selector-with-unique-id" class="com.tersesystems.logback.uniqueid.UniqueIdComponentAppender">
  <appender ...>

  </appender>
</appender>
```

To extract the unique ID, register a converter:

```xml
<!-- available as "%uniqueId" in a pattern layout -->
<conversionRule conversionWord="uniqueId" converterClass="com.tersesystems.logback.uniqueid.UniqueIdConverter" />
```

## ID Generators

Unique IDs come with several options.  Flake ID is the default.

### Flake ID

Flake IDs are decentralized and k-ordered, meaning that they are "roughly time-ordered when sorted lexicographically."

This implementation uses [idem](https://github.com/mguenther/idem) with `Flake128S`.

```xml
<appender name="selector-with-unique-id" class="com.tersesystems.logback.uniqueid.UniqueIdComponentAppender">
  <idGenerator class="com.tersesystems.logback.uniqueid.FlakeIdGenerator"/>
  <!-- ... -->
</appender>
```

### Random UUID

Generates a Random UUIDv4 using a ThreadLocalRandom according to <a href="https://github.com/f4b6a3/uuid-creator">https://github.com/f4b6a3/uuid-creator</a>.

```xml
<appender name="selector-with-unique-id" class="com.tersesystems.logback.uniqueid.UniqueIdComponentAppender">
  <idGenerator class="com.tersesystems.logback.uniqueid.RandomUUIDIdGenerator"/>
  <!-- ... -->
</appender>
```

## TSID Generator

Generates a TSID according to <a href="https://github.com/f4b6a3/tsid-creator">https://github.com/f4b6a3/tsid-creator</a>.

**Highly recommended to set a *tsidcreator.node* system property in your application to configure the node id.

```xml
<appender name="selector-with-unique-id" class="com.tersesystems.logback.uniqueid.UniqueIdComponentAppender">
  <idGenerator class="com.tersesystems.logback.uniqueid.TsidIdgenerator"/>
  <!-- ... -->
</appender>
```

## ULID Generator

Creates a monotonic ULID using a threadlocal random according to <a href="https://github.com/f4b6a3/ulid-creator">https://github.com/f4b6a3/ulid-creator</a>.

```xml
<appender name="selector-with-unique-id" class="com.tersesystems.logback.uniqueid.UniqueIdComponentAppender">
  <idGenerator class="com.tersesystems.logback.uniqueid.UlidIdGenerator"/>
  <!-- ... -->
</appender>
```

## KSU ID Generator

Creates a subsecond KSUID according to <a href="https://github.com/f4b6a3/ksuid-creator">https://github.com/f4b6a3/ksuid-creator</a>.

```xml
<appender name="selector-with-unique-id" class="com.tersesystems.logback.uniqueid.UniqueIdComponentAppender">
  <idGenerator class="com.tersesystems.logback.uniqueid.KsuidSubsecondIdGenerator"/>
  <!-- ... -->
</appender>
```

