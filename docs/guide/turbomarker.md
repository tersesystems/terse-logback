# Turbo Markers

[Turbo filters](https://logback.qos.ch/manual/filters.html#TurboFilter) are filters that decide whether a logging event should be created or not.  They are not appender specific in the way that normal filters are, and so are used to override logger levels.  However, there's a problem with the way that the turbo filter is set up: the two implementing classes are `ch.qos.logback.classic.turbo.MarkerFilter` and `ch.qos.logback.classic.turbo.MDCFilter`.  The marker filter will always log if the given marker is applied, and the MDC filter relies on an attribute being populated in the MDC map.

What we'd really like to do is say "for this particular user, log everything he does at DEBUG level" and not have it rely on thread-local state at all, and carry out an arbitrary computation at call time.  We can do this by adding a decider to a turbo filter, and adding "turbo markers."

## Installation

Add the library dependency using [com.tersesystems.logback:logback-turbomarker](https://mvnrepository.com/artifact/com.tersesystems.logback/logback-turbomarker).

## Usage

We start by pulling the `decide` method to an interface, [`TurboFilterDecider`](https://github.com/tersesystems/terse-logback/blob/master/logback-classic/src/main/java/com/tersesystems/logback/classic/TurboFilterDecider.java):

```java
package com.tersesystems.logback.classic;

public interface TurboFilterDecider {
    FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t);
}
```

And have the turbo filter [delegate to markers that implement the TurboFilterDecider interface](https://github.com/tersesystems/terse-logback/blob/master/logback-turbomarker/src/main/java/com/tersesystems/logback/turbomarker/TurboMarkerTurboFilter.java):

```java
package com.tersesystems.logback.turbomarker;

public class TurboMarkerTurboFilter extends TurboFilter {

    @Override
    public FilterReply decide(Marker rootMarker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        // ...
    }

    private FilterReply evaluateMarker(Marker marker, Marker rootMarker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        if (marker instanceof TurboFilterDecider) {
            TurboFilterDecider decider = (TurboFilterDecider) marker;
            return decider.decide(rootMarker, logger, level, format, params, t);
        }
        return FilterReply.NEUTRAL;
    }
}
```

This gets us part of the way there.  We can then set up a [`ContextAwareTurboFilterDecider`](https://github.com/tersesystems/terse-logback/blob/master/logback-turbomarker/src/main/java/com/tersesystems/logback/turbomarker/ContextAwareTurboFilterDecider.java), which does the same thing but assumes that you have a type `C` that is your external context.

```java
public interface ContextAwareTurboFilterDecider<C> {
    FilterReply decide(ContextAwareTurboMarker<C> marker, C context, Marker rootMarker, Logger logger, Level level, String format, Object[] params, Throwable t);
}
```

Then we add a marker class that [incorporates that context in decision making](https://github.com/tersesystems/terse-logback/blob/master/logback-turbomarker/src/main/java/com/tersesystems/logback/turbomarker/ContextAwareTurboMarker.java):

```java
public class ContextAwareTurboMarker<C> extends TurboMarker implements TurboFilterDecider {
    private final C context;
    private final ContextAwareTurboFilterDecider<C> contextAwareDecider;
    // ... initializers and such
    @Override
    public FilterReply decide(Marker rootMarker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        return contextAwareDecider.decide(this, context, rootMarker, logger, level, format, params, t);
    }
}
```

This may look good in the abstract, but it may make more sense to see it in action.  To do this, we'll set up an example application context:

```java
public class ApplicationContext {

    private final String userId;

    public ApplicationContext(String userId) {
        this.userId = userId;
    }

    public String currentUserId() {
        return userId;
    }
}
```

and a factory that contains the decider:

```java
import com.tersesystems.logback.turbomarker.*;

public class UserMarkerFactory {

    private final Set<String> userIdSet = new ConcurrentSkipListSet<>();

    private final ContextDecider<ApplicationContext> decider = context ->
        userIdSet.contains(context.currentUserId()) ? FilterReply.ACCEPT : FilterReply.NEUTRAL;

    public void addUserId(String userId) {
        userIdSet.add(userId);
    }

    public void clear() {
        userIdSet.clear();
    }

    public UserMarker create(ApplicationContext applicationContext) {
        return new UserMarker("userMarker", applicationContext, decider);
    }
}
```

and a `UserMarker`, which is only around for the logging evaluation:

```java
public class UserMarker extends ContextAwareTurboMarker<ApplicationContext> {
    public UserMarker(String name,
                      ApplicationContext applicationContext,
                      ContextAwareTurboFilterDecider<ApplicationContext> decider) {
        super(name, applicationContext, decider);
    }
}
```

and then we can set up logging that will only work for user "28":

```java
String userId = "28";
ApplicationContext applicationContext = new ApplicationContext(userId);
UserMarkerFactory userMarkerFactory = new UserMarkerFactory();
userMarkerFactory.addUserId(userId); // say we want logging events created for this user id

UserMarker userMarker = userMarkerFactory.create(applicationContext);

logger.info(userMarker, "Hello world, I am info and log for everyone");
logger.debug(userMarker, "Hello world, I am debug and only log for user 28");
```

This works especially well with a configuration management service like [Launch Darkly](https://docs.launchdarkly.com/docs/java-sdk-reference#section-variation), where you can [target particular users](https://docs.launchdarkly.com/docs/targeting-users#section-assigning-users-to-a-variation) and set up logging based on the user variation.  

The LaunchDarkly blog has [best practices for operational flags](https://launchdarkly.com/blog/operational-flags-best-practices/):

> Verbose logs are great for debugging and troubleshooting but always running an application in debug mode is not viable. The amount of log data generated would be overwhelming. Changing logging levels on the fly typically requires changing a configuration file and restarting the application. A multivariate operational flag enables you to change the logging level from WARNING to DEBUG in real-time.

But we can give an example using the Java SDK.  You can set up a factory like so:

```java
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.spi.FilterReply;
import com.launchdarkly.client.LDClientInterface;
import com.launchdarkly.client.LDUser;

public class LDMarkerFactory {
    private final LaunchDarklyDecider decider;

    public LDMarkerFactory(LDClientInterface client) {
        this.decider = new LaunchDarklyDecider(requireNonNull(client));
    }

    public LDMarker create(String featureFlag, LDUser user) {
        return new LDMarker(featureFlag, user, decider);
    }

    static class LaunchDarklyDecider implements MarkerContextDecider<LDUser> {
        private final LDClientInterface ldClient;

        LaunchDarklyDecider(LDClientInterface ldClient) {
            this.ldClient = ldClient;
        }

        @Override
        public FilterReply apply(ContextAwareTurboMarker<LDUser> marker, LDUser ldUser) {
            return ldClient.boolVariation(marker.getName(), ldUser, false) ?
                    FilterReply.ACCEPT :
                    FilterReply.NEUTRAL;
        }
    }

    public static class LDMarker extends ContextAwareTurboMarker<LDUser> {
        LDMarker(String name, LDUser context, ContextAwareTurboFilterDecider<LDUser> decider) {
            super(name, context, decider);
        }
    }
}
```

and then use the feature flag as the marker name and target the beta testers group:

```java
public class LDMarkerTest {
  private static LDClientInterface client;

  @BeforeAll
  public static void setUp() {
      client = new LDClient("sdk-key");
  }

  @AfterAll
  public static void shutDown() {
    try {
      client.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void testMatchingMarker() throws JoranException {
    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    ch.qos.logback.classic.Logger logger = loggerContext.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

    LDMarkerFactory markerFactory = new LDMarkerFactory(client);
    LDUser ldUser = new LDUser.Builder("UNIQUE IDENTIFIER")
            .firstName("Bob")
            .lastName("Loblaw")
            .customString("groups", singletonList("beta_testers"))
            .build();

    LDMarkerFactory.LDMarker ldMarker = markerFactory.create("turbomarker", ldUser);

    logger.info(ldMarker, "Hello world, I am info");
    logger.debug(ldMarker, "Hello world, I am debug");

    ListAppender<ILoggingEvent> appender = (ListAppender<ILoggingEvent>) logger.getAppender("LIST");
    assertThat(appender.list.size()).isEqualTo(2);

    appender.list.clear();
  }
}
```

This is also a reason why [diagnostic logging is better than a debugger](https://lemire.me/blog/2016/06/21/i-do-not-use-a-debugger/).  Debuggers are ephemeral, can't be used in production, and don't produce a consistent record of events: debugging log statements are the single best way to dump internal state and manage code flows in an application.

See [Targeted Diagnostic Logging in Production](https://tersesystems.com/blog/2019/07/22/targeted-diagnostic-logging-in-production/) for more details.
