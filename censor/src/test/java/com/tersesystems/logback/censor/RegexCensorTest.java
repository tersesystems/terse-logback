package com.tersesystems.logback.censor;

import com.typesafe.config.Config;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RegexCensorTest extends AbstractConfigBase {

    @Test
    public void testCensor() throws Exception {
        Config config = loadConfig();
        String regexPath = CensorConstants.CENSOR_TEXT_REGEX;
        String replacementTextPath = CensorConstants.CENSOR_TEXT_REPLACEMENT;
        RegexCensor censor = new RegexCensor(config, regexPath, replacementTextPath);
        assertThat(censor.apply("hunter2")).isEqualTo("*******");
    }

    @Test
    public void testCensorWithNoMatch() throws Exception {
        Config config = loadConfig();
        String regexPath = CensorConstants.CENSOR_TEXT_REGEX;
        String replacementTextPath = CensorConstants.CENSOR_TEXT_REPLACEMENT;
        RegexCensor censor = new RegexCensor(config, regexPath, replacementTextPath);
        assertThat(censor.apply("password1")).isEqualTo("password1");
    }
}
