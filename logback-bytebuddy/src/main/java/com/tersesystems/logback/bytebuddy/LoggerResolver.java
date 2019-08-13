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

import org.slf4j.Logger;

/**
 * Finds a logger given some input.
 */
public interface LoggerResolver {
    /**
     * @param origin the class name plus other stuff, provided from bytebuddy advice.
     * @return a logger.
     */
    Logger resolve(String origin);
}
