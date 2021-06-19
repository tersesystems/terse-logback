# Instrumentation

If you have library code that doesn't pass around `ILoggerFactory` and doesn't let you add information to logging, then you can get around this by instrumenting the code with [Byte Buddy](https://bytebuddy.net/).  Using Byte Buddy, you can do fun things like override `Security.setSystemManager` with [your own implementation](https://tersesystems.com/blog/2016/01/19/redefining-java-dot-lang-dot-system/), so using Byte Buddy to decorate code with `enter` and `exit` logging statements is relatively straightforward.

Instrumentation is configuration driven and simple.  Instead of debugging using printf statements and recompiling or stepping through a debugger, you can just add lines to a config file.

I like this approach better than the annotation or aspect-oriented programming approaches, because it is completely transparent to the code and gives roughly the same performance as inline code, adding [130 ns/op](https://github.com/raphw/byte-buddy/issues/714) by calling `class.getMethod`.

A major advantage of instrumentation is that because it logs `throwing` exceptions in instrumented code, you can log exceptions that would be swallowed by the caller.  For example, imagine that a library has the following method:

```java
public class Foo {
    public void throwException() throws Exception {
        throw new PlumException("I am sweet and cold");
    }

    public void swallowException() {
        try {
            throwException();
        } catch (Exception e) {
            // forgive me, the exception was delicious
        }
    }
}
```

By instrumenting the `throwException` method, you can see the logged exception at runtime when `swallowException` is called.

See [Application Logging in Java: Tracing 3rd Party Code](https://tersesystems.com/blog/2019/06/11/application-logging-in-java-part-8/) and [Hierarchical Instrumented Tracing with Logback](https://tersesystems.com/blog/2019/09/15/hierarchical-instrumented-tracing-with-logback/) for more details.

## Installation

You'll need to install `logback-bytebuddy` and `logback-tracing`, and provide a `byte-buddy` implementation.

```
implementation group: 'com.tersesystems.logback', name: 'logback-classic', version: '0.16.2'
implementation group: 'com.tersesystems.logback', name: 'logback-bytebuddy', version: '0.16.2'
implementation group: 'com.tersesystems.logback', name: 'logback-tracing', version: '0.16.2'

implementation group: 'net.bytebuddy', name: 'byte-buddy', version: '1.11.0'
```

There are two ways you can install instrumentation -- you can do it using an agent, or you can do it manually.

> NOTE: Because Byte Buddy must inspect each class on JVM initialization, it will have a (generally small) impact on the start up time of your application.

### Agent Installation

Using the agent is generally easier (less code) and more powerful (can change JDK classes), but it does require some explicit command line options.

First, you set the java agent, either directly on the command line:

```bash
java \
  -javaagent:path/to/logback-bytebuddy-x.x.x.jar=debug \
  -Dterse.logback.configurationFile=conf/logback.conf \
  -Dlogback.configurationFile=conf/logback-test.xml \
  com.example.PreloadedInstrumentationExample
```

or by using the [`JAVA_TOOLS_OPTIONS` environment variable](https://docs.oracle.com/javase/8/docs/technotes/guides/troubleshoot/envvars002.html).

```bash
export JAVA_TOOLS_OPTIONS="..."
```

Generally you'll be setting up these options in a build system.  There are example projects in Gradle and sbt set up with agent-based instrumentation at [https://github.com/tersesystems/logging-instrumentation-example](https://github.com/tersesystems/logging-instrumentation-example).

### Manual Installation

You also have the option of installing the agent manually.

The in process instrumentation is done with `com.tersesystems.logback.bytebuddy.LoggingInstrumentationByteBuddyBuilder`, which takes in some configuration and then installs itself on the byte buddy agent.

```java
new LoggingInstrumentationByteBuddyBuilder()
        .builderFromConfig(loggingInstrumentationAdviceConfig)
        .with(debugListener)
        .installOnByteBuddyAgent();
```

## Configuration

There are two parts to seeing tracing logs with instrumentation -- indicating the classes and methods you want instrumented, and then setting those loggers to TRACE.

### Setting Instrumented Classes and Methods

The instrumentation is configured using [HOCON](https://github.com/lightbend/config/blob/main/HOCON.md) in a `logback.conf` file in `src/main/resources`.

Settings are under the `logback.bytebuddy` section.  The `tracing` section contains a mapping of class names and methods, or the wildcard "*" to indicate all methods.

```
logback.bytebuddy {
  service-name = "my-service"

  tracing {    
    "fully.qualified.class.Name" = ["method1", "method2"]
    "play.api.mvc.ActionBuilder" = ["*"]
  }
}
```

NOTE: There are some limitations to what you can trace.  You can only instrument JDK classes when using the agent, and you cannot instrument native methods like `java.lang.System.currentTimeMillis()` for example.

### Setting Loggers to TRACE

Because instrumentation inserts `logger.trace` calls into the code, you must enable logging at `TRACE` level for those loggers to see output.  Setting the level from `logback.xml` works fine:

```xml
<configuration>
    <!-- ... -->
    <logger name="fully.qualified.class.Name" level="TRACE"/>
    <logger name="play.api.mvc.ActionBuilder" level="TRACE"/>
    <!-- ... -->
</configuration>
```

If you are using the [Config](typesafeconfig.md) module, you can also do this from `logback.conf`:

```hocon
levels {
  fully.qualified.class.Name = TRACE
  play.api.mvc.ActionBuilder = TRACE
}
```

Or you can use `ChangeLogLevel` at run time.

## Examples

Instrumentation is a tool that can be hard to explain, so here's some use cases showing how you can quickly instrument your code.

Also don't forget the example projects at [https://github.com/tersesystems/logging-instrumentation-example](https://github.com/tersesystems/logging-instrumentation-example).

### Instrumenting java.lang.Thread

Assuming an agent based instrumentation, in `logback.conf`:

```hocon
levels {
  java.lang.Thread = TRACE
}

logback.bytebuddy {
  service-name = "some-service"
  tracing {
    "java.lang.Thread" = [
      "run"
    ]
  }
}
```

and the code as follows:

```java
public class PreloadedInstrumentationExample {
    public static void main(String[] args) throws Exception {
        Thread thread = Thread.currentThread();
        thread.run();
    }
}
```

yields

```text
[Byte Buddy] DISCOVERY java.lang.Thread [null, null, loaded=true]
[Byte Buddy] TRANSFORM java.lang.Thread [null, null, loaded=true]
[Byte Buddy] COMPLETE java.lang.Thread [null, null, loaded=true]
92    TRACE java.lang.Thread - entering: java.lang.Thread.run() with arguments=[]
93    TRACE java.lang.Thread - exiting: java.lang.Thread.run() with arguments=[] => returnType=void
```

### Instrumenting javax.net.ssl.SSLContext

This is especially helpful when you're trying to debug SSL issues:

```hocon
levels {
  sun.security.ssl = TRACE
  javax.net.ssl = TRACE
}

logback.bytebuddy {
  service-name = "some-service"
  tracing {  
    "javax.net.ssl.SSLContext" = ["*"]
  }
}
```

will result in:

```
FcJ3XfsdKnM6O0Qbm7EAAA 12:31:55.498 [TRACE] j.n.s.SSLContext -  entering: javax.net.ssl.SSLContext.getInstance(java.lang.String) with arguments=[TLS] from source SSLContext.java:155
FcJ3XfsdKng6O0Qbm7EAAA 12:31:55.503 [TRACE] j.n.s.SSLContext -  exiting: javax.net.ssl.SSLContext.getInstance(java.lang.String) with arguments=[TLS] => returnType=javax.net.ssl.SSLContext from source SSLContext.java:157
FcJ3XfsdKng6O0Qbm7EAAB 12:31:55.504 [TRACE] j.n.s.SSLContext -  entering: javax.net.ssl.SSLContext.init([Ljavax.net.ssl.KeyManager;,[Ljavax.net.ssl.TrustManager;,java.security.SecureRandom) with arguments=[[org.postgresql.ssl.LazyKeyManager@27a97e08], [org.postgresql.ssl.NonValidatingFactory$NonValidatingTM@5918c260], null] from source SSLContext.java:282
FcJ3XfsdKnk6O0Qbm7EAAA 12:31:55.504 [TRACE] j.n.s.SSLContext -  exiting: javax.net.ssl.SSLContext.init([Ljavax.net.ssl.KeyManager;,[Ljavax.net.ssl.TrustManager;,java.security.SecureRandom) with arguments=[[org.postgresql.ssl.LazyKeyManager@27a97e08], [org.postgresql.ssl.NonValidatingFactory$NonValidatingTM@5918c260], null] => returnType=void from source SSLContext.java:283
```

Be warned that JSSE can be extremely verbose in its `toString` output.

### Instrumenting ClassCalledByAgent

If you are already developing an agent, or want finer grained control over Byte Buddy, you can create the agent in process and inspect how Byte Buddy works.  This is an advanced use case, but it's useful to get familiar.

With the following code:

```java
public class ClassCalledByAgent {
    public void printStatement() {
        System.out.println("I am a simple println method with no logging");
    }

    public void printArgument(String arg) {
        System.out.println("I am a simple println, printing " + arg);
    }

    public void throwException(String arg) {
        throw new RuntimeException("I'm a squirrel!");
    }
}
```

And the following configuration in `logback.conf`:

```hocon
logback.bytebuddy {
  service-name = "example-service"
  tracing {
    "com.tersesystems.logback.bytebuddy.ClassCalledByAgent" = [
      "printStatement",
      "printArgument",
      "throwException",
    ]
  }
}
```

and have `com.tersesystems.logback.bytebuddy.ClassCalledByAgent` logging level set to `TRACE` in `logback.xml`.

We can start up the agent, add in the builder and run through the methods:

```java
public class InProcessInstrumentationExample {

    public static AgentBuilder.Listener createDebugListener(List<String> classNames) {
        return new AgentBuilder.Listener.Filtering(
                LoggingInstrumentationAdvice.stringMatcher(classNames),
                AgentBuilder.Listener.StreamWriting.toSystemOut());
    }

    public static void main(String[] args) throws Exception {
        // Helps if you install the byte buddy agents before anything else at all happens...
        ByteBuddyAgent.install();

        Logger logger = LoggerFactory.getLogger(InProcessInstrumentationExample.class);
        SystemFlow.setLoggerResolver(new FixedLoggerResolver(logger));

        Config config = LoggingInstrumentationAdvice.generateConfig(ClassLoader.getSystemClassLoader(), false);
        LoggingInstrumentationAdviceConfig adviceConfig = LoggingInstrumentationAdvice.generateAdviceConfig(config);

        // The debugging listener shows what classes are being picked up by the instrumentation
        Listener debugListener = createDebugListener(adviceConfig.classNames());
        new LoggingInstrumentationByteBuddyBuilder()
                .builderFromConfig(adviceConfig)
                .with(debugListener)
                .installOnByteBuddyAgent();

        // No code change necessary here, you can wrap completely in the agent...
        ClassCalledByAgent classCalledByAgent = new ClassCalledByAgent();
        classCalledByAgent.printStatement();
        classCalledByAgent.printArgument("42");

        try {
            classCalledByAgent.throwException("hello world");
        } catch (Exception e) {
            // I am too lazy to catch this exception.  I hope someone does it for me.
        }
    }
}
```

And get the following:

```text
[Byte Buddy] DISCOVERY com.tersesystems.logback.bytebuddy.ClassCalledByAgent [sun.misc.Launcher$AppClassLoader@75b84c92, null, loaded=true]
[Byte Buddy] TRANSFORM com.tersesystems.logback.bytebuddy.ClassCalledByAgent [sun.misc.Launcher$AppClassLoader@75b84c92, null, loaded=true]
[Byte Buddy] COMPLETE com.tersesystems.logback.bytebuddy.ClassCalledByAgent [sun.misc.Launcher$AppClassLoader@75b84c92, null, loaded=true]
524   TRACE c.t.l.b.InProcessInstrumentationExample - entering: com.tersesystems.logback.bytebuddy.ClassCalledByAgent.printStatement() with arguments=[] from source ClassCalledByAgent.java:18
I am a simple println method with no logging
529   TRACE c.t.l.b.InProcessInstrumentationExample - exiting: com.tersesystems.logback.bytebuddy.ClassCalledByAgent.printStatement() with arguments=[] => returnType=void from source ClassCalledByAgent.java:19
529   TRACE c.t.l.b.InProcessInstrumentationExample - entering: com.tersesystems.logback.bytebuddy.ClassCalledByAgent.printArgument(java.lang.String) with arguments=[42] from source ClassCalledByAgent.java:22
I am a simple println, printing 42
529   TRACE c.t.l.b.InProcessInstrumentationExample - exiting: com.tersesystems.logback.bytebuddy.ClassCalledByAgent.printArgument(java.lang.String) with arguments=[42] => returnType=void from source ClassCalledByAgent.java:23
529   TRACE c.t.l.b.InProcessInstrumentationExample - entering: com.tersesystems.logback.bytebuddy.ClassCalledByAgent.throwException(java.lang.String) with arguments=[hello world] from source ClassCalledByAgent.java:26
532   ERROR c.t.l.b.InProcessInstrumentationExample - throwing: com.tersesystems.logback.bytebuddy.ClassCalledByAgent.throwException(java.lang.String) with arguments=[hello world] ! thrown=java.lang.RuntimeException: I'm a squirrel!
java.lang.RuntimeException: I'm a squirrel!
	at com.tersesystems.logback.bytebuddy.ClassCalledByAgent.throwException(ClassCalledByAgent.java:26)
	at com.tersesystems.logback.bytebuddy.InProcessInstrumentationExample.main(InProcessInstrumentationExample.java:65)
```

The `[Byte Buddy]` statements up top are caused by the debug listener, and let you know that Byte Buddy has successfully instrumented the class.  Note also that there is no runtime overhead in pulling line numbers or source files into the enter/exit methods, as these are pulled directly from bytecode and do not involve `fillInStackTrace`.
