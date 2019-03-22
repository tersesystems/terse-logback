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
package com.tersesystems.logback.censor;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RegexCensorTest {

    @Test
    public void testCensor() throws Exception {
        String replacementText = "*******";

        RegexCensor censor = new RegexCensor();
        censor.setReplacementText(replacementText);
        censor.addRegex("hunter2");
        censor.start();

        assertThat(censor.censorText("hunter2")).isEqualTo("*******");
    }

    @Test
    public void testCensorWithNoMatch() throws Exception {
        String replacementText = "*******";

        RegexCensor censor = new RegexCensor();
        censor.setReplacementText(replacementText);
        censor.addRegex("hunter2");
        ;
        censor.start();

        assertThat(censor.censorText("password1")).isEqualTo("password1");
    }
}
