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

import java.util.Arrays;
import java.util.Objects;

class MethodInfo {
    final String methodName;
    final String descriptor;
    final String source;
    private int startLine;
    private int endLine;

    MethodInfo(String methodName, String descriptor, String source) {
        this.methodName = Objects.requireNonNull(methodName);
        this.descriptor = descriptor;
        this.source = source;
    }

    public void setStartLine(int line) {
        this.startLine = line;
    }

    public void setEndLine(int line) {
        this.endLine = line;
    }

    public int getStartLine() {
        return startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    @Override
    public String toString() {
        return "MethodInfo{" +
                "methodName='" + methodName + '\'' +
                ", descriptor='" + descriptor + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}
