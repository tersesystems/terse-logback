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
 * Borrowed from securityfixer, showing tracing when you set the security manager.
 *
 * <p>Will not work on native methods, i.e. `System.currentTimeMillis`.
 *
 * <p>Move this into the main source path and redeploy if you want to test (I can't figure out how
 * to do agent stuff in Gradle)
 */
public class PreloadedInstrumentationExample {
  public static void main(String[] args) throws Exception {
    Thread thread = Thread.currentThread();
    thread.run();
  }
}
