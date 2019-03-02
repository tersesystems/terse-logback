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

/**
 * Censoring message converter for text.
 *
 * Note that this does not filter out marker text or additional information related to the event,
 * i.e. it does not filter out exception text.
 */
public class CensoringMessageConverter extends ClassicConverter {

    private Censor censor;

    @Override
    public void start() {
        Config config = (Config) getContext().getObject(CensorConstants.TYPESAFE_CONFIG_CTX_KEY);
        this.censor = new RegexCensor(config, CensorConstants.CENSOR_TEXT_REGEX, CensorConstants.CENSOR_TEXT_REPLACEMENT);
        started = true;
    }


    @Override
    public String convert(ILoggingEvent event) {
        String formattedMessage = event.getFormattedMessage();
        if (censor != null) {
            return String.valueOf(censor.apply(formattedMessage));
        } else {
            return formattedMessage;
        }
    }
}
