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

import java.time.Instant;

public class MySpecialException extends Exception {

    private final Instant timestamp;

    public MySpecialException(String message, Instant timestamp) {
        super(message);
        this.timestamp = timestamp;
    }

    public MySpecialException(String message, Instant timestamp, Throwable cause) {
        super(message, cause);
        this.timestamp = timestamp;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
