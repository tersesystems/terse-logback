
## Project Setup

The project is configured into several modules: `censor`, `ext`, `classic`, `example`, and `guice-example`.  The most relevant ones to start with are `classic` and `example`.

The `classic` module contains all the logback code and the appenders, and is intended to be deployed as a small helper library for your other projects, managed through Maven and an artifact manager, or just by packaging the JAR.  The `example` project depends on `classic`, and contains the "end user" experience where log levels are adjusted and JSON can be pretty printed or not.

Notably, the `example` project cannot touch the appenders directly, and has no control over the format of the JSON appender -- console and text patterns can be overridden for developer convenience.  By enforcing a [separation of concerns](https://en.wikipedia.org/wiki/Separation_of_concerns) between **logger configuration** and **logging levels**, it is easy and simple to manage appenders in one place, e.g. going from file appenders to TCP appenders, adding filters for sensitive information, or collapsing repeated log information.

The `guice-example` shows a logback factory that is exposed through a `Provider` in Guice.

This is not intended to be a drop in replacement or a straight library dependency.  You will want to modify this to your own tastes.