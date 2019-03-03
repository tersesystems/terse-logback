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
package com.tersesystems.logback.censor;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Context;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.typesafe.config.Config;
import org.junit.Test;

import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;

public class CensoringJsonGeneratorDecoratorTest extends AbstractConfigBase {

    @Test
    public void basicCensor() throws Exception {
        CensoringJsonGeneratorDecorator decorator = new CensoringJsonGeneratorDecorator();
        Context context = new LoggerContext();
        Config config = loadConfig();
        context.putObject(CensorConstants.TYPESAFE_CONFIG_CTX_KEY, config);
        decorator.setContext(context);
        decorator.start();

        StringWriter writer = new StringWriter();
        JsonFactory factory = new MappingJsonFactory();
        JsonGenerator generator = decorator.decorate(factory.createGenerator(writer));

        generator.writeStartObject();
        generator.writeStringField("message", "My hunter2 message");
        generator.writeEndObject();
        generator.flush();

        assertThat(writer.toString()).isEqualTo("{\"message\":\"My ******* message\"}");
    }

    @Test
    public void filterKey() throws Exception {
        CensoringJsonGeneratorDecorator decorator = new CensoringJsonGeneratorDecorator();
        Context context = new LoggerContext();
        Config config = loadConfig();
        context.putObject(CensorConstants.TYPESAFE_CONFIG_CTX_KEY, config);
        decorator.setContext(context);
        decorator.start();

        StringWriter writer = new StringWriter();
        JsonFactory factory = new MappingJsonFactory();
        JsonGenerator generator = decorator.decorate(factory.createGenerator(writer));

        generator.writeStartObject();
        generator.writeStringField("password", "this entire field should be gone");
        generator.writeEndObject();
        generator.flush();

        assertThat(writer.toString()).isEqualTo("");
    }

    @Test
    public void prettyPrintCensor() throws Exception {
        CensoringJsonGeneratorDecorator decorator = new CensoringPrettyPrintingJsonGeneratorDecorator();
        Context context = new LoggerContext();
        Config config = loadConfig();
        context.putObject(CensorConstants.TYPESAFE_CONFIG_CTX_KEY, config);
        decorator.setContext(context);
        decorator.start();

        StringWriter writer = new StringWriter();
        JsonFactory factory = new MappingJsonFactory();
        JsonGenerator generator = decorator.decorate(factory.createGenerator(writer));

        generator.writeStartObject();
        generator.writeStringField("message", "My hunter2 message");
        generator.writeEndObject();
        generator.flush();

        assertThat(writer.toString()).isEqualTo("{\n  \"message\" : \"My ******* message\"\n}");
    }

}
