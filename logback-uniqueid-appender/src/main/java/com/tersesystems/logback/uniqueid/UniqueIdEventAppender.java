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
package com.tersesystems.logback.uniqueid;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.tersesystems.logback.core.DecoratingAppender;

public class UniqueIdEventAppender extends DecoratingAppender<ILoggingEvent, IUniqueIdLoggingEvent> {

    private IdGenerator idGenerator = new FlakeIdGenerator();

    public IdGenerator getIdGenerator() {
        return idGenerator;
    }

    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    protected IUniqueIdLoggingEvent decorateEvent(ILoggingEvent eventObject) {
        return new UniqueIdLoggingEvent(eventObject, idGenerator.generateId());
    }

}

