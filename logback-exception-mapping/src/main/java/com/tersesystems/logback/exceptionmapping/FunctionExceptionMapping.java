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
package com.tersesystems.logback.exceptionmapping;

import java.util.List;
import java.util.function.Function;

public class FunctionExceptionMapping implements ExceptionMapping {
    private final Function<Throwable, List<ExceptionProperty>> function;
    private final String name;

    public FunctionExceptionMapping(String name, Function<Throwable, List<ExceptionProperty>> f) {
        this.name = name;
        this.function = f;
    }

    @Override
    public List<ExceptionProperty> apply(Throwable e) {
        return function.apply(e);
    }

    @Override
    public String getName() {
        return name;
    }
}
