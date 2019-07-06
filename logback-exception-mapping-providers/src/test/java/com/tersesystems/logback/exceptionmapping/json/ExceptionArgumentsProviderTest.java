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
package com.tersesystems.logback.exceptionmapping.json;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.status.OnConsoleStatusListener;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.tersesystems.logback.exceptionmapping.DefaultExceptionMappingRegistry;
import com.tersesystems.logback.exceptionmapping.ExceptionMappingRegistry;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.function.Consumer;

import static com.tersesystems.logback.exceptionmapping.Constants.DEFAULT_MAPPINGS_KEY;
import static com.tersesystems.logback.exceptionmapping.Constants.REGISTRY_BAG;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

public class ExceptionArgumentsProviderTest {

    @Test
    public void testProvider() throws IOException {
        LoggerContext context = new LoggerContext();
        context.getStatusManager().add(new OnConsoleStatusListener());
        createExceptionMappingRegistry(context);

        StringWriter writer = new StringWriter();
        JsonGenerator g = mkJsonGenerator(writer);

        ExceptionArgumentsProvider provider = new ExceptionArgumentsProvider();
        provider.setContext(context);
        provider.setFieldName("exception");
        provider.start();

        ILoggingEvent event = mkLoggingEvent(context);

        g.writeStartObject();
        provider.writeTo(g, event);
        g.writeEndObject();
        g.flush();
        g.close();

        String s = writer.toString();
        assertThat(s).isEqualTo("{\"exception\":[{\"name\":\"java.lang.RuntimeException\",\"properties\":{\"message\":\"derp\"}}]}");
    }

    private void createExceptionMappingRegistry(LoggerContext context) {
        Consumer<Exception> handler = Throwable::printStackTrace;
        ExceptionMappingRegistry registry = new DefaultExceptionMappingRegistry(handler);
        registry.register(Throwable.class.getName(), "message");
        context.putObject(REGISTRY_BAG, singletonMap(DEFAULT_MAPPINGS_KEY, registry));
    }

    private ILoggingEvent mkLoggingEvent(LoggerContext context) {
        Exception ex = new RuntimeException("derp");
        return new LoggingEvent("fcqn", context.getLogger("fcqn"), Level.INFO, "info", ex, null);
    }

    private JsonGenerator mkJsonGenerator(StringWriter writer) throws IOException {
        MappingJsonFactory jsonFactory = new MappingJsonFactory();
        JsonGenerator g = jsonFactory.createGenerator(writer);
        g.enable(JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION);
        return g;
    }
}
