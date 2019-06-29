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
package com.tersesystems.logback.classic;

import ch.qos.logback.classic.pattern.ThrowableHandlingConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Exception message converter that only prints out the messages of the nested exception.
 *
 * The first argument is the amount of leading whitespace to add before the exception.
 *
 * The second argument is the maximum depth of the nested exceptions.
 *
 * The third, fourth, and fifth arguments are the prefix, separator, and suffix, respectively.
 *
 * Use in a pattern encoder, i.e. "%exmessage{1, 10, cause=[}"
 */
public class ExceptionMessageConverter extends ThrowableHandlingConverter {

    @Override
    public String convert(ILoggingEvent event) {
        Integer whitespace = getLeadingWhitespace();
        if (whitespace < 0) {
            addWarn("Cannot render whitespace less than 0!");
            whitespace = 0;
        }

        Integer depth = getDepth();
        if (depth < 1) {
            addWarn("Cannot render depth less than 1!");
            depth = 1;
        }
        String prefix = getPrefix();
        String sep = getSeparator();
        String suffix = getSuffix();
        IThrowableProxy ex = event.getThrowableProxy();
        if (ex == null) {
            return "";
        }
        return processException(ex, whitespace, depth, prefix, sep, suffix);
    }

    private Integer getLeadingWhitespace() {
        return Integer.parseInt(getOption(0).orElse("1"));
    }

    protected Integer getDepth() {
        return Integer.parseInt(getOption(1).orElse("10"));
    }

    protected String getPrefix() {
        return getOption(2).orElse("[");
    }

    protected String getSeparator() {
        return getOption(3).orElse(" > ");
    }

    protected String getSuffix() {
        return getOption(4).orElse("]");
    }

    protected Optional<String> getOption(int index) {
        List<String> optionList = getOptionList();
        if (optionList != null && optionList.size() >= index + 1) {
            return Optional.of(optionList.get(index));
        }
        return Optional.empty();
    }

    protected String processException(IThrowableProxy throwableProxy,
                                      Integer whitespace,
                                      Integer depth,
                                      String prefix, String sep, String suffix) {
        String ws = String.join("", Collections.nCopies(whitespace, " "));
        StringBuilder b = new StringBuilder(ws + prefix);
        IThrowableProxy ex = throwableProxy;
        for (int i = 0; i < depth; i++) {
            b.append(ex.getMessage());
            ex = ex.getCause();
            if (ex == null || i + 1 == depth) break;
            b.append(sep);
        }
        b.append(suffix);
        return b.toString();
    }

}
