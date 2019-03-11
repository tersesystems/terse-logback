
## Instrumenting Logging Code with Byte Buddy

If you have library code that doesn't pass around `ILoggerFactory` and doesn't let you add information to logging, then you can get around this by instrumenting the code with [Byte Buddy](https://bytebuddy.net/).  Using Byte Buddy, you can do fun things like override `Security.setSystemManager` with [your own implementation](https://tersesystems.com/blog/2016/01/19/redefining-java-dot-lang-dot-system/), so using Byte Buddy to decorate code with `enter` and `exit` logging statements is relatively straightforward.

There's two different ways to do it.  You can use interception, which gives you a straightforward method delegation model, or you can use `Advice`, which rewrites the bytecode inline before the JVM gets to it.  Either way, you can write to a logger without touching the class itself, and you can modify which classes and methods you touch.  

I like this approach better than the annotation or aspect-oriented programming approaches, because it is completely transparent to the code and gives the same performance as inline code.  I use a `ThreadLocal` logger here, as it gives me more control over logging capabilities than using MDC would, but there are many options available.

```java
package example;

import com.tersesystems.logback.bytebuddy.ClassAdviceRewriter;
import com.tersesystems.logback.bytebuddy.InfoLoggingInterceptor;
import com.tersesystems.logback.bytebuddy.ThreadLocalLogger;
import net.bytebuddy.ByteBuddy;
import static net.bytebuddy.agent.builder.AgentBuilder.*;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.StringMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * Use ByteBuddy to add logging to classes that don't have it.
 */
public class ClassWithByteBuddy {

    // This is a class we're going to wrap entry and exit methods around.
    public static class SomeLibraryClass {
        public void doesNotUseLogging() {
            System.out.println("Logging sucks, I use println");
        }
    }

    // We can do this by intercepting the class and putting stuff around it.
    static class Interception {
        // Do it through wrapping
        public SomeLibraryClass instrumentClass() throws IllegalAccessException, InstantiationException {
            Class<SomeLibraryClass> offendingClass = SomeLibraryClass.class;
            String offendingMethodName = "doesNotUseLogging";

            return new ByteBuddy()
                    .subclass(offendingClass)
                    .method(ElementMatchers.named(offendingMethodName))
                    .intercept(MethodDelegation.to(new InfoLoggingInterceptor()))
                    .make()
                    .load(offendingClass.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                    .getLoaded()
                    .newInstance();
        }

        public void doStuff() throws IllegalAccessException, InstantiationException {
            SomeLibraryClass someLibraryClass = this.instrumentClass();
            someLibraryClass.doesNotUseLogging();
        }
    }

    // This is a class we're going to redefine completely.
    public static class SomeOtherLibraryClass {
        public void doesNotUseLogging() {
            System.out.println("I agree, I don't use logging either");
        }
    }

    static class AgentBased {
        public static void premain() {
            try {
                String className = "SomeOtherLibraryClass";
                String methodName = "doesNotUseLogging";

                // The debugging listener shows what classes are being picked up by the instrumentation
                Listener.Filtering debuggingListener = new Listener.Filtering(
                        new StringMatcher(className, StringMatcher.Mode.CONTAINS),
                        Listener.StreamWriting.toSystemOut());

                // This gives a bit of a speedup when going through classes...
                RawMatcher ignoreMatcher = new RawMatcher.ForElementMatchers(ElementMatchers.nameStartsWith("net.bytebuddy.").or(isSynthetic()), any(), any());

                // Create and install the byte buddy remapper
                new AgentBuilder.Default()
                        .with(new AgentBuilder.InitializationStrategy.SelfInjection.Eager())
                        .ignore(ignoreMatcher)
                        .with(debuggingListener)
                        .type(ElementMatchers.nameContains(className))
                        .transform((builder, type, classLoader, module) ->
                                builder.visit(Advice.to(ClassAdviceRewriter.class).on(named(methodName)))
                        )
                        .installOnByteBuddyAgent();
            } catch (RuntimeException e) {
                System.out.println("Exception instrumenting code : " + e);
                e.printStackTrace();
            }
        };

        public void doStuff() {
            // No code change necessary here, you can wrap completely in the agent...
            SomeOtherLibraryClass someOtherLibraryClass = new SomeOtherLibraryClass();
            someOtherLibraryClass.doesNotUseLogging();
        }
    }

    public static void main(String[] args) throws Exception {
        // Helps if you install the byte buddy agents before anything else at all happens...
        ByteBuddyAgent.install();
        AgentBased.premain();

        Logger logger = LoggerFactory.getLogger(ClassWithByteBuddy.class);
        ThreadLocalLogger.setLogger(logger);

        new Interception().doStuff();
        new AgentBased().doStuff();
    }
}
```

Provides (with the bytebuddy instrumentation debug output included):

```text
[Byte Buddy] DISCOVERY example.ClassWithByteBuddy$SomeOtherLibraryClass [sun.misc.Launcher$AppClassLoader@18b4aac2, null, loaded=false]
[Byte Buddy] TRANSFORM example.ClassWithByteBuddy$SomeOtherLibraryClass [sun.misc.Launcher$AppClassLoader@18b4aac2, null, loaded=false]
[Byte Buddy] COMPLETE example.ClassWithByteBuddy$SomeOtherLibraryClass [sun.misc.Launcher$AppClassLoader@18b4aac2, null, loaded=false]
[INFO ] e.ClassWithByteBuddy - entering: example.ClassWithByteBuddy$SomeLibraryClass.doesNotUseLogging()
Logging sucks, I use println
[INFO ] e.ClassWithByteBuddy - exit: example.ClassWithByteBuddy$SomeLibraryClass.doesNotUseLogging(), response = null
[INFO ] e.ClassWithByteBuddy - entering: example.ClassWithByteBuddy$SomeOtherLibraryClass.doesNotUseLogging()
I agree, I don't use logging either
[INFO ] e.ClassWithByteBuddy - exiting: example.ClassWithByteBuddy$SomeOtherLibraryClass.doesNotUseLogging()
```