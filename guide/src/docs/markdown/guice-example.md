
## Dependency Injection with Guice

Finally, if you're using a DI framework like Guice, you can leverage some of the contextual code in [Sangria](https://tavianator.com/announcing-sangria/) to do some of the gruntwork for you.  For example, here's how you configure a `Logger` instance in Guice:

```java
package example;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.spi.InjectionPoint;
import com.tavianator.sangria.contextual.ContextSensitiveBinder;
import com.tavianator.sangria.contextual.ContextSensitiveProvider;
import com.tersesystems.logback.context.logstash.LogstashContext;
import com.tersesystems.logback.context.logstash.LogstashLoggerFactory;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

public class GuiceAssistedLogging {

    public static class MyClass {
        private final Logger logger;

        @Inject
        MyClass(Logger logger) {
            this.logger = logger;
        }

        public void doStuff() {
            logger.info("hello world!");
        }
    }

    @Singleton
    static class Slf4jLoggerProvider implements ContextSensitiveProvider<Logger> {
        private final ILoggerFactory loggerFactory;

        @Inject
        Slf4jLoggerProvider(ILoggerFactory loggerFactory) {
            this.loggerFactory = loggerFactory;
        }

        @Override
        public Logger getInContext(InjectionPoint injectionPoint) {
            return loggerFactory.getLogger(injectionPoint.getDeclaringType().getRawType().getName());
        }

        @Override
        public Logger getInUnknownContext() {
            return loggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        }
    }

    static class ILoggerFactoryProvider implements Provider<ILoggerFactory> {
        @Override
        public ILoggerFactory get() {
            // This would be hooked up to @RequestScoped in a real application
            LogstashContext context = LogstashContext.create("threadName", Thread.currentThread().getName());
            return LogstashLoggerFactory.create().withContext(context);
        }
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                install(new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(ILoggerFactory.class).toProvider(ILoggerFactoryProvider.class);
                        ContextSensitiveBinder.create(binder())
                                .bind(Logger.class)
                                .toContextSensitiveProvider(Slf4jLoggerProvider.class);
                    }
                });
            }
        });

        MyClass instance = injector.getInstance(MyClass.class);
        // Assume this is running in an HTTP request that is @RequestScoped
        instance.doStuff();
    }

}
```

which yields:

```json
{"@timestamp":"2019-01-27T00:19:08.628+00:00","@version":"1","message":"hello world!","logger_name":"example.GuiceAssistedLogging$MyClass","thread_name":"main","level":"INFO","level_value":20000,"threadName":"main"}
```

If you are using a Servlet based API, then you can piggyback of Guice's [servlet extensions](https://github.com/google/guice/wiki/Servlets) and then integrate the logging context as part of the [`CDI / JSR 299 / @RequestScoped`](https://docs.oracle.com/javaee/6/tutorial/doc/gjbbk.html).  I have not tried this myself.
