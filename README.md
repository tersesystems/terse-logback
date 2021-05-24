<!---freshmark shields
output = [
	 link(shield('Maven central', 'mavencentral', '{{group}}:{{artifactIdMaven}}', 'blue'), 'https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22{{group}}%22%20AND%20a%3A%22{{artifactIdMaven}}%22'),
	link(shield('License CC0', 'license', 'CC0', 'blue'), 'https://tldrlegal.com/license/creative-commons-cc0-1.0-universal'),
	'',
	link(image('Travis CI', 'https://travis-ci.org/tersesystems/terse-logback.svg?branch=master'), 'https://travis-ci.org/tersesystems/terse-logback')
	].join('\n')
-->
[![Maven central](https://img.shields.io/badge/mavencentral-com.tersesystems.logback%3Alogback--core-blue.svg)](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.tersesystems.logback%22%20AND%20a%3A%22logback-core%22)
[![License CC0](https://img.shields.io/badge/license-CC0-blue.svg)](https://tldrlegal.com/license/creative-commons-cc0-1.0-universal)

[![Travis CI](https://travis-ci.org/tersesystems/terse-logback.svg?branch=master)](https://travis-ci.org/tersesystems/terse-logback)
<!---freshmark /shields -->

# Terse Logback

Terse Logback is a collection of [Logback](https://logback.qos.ch/) extensions that shows how to use [Logback](https://logback.qos.ch/manual/index.html) effectively for [structured logging](structured-logging.md), [ringbuffer logging](guide/ringbuffer.md), [system instrumentation](guide/instrumentation.md), and [JDBC](guide/jdbc.md).  

Other logging projects you may be interested in: 

* [Blacklite](https://github.com/tersesystems/blacklite/), an SQLite appender with memory-mapping and zstandard dictionary compression that clocks around 800K statements per second.
* [Blindsight](https://github.com/tersesystems/blindsight), a Scala logging API built around modern logging concepts.

## Documentation

Documentation is available at [https://tersesystems.github.io/terse-logback](https://tersesystems.github.io/terse-logback).

## Showcase

There is a showcase project at [https://github.com/tersesystems/terse-logback-showcase](https://github.com/tersesystems/terse-logback-showcase).
