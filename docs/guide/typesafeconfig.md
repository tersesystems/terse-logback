# Typesafe Config

The `TypesafeConfigAction` will search in a variety of places for configuration using [standard fallback behavior](https://github.com/lightbend/config#standard-behavior) for Typesafe Config, which gives a richer experience to end users.

## Installation

Add the library dependency using [com.tersesystems.logback:logback-typesafe-config](https://mvnrepository.com/artifact/com.tersesystems.logback/logback-typesafe-config).

## Usage

The configuration is derived as follows:

```java
Config config = systemProperties        // Look for a property from system properties first...
        .withFallback(file)          // if we don't find it, then look in an explicitly defined file...
        .withFallback(testResources) // if not, then if logback-test.conf exists, look for it there...
        .withFallback(resources)     // then look in logback.conf...
        .withFallback(reference)     // and then finally in logback-reference.conf.
        .resolve();                  // Tell config that we want to use ${?ENV_VAR} type stuff.
```

The configuration is then placed in the `LoggerContext` which is available to all of Logback.

```java
lc.putObject(ConfigConstants.TYPESAFE_CONFIG_CTX_KEY, config);
```

And then all properties are made available to Logback, either at the `local` scope or at the `context` scope.

Properties must be strings, but you can also provide Maps and Lists to the Logback Context, through `context.getObject`.

### Log Levels and Properties through Typesafe Config

Configuration of properties and setting log levels is done through [Typesafe Config](https://github.com/lightbend/config#overview), using `TypesafeConfigAction`

Here's the `logback.conf` from the example application.  It's in Human-Optimized Config Object Notation or [HOCON](https://github.com/lightbend/config/blob/master/HOCON.md).

```hocon
# Set logger levels here.
levels = {
    # Override the default root log level with ROOT_LOG_LEVEL environment variable, if defined...
    ROOT = ${?ROOT_LOG_LEVEL}

    # You can set a logger with a simple package name.
    example = DEBUG

    # You can also do nested overrides here.
    deeply.nested {
        package = TRACE
    }
}

# Overrides the properties from logback-reference.conf
local {

    logback.environment=production

    censor {
        regex = """hunter2""" // http://bash.org/?244321
        replacementText = "*******"
        json.keys += "password" // adding password key will remove the key/value pair entirely
    }

    # Overwrite text file on every run.
    textfile {
        append = false
    }

    # Override the color code in console for info statements
    highlight {
        info = "black"
    }
}

# You can also include settings from other places
include "myothersettings"
```

For tests, there's a `logback-test.conf` that will override (rather than completely replace) any settings that you have in `logback.conf`:

```hocon
include "logback-reference"

levels {
  example = TRACE
}

local {
  logback.environment=test

  textfile {
    location = "log/test/application-test.log"
    append = false
  }

  jsonfile {
    location = "log/test/application-test.json"
    prettyprint = true
  }
}
```

There is also a `logback-reference.conf` file that handles the default configuration for the appenders, and those settings can be overridden.  They are written out individually in the encoder configuration so I won't go over it here.

Note that appender logic is not available here -- it's all defined through the `structured-config` in `logback.xml`.

Using Typesafe Config is not a requirement -- the point here is to show that there are more options to configuring Logback than using a straight XML file.

See [Application Logging in Java: Adding Configuration](https://tersesystems.com/blog/2019/05/05/application-logging-in-java-part-2/) for more details.
