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

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.typesafe.config.Config;

import java.util.List;

/**
 * Censoring message converter for text.
 *
 * Note that this does not filter out marker text or additional information related to the event,
 * i.e. it does not filter out exception text.
 */
public class CensoringMessageConverter extends ClassicConverter {

    private Censor censor;

    public boolean isEnabled() {
        Config config = (Config) getContext().getObject(CensorConstants.TYPESAFE_CONFIG_CTX_KEY);
        return config.getBoolean(CensorConstants.CENSOR_TEXT_ENABLED);
    }

    @Override
    public void start() {
        Config config = (Config) getContext().getObject(CensorConstants.TYPESAFE_CONFIG_CTX_KEY);
        String replacementText = config.getString(CensorConstants.CENSOR_TEXT_REPLACEMENT);
        List<String> regexes = config.getStringList(CensorConstants.CENSOR_TEXT_REGEX);
        this.censor = new RegexCensor(regexes, replacementText);
        started = true;
    }

    @Override
    public String convert(ILoggingEvent event) {
        String formattedMessage = event.getFormattedMessage();
        if (isEnabled() && isStarted()) {
            return String.valueOf(censor.apply(formattedMessage));
        } else {
            return formattedMessage;
        }
    }
}
