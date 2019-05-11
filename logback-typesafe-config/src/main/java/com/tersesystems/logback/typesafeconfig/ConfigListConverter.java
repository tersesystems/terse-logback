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
package com.tersesystems.logback.typesafeconfig;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.typesafe.config.*;

import java.util.List;

/**
 * Queries a list in typesafe config by specifying the full path and index.
 *
 * This is a means of working around <a href="https://github.com/lightbend/config/issues/30">#30</a>.
 *
 * You must have a typesafe config in context, usually through typesafeConfigAction.
 *
 * <pre>{@code
 *  <conversionRule conversionWord="configList"
 *    converterClass="com.tersesystems.logback.typesafeconfig.ConfigListConverter" />
 * }</pre>
 *
 * And then define the option list in the layout as the path and the index:
 *
 * <pre>{@code
 *  %configList{some.property.array,2}
 * }</pre>
 */
public class ConfigListConverter extends ClassicConverter {
    @Override
    public String convert(ILoggingEvent event) {

        List<String> options = getOptionList();
        String path = options.get(0);
        String index = options.get(1);
        try {
            Config config = (Config) getContext().getObject(ConfigConstants.TYPESAFE_CONFIG_CTX_KEY);
            if (path == null) {
                addError("No option found - you must specify property as %config{some.property.array,0} ");
                return "%PARSER_ERROR";
            }

            ConfigList configList = config.getList(path);
            ConfigValue configValue = configList.get(Integer.parseInt(index));
            return configValue.unwrapped().toString();
        } catch (ConfigException e) {
            addError(String.format("Exception rendering path %s, index %s, exception %s", path, index, e.getMessage()));
            return "%PARSER_ERROR";
        }
    }
}
