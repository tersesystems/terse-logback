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

import org.junit.Test;

public class ClassWithStructuredArgumentsTest {

    @Test
    public void logValue() {
        String correlationId = IdGenerator.getInstance().generateCorrelationId();
        ClassWithStructuredArguments classWithStructuredArguments = new ClassWithStructuredArguments();
        classWithStructuredArguments.logValue(correlationId);
    }

    @Test
    public void logNameAndValue() {
        String correlationId = IdGenerator.getInstance().generateCorrelationId();
        ClassWithStructuredArguments classWithStructuredArguments = new ClassWithStructuredArguments();
        classWithStructuredArguments.logNameAndValue(correlationId);
    }

    @Test
    public void logNameAndValueWithFormat() {
        String correlationId = IdGenerator.getInstance().generateCorrelationId();
        ClassWithStructuredArguments classWithStructuredArguments = new ClassWithStructuredArguments();
        classWithStructuredArguments.logNameAndValueWithFormat(correlationId);
    }
}
