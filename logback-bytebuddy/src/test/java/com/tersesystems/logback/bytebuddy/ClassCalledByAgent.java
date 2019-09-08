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
package com.tersesystems.logback.bytebuddy;

/**
 * This class does no logging.
 */
public class ClassCalledByAgent {
    public void printStatement() {
        System.out.println("I am a simple println method with no logging");
    }

    public void printArgument(String arg) {
        System.out.println("I am a simple println, printing " + arg);
    }

    public void throwException(String arg) {
        throw new RuntimeException("I'm a squirrel!");
    }
}
