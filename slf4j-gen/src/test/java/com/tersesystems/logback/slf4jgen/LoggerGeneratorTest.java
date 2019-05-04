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
package com.tersesystems.logback.slf4jgen;

import com.squareup.javapoet.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.function.Predicate;

public class LoggerGeneratorTest {

    @Test
    public void testProxy() throws IOException {
        String delegate = "proxy";

        TypeSpec.Builder typeBuilder = TypeSpec.classBuilder("ProxyLogger")
                .addSuperinterface(Logger.class)
                .addModifiers(Modifier.PUBLIC)
                .addField(org.slf4j.Logger.class, delegate, Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(org.slf4j.Logger.class, delegate)
                        .addStatement("this.$N = $N", delegate, delegate)
                        .build());

        ProxyFrame proxyFrame = () -> delegate;
        LoggerGenerator generator = new LoggerGenerator(typeBuilder, proxyFrame);
        TypeSpec proxyLoggerTypeSpec = generator.generate();

        JavaFile javaFile = JavaFile.builder("com.tersesystems.logback.ext.proxy", proxyLoggerTypeSpec)
                .build();
        javaFile.writeTo(System.out);
    }


    @Test
    public void testPredicate() throws IOException {
        String predicate = "predicate";
        String delegate = "proxy";

        TypeName levelPredicateTypeName = ParameterizedTypeName.get(Predicate.class, Level.class);
        TypeSpec.Builder typeBuilder = TypeSpec.classBuilder("PredicateLogger")
                .addSuperinterface(Logger.class)
                .addModifiers(Modifier.PUBLIC)
                .addField(org.slf4j.Logger.class, delegate, Modifier.PRIVATE, Modifier.FINAL)
                .addField(levelPredicateTypeName, predicate, Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(org.slf4j.Logger.class, delegate)
                        .addParameter(levelPredicateTypeName, predicate)
                        .addStatement("this.$N = $N", delegate, delegate)
                        .addStatement("this.$N = $N", predicate, predicate)
                        .build());

        PredicateFrame predicateFrame = new PredicateFrame() {
            @Override
            public String predicate() {
                return predicate;
            }

            @Override
            public String delegate() {
                return delegate;
            }
        };
        LoggerGenerator generator = new LoggerGenerator(typeBuilder, predicateFrame);

        TypeSpec proxyLoggerTypeSpec = generator.generate();
        JavaFile javaFile = JavaFile.builder("com.tersesystems.logback.ext.predicate", proxyLoggerTypeSpec)
                .build();
        javaFile.writeTo(System.out);
    }
}
