# Terse Logback

This is a Java project that shows how to use [Logback](https://logback.qos.ch/manual/index.html) effectively for structured logging.  

It also serves as a demonstration of what you can do with Logback and logging in general, including some features that may not be obvious at first glance.

It shows how you configure Logback, and how you can reduce the amount of complexity in your end projects by packaging your logging appenders and configurators in a distinct project.

I've written about the reasoning and internal architecture in a series of blog posts.  The [full list](https://tersesystems.com/category/logging/) is available on [https://tersesystems.com](https://tersesystems.com).

## Quickstart

You want to start up a project immediately and figure things out?  Okay then.

The project is configured into several modules.  The most relevant one to start with is [`logback-structured-config`](https://github.com/tersesystems/terse-logback/tree/master/logback-structured-config/src/main/resources) which shows a finished project put together.  

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

That should give you a fairly verbose logging setup and allow you to change the configuration.  See the [reference section](https://github.com/tersesystems/terse-logback#logback-xml-with-custom-actions) for more details.


You may also want to look at https://github.com/wsargent/sbt-with-jdk-13-docker-logging-example which leverages [sbt-native-packager](https://www.scala-sbt.org/sbt-native-packager/index.html) to provide different logging behavior.
