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
package com.tersesystems.logback.bytebuddy.impl;

import java.security.cert.X509Certificate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SafeArguments {
    public List<String> apply(Object[] allArguments) {
       return Arrays.stream(allArguments).map(this::apply).collect(Collectors.toList());
    }

    public String apply(Object returnValue) {
        if (returnValue instanceof Collection<?>) {
            return parseCollection((Collection<Object>) returnValue);
        }

        if (returnValue instanceof Object[]) {
            return parseArray((Object[]) returnValue);
        }

        if (returnValue instanceof X509Certificate) {
            return parseCertificate((X509Certificate) returnValue);
        }

        return Objects.toString(returnValue);
    }

    private String parseCertificate(X509Certificate cert) {
        String s = cert.getSerialNumber().toString(16);
        String sub = cert.getSubjectDN().getName();
        return "X509Certificate(serialNumber = " + s + ", subject = " + sub + ")";
    }

    private String parseArray(Object[] returnValue) {
        return parseStream(Arrays.stream(returnValue));
    }

    private String parseCollection(Collection<Object> coll) {
        return parseStream(coll.stream());
    }

    private String parseStream(Stream<Object> stream) {
        return stream.map(this::apply).collect(Collectors.joining(","));
    }

}
