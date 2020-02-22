
## Instrumenting Compiled Code with Logging using Byte Buddy

If you have library code that doesn't pass around `ILoggerFactory` and doesn't let you add information to logging, then you can get around this by instrumenting the code with [Byte Buddy](https://bytebuddy.net/).  Using Byte Buddy, you can do fun things like override `Security.setSystemManager` with [your own implementation](https://tersesystems.com/blog/2016/01/19/redefining-java-dot-lang-dot-system/), so using Byte Buddy to decorate code with `enter` and `exit` logging statements is relatively straightforward.

I like this approach better than the annotation or aspect-oriented programming approaches, because it is completely transparent to the code and gives roughly the same performance as inline code, adding [130 ns/op](https://github.com/raphw/byte-buddy/issues/714) by calling `class.getMethod`.

There are two ways you can instrument code.  The first way is to do it in process, after the JVM has loaded.  The second way is to load the java agent before the JVM starts, which lets you instrument classes on the system classloader.

### Instrumenting Application Code

The in process instrumentation is done with `com.tersesystems.logback.bytebuddy.LoggingInstrumentationByteBuddyBuilder`, which takes in some configuration and then installs itself on the byte buddy agent.

```java
new LoggingInstrumentationByteBuddyBuilder()
        .builderFromConfig(loggingInstrumentationAdviceConfig)
        .with(debugListener)
        .installOnByteBuddyAgent();
```

This is driven from configuration, so with the following code:

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

If you want to trace all the methods in a class, you can use a wildcard, i.e. `"mypackage.MyClass" = ["*"]`.

If you are using `logback-typesafe-config` then you can also set the levels from `logback.conf`, which can be very convenient.  For example in a Play application, you could set:

```hocon
levels {
  controllers = TRACE
  services = TRACE
  models = TRACE
  play.api.mvc = TRACE
}

logback.bytebuddy {
  service-name = "play-hello-world"

  tracing {
    "play.api.mvc.ActionBuilder" = ["*"]
    "play.api.mvc.BodyParser" = ["*"]
    "play.api.mvc.ActionFunction" = ["*"]

    "services.ComputerService" = ["*"]
    "controllers.HomeController" = ["*"]
    "models.Computer" = ["*"]
  }
}
```

and get automatic tracing.  This works particularly well with the honeycomb appender.

### Instrumenting System Classes

Instrumenting system level classes is a bit more involved, but can be done in configuration.

> **NOTE**: There are some limitations to instrumenting system level code.  You cannot instrument native methods like `java.lang.System.currentTimeMillis()` for example.

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

and then in `logback.conf`:

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
