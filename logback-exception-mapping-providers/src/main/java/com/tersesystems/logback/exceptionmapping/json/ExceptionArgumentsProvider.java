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

import static com.tersesystems.logback.exceptionmapping.Constants.REGISTRY_BAG;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import com.fasterxml.jackson.core.JsonGenerator;
import com.tersesystems.logback.exceptionmapping.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.logstash.logback.composite.AbstractFieldJsonProvider;
import net.logstash.logback.composite.JsonWritingUtils;

public class ExceptionArgumentsProvider extends AbstractFieldJsonProvider<ILoggingEvent> {

  @SuppressWarnings("unchecked")
  private ExceptionMappingRegistry getRegistry() {
    final String key = Constants.DEFAULT_MAPPINGS_KEY;

    final Map<String, ExceptionMappingRegistry> mappingsBag =
        (Map<String, ExceptionMappingRegistry>) getContext().getObject(REGISTRY_BAG);

    if (mappingsBag == null) {
      addError("No mappingsRegistry bag found for converter!");
      return null;
    }

    ExceptionMappingRegistry exceptionMappingRegistry = mappingsBag.get(key);
    if (exceptionMappingRegistry == null) {
      addError("No mappingsRegistry found for converter for key " + key);
    }
    return exceptionMappingRegistry;
  }

  @Override
  public void writeTo(JsonGenerator generator, ILoggingEvent event) throws IOException {
    writeExceptionIfNecessary(generator, event.getThrowableProxy());
  }

  private void writeExceptionIfNecessary(JsonGenerator generator, IThrowableProxy throwableProxy)
      throws IOException {
    if (throwableProxy instanceof ThrowableProxy) {
      ExceptionMappingRegistry registry = getRegistry();
      if (registry == null) {
        addError("No registry found!");
        return;
      }
      ThrowableProxy proxy = (ThrowableProxy) throwableProxy;
      Throwable throwable = proxy.getThrowable();
      if (getFieldName() != null) {
        generator.writeArrayFieldStart(getFieldName());
      }

      ExceptionCauseIterator.create(throwable).stream()
          .forEach(
              t -> {
                try {
                  renderException(generator, registry, t);
                } catch (IOException e) {
                  addError("Cannot render exception", e);
                }
              });
      if (getFieldName() != null) {
        generator.writeEndArray();
      }
    }
  }

  private void renderException(
      JsonGenerator generator, ExceptionMappingRegistry registry, Throwable throwable)
      throws IOException {
    Map<String, String> propertyMap = new HashMap<>();
    List<ExceptionProperty> properties = registry.apply(throwable);

    for (ExceptionProperty property : properties) {
      if (property instanceof KeyValueExceptionProperty) {
        KeyValueExceptionProperty p = ((KeyValueExceptionProperty) property);
        propertyMap.put(p.getKey(), p.getValue());
      }
    }

    generator.writeStartObject();
    JsonWritingUtils.writeStringField(generator, "name", throwable.getClass().getName());
    JsonWritingUtils.writeMapStringFields(generator, "properties", propertyMap);
    generator.writeEndObject();
  }
}
