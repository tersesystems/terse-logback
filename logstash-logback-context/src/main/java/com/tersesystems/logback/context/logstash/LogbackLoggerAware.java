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
package com.tersesystems.logback.context.logstash;

import java.util.Optional;

/**
 * Helper class for getting at the logback logger.
 */
public interface LogbackLoggerAware {
    Optional<ch.qos.logback.classic.Logger> getLogbackLogger();
}
