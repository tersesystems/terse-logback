package com.tersesystems.logback.censor;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Context;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.Test;

import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;

public class DecoratorTest {

    @Test
    public void basicCensor() throws Exception {
        CensoringJsonGeneratorDecorator decorator = new CensoringJsonGeneratorDecorator();
        Context context = new LoggerContext();
        Config config = ConfigFactory.load();
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
    public void prettyPrintCensor() throws Exception {
        CensoringJsonGeneratorDecorator decorator = new CensoringPrettyPrintingJsonGeneratorDecorator();
        Context context = new LoggerContext();
        Config config = ConfigFactory.load();
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
