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
import ch.qos.logback.core.Context;
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

    @Override
    public void start() {
        this.censor = (Censor) getContext().getObject("censor");
        started = true;
        addInfo("started censoring message converter!");
    }

    @Override
    public String convert(ILoggingEvent event) {
        String formattedMessage = event.getFormattedMessage();
        return String.valueOf(censor.censorText(formattedMessage));
    }
}
