package com.tersesystems.logback.bytebuddy;

import java.util.Arrays;
import java.util.Objects;

class MethodInfo {
    final String methodName;
    final String descriptor;
    final String[] exceptions;
    final String source;
    private int startLine;
    private int endLine;

    MethodInfo(String methodName, String descriptor, String[] exceptions, String source) {
        this.methodName = Objects.requireNonNull(methodName);
        this.descriptor = descriptor;
        this.exceptions = exceptions;
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
                ", exceptions=" + Arrays.toString(exceptions) +
                ", source='" + source + '\'' +
                '}';
    }
}
