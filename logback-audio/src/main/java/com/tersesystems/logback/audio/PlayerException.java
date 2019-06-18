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
package com.tersesystems.logback.audio;

public class PlayerException extends RuntimeException {
    public PlayerException() {
    }

    public PlayerException(String s) {
        super(s);
    }

    public PlayerException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public PlayerException(Throwable throwable) {
        super(throwable);
    }

    public PlayerException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
