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

import com.typesafe.config.Config;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RegexCensorTest extends AbstractConfigBase {

    @Test
    public void testCensor() throws Exception {
        Config config = loadConfig();
        String replacementText = config.getString(CensorConstants.CENSOR_TEXT_REPLACEMENT);
        List<String> regexes = config.getStringList(CensorConstants.CENSOR_TEXT_REGEX);

        RegexCensor censor = new RegexCensor(regexes, replacementText);
        assertThat(censor.apply("hunter2")).isEqualTo("*******");
    }

    @Test
    public void testCensorWithNoMatch() throws Exception {
        Config config = loadConfig();
        String replacementText = config.getString(CensorConstants.CENSOR_TEXT_REPLACEMENT);
        List<String> regexes = config.getStringList(CensorConstants.CENSOR_TEXT_REGEX);
        RegexCensor censor = new RegexCensor(regexes, replacementText);
        assertThat(censor.apply("password1")).isEqualTo("password1");
    }
}
