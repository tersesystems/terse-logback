/*
 * SPDX-License-Identifier: CC0-1.0
 *
 * Copyright 2018-2019 Will Sargent.
 *
 * Licensed under the CC0 Public Domain Dedication;
 * You may obtain a copy of the License at
 *
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */
package com.tersesystems.logback.bytebuddy;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.pool.TypePool;

import static net.bytebuddy.matcher.ElementMatchers.*;
import static net.bytebuddy.matcher.ElementMatchers.any;

/**
 * Creates byte buddy agent builders with the LoggingInstrumentation advice.
 */
public class LoggingInstrumentationByteBuddyBuilder {

    private static final MethodInfoLookup METHOD_INFO_LOOKUP = MethodInfoLookup.getInstance();

    private static final Class<?> INSTRUMENTATION_ADVICE_CLASS = LoggingInstrumentationAdvice.class;

    /**
     * Creates a builder from the element matchers.
     *
     * @param typesMatcher   an element matcher for types we should instrument.
     * @param methodsMatcher an element matcher for the methods in the types that should be instrumented.
     * @return
     */
    public AgentBuilder builderFromConfig(ElementMatcher<? super TypeDescription> typesMatcher,
                                          ElementMatcher<? super MethodDescription> methodsMatcher) {
        return new AgentBuilder.Default()
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE)
                .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
                .disableClassFormatChanges() // frozen instrumented types
                .type(typesMatcher) // for these classes...
                .transform((builder, type, classLoader, module) -> {
                    // ...apply this advice to these methods.
                    Advice to = Advice.to(INSTRUMENTATION_ADVICE_CLASS);
                    AsmVisitorWrapper on = to.on(methodsMatcher);
                    AsmVisitorWrapper lineWrapper = wrapper();
                    return builder.visit(lineWrapper).visit(on);
                });
    }

    private AsmVisitorWrapper wrapper() {
        return new AsmVisitorWrapper.AbstractBase() {
            @Override
            public ClassVisitor wrap(TypeDescription instrumentedType,
                                     ClassVisitor classVisitor,
                                     Implementation.Context implementationContext,
                                     TypePool typePool,
                                     FieldList<FieldDescription.InDefinedShape> fields,
                                     MethodList<?> methods, int writerFlags, int readerFlags) {
                return new ClassVisitor(Opcodes.ASM5, classVisitor) {
                    private String className;
                    private String source;

                    @Override
                    public void visitSource(String source, String debug) {
                        this.source = source;
                        super.visitSource(source, debug);
                    }

                    @Override
                    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                        this.className = name != null ? name.replace('/', '.') : null;
                        super.visit(version, access, name, signature, superName, interfaces);
                    }

                    @Override
                    public MethodVisitor visitMethod(int access,
                                                     String n,
                                                     String d,
                                                     String s,
                                                     String[] e) {
                        MethodVisitor methodVisitor = super.visitMethod(access, n, d, s, e);
                        return new MethodVisitor(Opcodes.ASM5, methodVisitor) {
                            private int line;
                            MethodInfo methodInfo = new MethodInfo(n, d, e, source);

                            boolean isStart = false;

                            @Override
                            public void visitCode() {
                                isStart = true;
                                super.visitCode();
                            }

                            @Override
                            public void visitLineNumber(int line, Label start) {
                                if (isStart) {
                                    methodInfo.setStartLine(line);
                                    isStart = false;
                                }
                                this.line = line;
                                super.visitLineNumber(line, start);
                            }

                            @Override
                            public void visitEnd() {
                                methodInfo.setEndLine(line);
                                METHOD_INFO_LOOKUP.add(className, methodInfo);
                                super.visitEnd();
                            }
                        };
                    }
                };
            }
        };
    }

    /**
     * Use this method if you want to redefine system classloader classes.
     *
     * @param typesMatcher   an element matcher for types we should instrument.
     * @param methodsMatcher an element matcher for the methods in the types that should be instrumented.
     * @return agent builder with ignore and RETRANSFORMATION set.
     */
    public AgentBuilder builderFromConfigWithRetransformation(ElementMatcher<? super TypeDescription> typesMatcher,
                                                              ElementMatcher<? super MethodDescription> methodsMatcher) {
        return withSystemClassLoaderMatching(builderFromConfig(typesMatcher, methodsMatcher));
    }

    protected AgentBuilder withSystemClassLoaderMatching(AgentBuilder builder) {
        return builder
                .ignore(ignoreMatchers())                                 // do not ignore system classes
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION); // try to retransform already loaded classes
    }

    public AgentBuilder.RawMatcher.ForElementMatchers ignoreMatchers() {
        ElementMatcher.Junction<? super TypeDescription> matchers =
                nameStartsWith("net.bytebuddy.")
                        //.or(nameStartsWith("com.tersesystems.logback.bytebuddy"))
                        .or(nameStartsWith("org.slf4j."))
                        .or(nameStartsWith("ch.qos.logback."))
                        .or(isSynthetic());
        return new AgentBuilder.RawMatcher.ForElementMatchers(matchers, any(), any());
    }

    public AgentBuilder builderFromConfig(ElementMatcher<? super TypeDescription> typesMatcher,
                                          ElementMatcher<? super MethodDescription> methodsMatcher,
                                          AgentBuilder.Listener listener) {
        return builderFromConfig(typesMatcher, methodsMatcher).with(listener);
    }

    public AgentBuilder builderFromConfig(LoggingAdviceConfig c) {
        return builderFromConfig(c.types(), c.methods());
    }

    public AgentBuilder builderFromConfigWithRetransformation(LoggingAdviceConfig c) {
        return builderFromConfigWithRetransformation(c.types(), c.methods());
    }
}
