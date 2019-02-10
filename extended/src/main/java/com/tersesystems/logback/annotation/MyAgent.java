package com.tersesystems.logback.annotation;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.InitializationStrategy;
import net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy;
import net.bytebuddy.agent.builder.AgentBuilder.TypeStrategy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.matcher.StringMatcher;

import static net.bytebuddy.matcher.ElementMatchers.*;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.annotationType;

public class MyAgent {

//    public static void premain(Instrumentation instrumentation) {
//        System.out.println("Entered premain");
//        try {
//            new AgentBuilder.Default()
//                    .with(RedefinitionStrategy.RETRANSFORMATION)
//                    .with(InitializationStrategy.NoOp.INSTANCE)
//                    .with(TypeStrategy.Default.REDEFINE)
//                    .ignore(new AgentBuilder.RawMatcher.ForElementMatchers(nameStartsWith("net.bytebuddy.").or(isSynthetic()), any(), any()))
//
//                    .with(AgentBuilder.Listener.StreamWriting.toSystemError().withErrorsOnly())
//                    //.type(is(hasAnnotation(annotationType(AutoLog.class)).and(isMethod())))
//                    .with(new AgentBuilder.Listener.Filtering(
//                            new StringMatcher("example", StringMatcher.Mode.STARTS_WITH),
//                            AgentBuilder.Listener.StreamWriting.toSystemOut()))
//                    .type(any())
//                    .transform((builder, type, classLoader, module) ->
//                            builder.method(isAnnotatedWith(AutoLog.class)).intercept(Advice.to(Interceptor.class))
//                    )
//                    .installOn(instrumentation);
//        } catch (RuntimeException e) {
//            System.out.println("Exception instrumenting code : " + e);
//            e.printStackTrace();
//        }
//    }

    // https://stackoverflow.com/questions/51594148/duplicate-class-error-in-a-bytebuddy-agent-applying-advice?rq=1
    // public static void premain(String arguments, Instrumentation instrumentation) {
    //     installedInPremain = true;
    //     new AgentBuilder.Default()
    //             .type(ElementMatchers.named("com.acme.FooBar"))
    //             .transform((builder, typeDescription, classLoader, module) -> visit(builder))
    //             .installOn(instrumentation);

    // }

    
    // public static void instrumentOnDemand() {
    //     ByteBuddyAgent.install();
    //     DynamicType.Builder<URLPropertyLoader> typeBuilder = new ByteBuddy().redefine(FooBar.class);
    //     DynamicType.Builder<FooBar> visited = visit(typeBuilder);
    //     visited.make().load(FooBar.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    // }

    // private static <T> DynamicType.Builder<T> visit(DynamicType.Builder<T> builder) {
    //     return builder.visit(Advice.to(SnoopLoad.class).on(named("load").and(takesArguments(0))))
    //             .visit(Advice.to(SnoopOpenStream.class).on(named("openStream")))
    //             .visit(Advice.to(SnoopPut.class).on(named("put")));
    // }

    // https://github.com/raphw/byte-buddy/blob/master/byte-buddy-dep/src/test/java/net/bytebuddy/agent/builder/AgentBuilderDefaultApplicationTest.java
    // public static void premain(Instrumentation instrumentation) {
    //     System.out.println("Entered premain");
    //     try {
    //         new AgentBuilder.Default()
    //                 .with(RedefinitionStrategy.RETRANSFORMATION)
    //                 .with(InitializationStrategy.NoOp.INSTANCE)
    //                 .with(TypeStrategy.Default.REDEFINE)
    //                 .ignore(new AgentBuilder.RawMatcher.ForElementMatchers(nameStartsWith("net.bytebuddy.").or(isSynthetic()), any(), any()))
    //                 //.with(AgentBuilder.Listener.StreamWriting.toSystemError().withErrorsOnly())
    //                 .with(new AgentBuilder.Listener.Filtering(
    //                         new StringMatcher("example", StringMatcher.Mode.STARTS_WITH),
    //                     AgentBuilder.Listener.StreamWriting.toSystemOut()))
    //                 .type(any())
    //                 .transform((builder, type, classLoader, module) ->
    //                     builder.method(named("doStuff")).intercept(MethodDelegation.to(Interceptor.class))
    //                 )
    //                 .installOn(instrumentation);
    //     } catch (RuntimeException e) {
    //         System.out.println("Exception instrumenting code : " + e);
    //         e.printStackTrace();
    //     }
    // }

}