<!---freshmark shields
output = [
	link(shield('Bintray', 'bintray', 'tersesystems:terse-logback', 'blue'), 'https://search.maven.org/search?q=g:com.tersesystems.terse-logback'),
	link(shield('Latest version', 'latest', '{{previousVersion}}', 'blue'), 'https://github.com/tersesystems/terse-logback/releases/latest'),
	link(shield('License CC0', 'license', 'CC0', 'blue'), 'https://tldrlegal.com/license/creative-commons-cc0-1.0-universal'),
	'',
	link(shield('Release Notes', 'release-notes', '{{previousVersion}}', 'brightgreen'), 'docs/release-notes.md'),
	link(image('Travis CI', 'https://travis-ci.org/tersesystems/terse-logback.svg?branch=master'), 'https://travis-ci.org/tersesystems/terse-logback')
	].join('\n')
-->
[![Maven Central](https://img.shields.io/badge/maven-central-tersesystems%3Aterse--logback-blue.svg)](https://search.maven.org/search?q=g:com.tersesystems.terse-logback)
[![Latest version](https://img.shields.io/badge/latest-0.16.2-blue.svg)](https://github.com/tersesystems/terse-logback/releases/latest)
[![License CC0](https://img.shields.io/badge/license-CC0-blue.svg)](https://tldrlegal.com/license/creative-commons-cc0-1.0-universal)

[![Release Notes](https://img.shields.io/badge/release--notes-0.16.2-brightgreen.svg)](docs/release-notes.md)
[![Travis CI](https://travis-ci.org/tersesystems/terse-logback.svg?branch=master)](https://travis-ci.org/tersesystems/terse-logback)
<!---freshmark /shields -->

# Terse Logback

Terse Logback is a collection of [Logback](https://logback.qos.ch/) extensions that shows how to use [Logback](https://logback.qos.ch/manual/index.html) effectively for [structured logging](structured-logging.md), [ringbuffer logging](guide/ringbuffer.md), [system instrumentation](guide/instrumentation.md), and [JDBC](guide/jdbc.md).  

Using Terse Logback increases the observability of your application.  Or as [@mipsytipsy](https://twitter.com/mipsytipsy) puts it:

<blockquote class="twitter-tweet"><p lang="en" dir="ltr">HOLY ROYAL SHITBALLS. To all of you who have been asking (for YEARS) if there are any alternatives to honeycomb for observability: yes, finally YES! <a href="https://twitter.com/will_sargent?ref_src=twsrc%5Etfw">@will_sargent</a> has hacked together the most ingenious little solution using only logs and feature flags:<a href="https://t.co/xwdHWMlcEl">https://t.co/xwdHWMlcEl</a></p>&mdash; Charity Majors (@mipsytipsy) <a href="https://twitter.com/mipsytipsy/status/1153889935536975872?ref_src=twsrc%5Etfw">July 24, 2019</a></blockquote> 

## Documentation

Documentation is available at [https://tersesystems.github.io/terse-logback](https://tersesystems.github.io/terse-logback).

## Showcase

There is a showcase project at [https://github.com/tersesystems/terse-logback-showcase](https://github.com/tersesystems/terse-logback-showcase).
