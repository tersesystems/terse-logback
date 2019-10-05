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
package com.tersesystems.logback.exceptionmapping;

import static com.tersesystems.logback.exceptionmapping.Constants.*;

import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import com.tersesystems.logback.classic.ExceptionMessageConverter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExceptionMessageWithMappingsConverter extends ExceptionMessageConverter {

  @Override
  protected String constructMessage(IThrowableProxy ex) {
    String className = ex.getClassName();
    String arguments = findArgumentMappings(ex);
    return String.format("%s(%s)", className, arguments);
  }

  private String getMappingsKey() {
    return DEFAULT_MAPPINGS_KEY;
  }

  private String findArgumentMappings(IThrowableProxy ex) {
    if (ex instanceof ThrowableProxy) {
      ExceptionMappingRegistry registry = getRegistry();
      if (registry == null) {
        return "";
      }
      Throwable throwable = ((ThrowableProxy) ex).getThrowable();
      return format(registry.apply(throwable));
    } else {
      return "";
    }
  }

  private String format(List<ExceptionProperty> args) {
    return args.stream()
        .map(
            arg -> {
              StringBuilder sb = new StringBuilder();
              StringBufferExceptionPropertyWriter exceptionPropertyWriter =
                  new StringBufferExceptionPropertyWriter(sb);
              exceptionPropertyWriter.write(arg);
              return sb.toString();
            })
        .collect(Collectors.joining(" "));
  }

  @SuppressWarnings("unchecked")
  private ExceptionMappingRegistry getRegistry() {
    String key = getMappingsKey();

    Map<String, ExceptionMappingRegistry> mappingsBag =
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

  class StringBufferExceptionPropertyWriter {
    private final StringBuilder sb;

    StringBufferExceptionPropertyWriter(StringBuilder sb) {
      this.sb = sb;
    }

    public void write(ExceptionProperty exceptionProperty) {
      if (exceptionProperty instanceof KeyValueExceptionProperty) {
        KeyValueExceptionProperty kv = (KeyValueExceptionProperty) exceptionProperty;
        sb.append(kv.getKey());
        sb.append("=");
        sb.append("\"");
        sb.append(kv.getValue());
        sb.append("\"");
      }
    }
  };
}
