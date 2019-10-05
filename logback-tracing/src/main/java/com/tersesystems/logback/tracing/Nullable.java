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
package com.tersesystems.logback.tracing;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * autovalue wants a Nullable but doesn't tell us from where.
 *
 * <p>anything will work, so defining one here.
 *
 * <p>https://github.com/google/auto/issues/283#issuecomment-337281043
 */
@Target(TYPE_USE)
@Retention(SOURCE)
@interface Nullable {}
