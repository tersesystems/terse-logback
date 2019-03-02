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
package example;

import org.slf4j.Logger;

import static net.logstash.logback.argument.StructuredArguments.*;
import static org.slf4j.LoggerFactory.*;

public class ClassWithStructuredArguments {
    private final Logger logger = getLogger(getClass());

    public void logValue(String correlationId) {
        if (logger.isInfoEnabled()) {
            logger.info("id is {}", value("correlationId", correlationId));
        }
    }

    public void logNameAndValue(String correlationId) {
        logger.info("id is {}", keyValue("correlationId", correlationId));
    }

    public void logNameAndValueWithFormat(String correlationId) {
        logger.info("id is {}", keyValue("correlationId", correlationId, "{0}=[{1}]"));
    }

    public void doThings(String correlationId) {
        logValue(correlationId);
        logNameAndValue(correlationId);
        logNameAndValueWithFormat(correlationId);
    }

    public static void main(String[] args) {
        String correlationId = IdGenerator.getInstance().generateCorrelationId();
        ClassWithStructuredArguments classWithStructuredArguments = new ClassWithStructuredArguments();
        classWithStructuredArguments.doThings(correlationId);
    }
}