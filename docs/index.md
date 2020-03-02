# Terse Logback

Terse Logback is a collection of [Logback](https://logback.qos.ch/) extensions that shows how to use [Logback](https://logback.qos.ch/manual/index.html) effectively for [structured logging](structured-logging.md), [ringbuffer logging](guide/ringbuffer.md), [system instrumentation](guide/instrumentation.md), and [JDBC](guide/jdbc.md).  

Using Terse Logback increases the observability of your application.  Or as [@mipsytipsy](https://twitter.com/mipsytipsy) puts it:

<blockquote class="twitter-tweet"><p lang="en" dir="ltr">HOLY ROYAL SHITBALLS. To all of you who have been asking (for YEARS) if there are any alternatives to honeycomb for observability: yes, finally YES! <a href="https://twitter.com/will_sargent?ref_src=twsrc%5Etfw">@will_sargent</a> has hacked together the most ingenious little solution using only logs and feature flags:<a href="https://t.co/xwdHWMlcEl">https://t.co/xwdHWMlcEl</a></p>&mdash; Charity Majors (@mipsytipsy) <a href="https://twitter.com/mipsytipsy/status/1153889935536975872?ref_src=twsrc%5Etfw">July 24, 2019</a></blockquote> <script async src="https://platform.twitter.com/widgets.js" charset="utf-8"></script> 

I've written about the reasoning and internal architecture in a series of blog posts.  The [full list](https://tersesystems.com/category/logging/) is available on [https://tersesystems.com](https://tersesystems.com).

## Showcase

If you want to see a running application, there is a [showcase web application](https://github.com/tersesystems/terse-logback-showcase) that run out of the box that demonstrates some of the more advanced features, and shows you can integrate terse-logback with [Sentry](https://sentry.io) and [Honeycomb](https://www.honeycomb.io).

## Quickstart

You want to start up a project immediately and figure things out?  Okay then.

The project is configured into several modules.  The most relevant one to start with is [`logback-structured-config`](https://github.com/tersesystems/terse-logback/tree/master/logback-structured-config/src/main/resources) which gives you a starting point.

The `logback-structured-config` module contains all the logback code and the appenders, and is intended to be deployed as a small helper library for your other projects, managed through Maven and an artifact manager, or just by packaging the JAR.

You can see it on [mvnrepository](https://mvnrepository.com/artifact/com.tersesystems.logback/logback-structured-config) but you will need a custom resolver, so better to read through the whole thing.

This is [not intended](https://tersesystems.com/blog/2019/04/23/application-logging-in-java-part-1/) to be a drop in replacement or a straight library dependency.  You will want to modify this to your own tastes.

### Installation

See the [installation](installation.md) guide.

### Configuration

After you've set up the resolvers and library dependencies for your build, you'll add the following to `src/main/resources/logback.xml`:

```xml
<configuration debug="true">
  <include resource="terse-logback/default.xml"/>
</configuration>
```

Then add a `logback.conf` file that contains the following:

```hocon
levels {
  ROOT = DEBUG
}
```

That should give you a fairly verbose logging setup and allow you to change the configuration.  See the [reference section](guide/structured-config.md) for more details.
