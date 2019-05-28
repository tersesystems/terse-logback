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
package com.tersesystems.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.UUID;

public class UniqueIdEventAppender extends DecoratingAppender<ILoggingEvent, IUniqueIdLoggingEvent> {
    private final IdGenerator idGenerator = IdGenerator.getInstance();

    @Override
    protected IUniqueIdLoggingEvent decorateEvent(ILoggingEvent eventObject) {
        return new UniqueIdLoggingEvent(eventObject, idGenerator.generateId());
    }

    public static class UniqueIdLoggingEvent extends ProxyLoggingEvent implements IUniqueIdLoggingEvent {
        private final String uniqueId;
        UniqueIdLoggingEvent(ILoggingEvent delegate, String uniqueId) {
            super(delegate);
            this.uniqueId = uniqueId;
        }

        @Override
        public String uniqueId() {
            return this.uniqueId;
        }
    }

    static class IdGenerator {
        private IdGenerator() {
        }

        private static class SingletonHolder {
            static final IdGenerator instance = new IdGenerator();
        }

        public static IdGenerator getInstance() {
            return SingletonHolder.instance;
        }

        public String generateId() {
            return UUID.randomUUID().toString();
        }
    }
}

