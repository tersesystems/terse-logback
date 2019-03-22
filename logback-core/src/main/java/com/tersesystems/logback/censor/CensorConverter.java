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

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.CompositeConverter;

/**
 * Censoring message converter for text.
 *
 * Note that this does not filter out marker text or additional information related to the event,
 * i.e. it does not filter out exception text.
 */
public class CensorConverter extends CompositeConverter<ILoggingEvent> {

    private Censor censor;

    public String transform(ILoggingEvent event, String in) {
        if (censor == null) {
            censor = (Censor) getContext().getObject("censor");
        }
        return String.valueOf(censor.censorText(in));
    }

    public void setCensor(Censor censor) {
        this.censor = censor;
    }
}
