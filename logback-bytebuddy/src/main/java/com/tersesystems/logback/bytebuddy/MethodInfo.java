package com.tersesystems.logback.bytebuddy;

import java.util.Arrays;
import java.util.Objects;

class MethodInfo {
    final int modifiers;
    final String internalName;
    final String descriptor;
    final String signature;
    final String[] exceptions;
    final int line;
    final String debug;
    final String source;
    final String declaringType;

    MethodInfo(int modifiers, String internalName, String descriptor, String signature, String[] exceptions,
               String declaringType,
               String source, String debug, int line) {
        this.modifiers = modifiers;
        this.internalName = internalName;
        this.descriptor = descriptor;
        this.signature = signature;
        this.exceptions = exceptions;
        this.declaringType = declaringType;
        this.source = source;
        this.debug = debug;
        this.line = line;
    }

    @Override
    public String toString() {
        return "MethodInfo{" +
                "modifiers=" + modifiers +
                ", internalName='" + internalName + '\'' +
                ", descriptor='" + descriptor + '\'' +
                ", signature='" + signature + '\'' +
                ", exceptions=" + Arrays.toString(exceptions) +
                ", line=" + line +
                ", debug='" + debug + '\'' +
                ", source='" + source + '\'' +
                ", declaringType='" + declaringType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodInfo that = (MethodInfo) o;
        return modifiers == that.modifiers &&
                line == that.line &&
                Objects.equals(internalName, that.internalName) &&
                Objects.equals(descriptor, that.descriptor) &&
                Objects.equals(signature, that.signature) &&
                Arrays.equals(exceptions, that.exceptions) &&
                Objects.equals(debug, that.debug) &&
                Objects.equals(source, that.source) &&
                Objects.equals(declaringType, that.declaringType);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(modifiers, internalName, descriptor, signature, line, debug, source, declaringType);
        result = 31 * result + Arrays.hashCode(exceptions);
        return result;
    }
}
