/*
 * SPDX-License-Identifier: CC0-1.0
 *
 * Copyright 2018-2020 Will Sargent.
 *
 * Licensed under the CC0 Public Domain Dedication;
 * You may obtain a copy of the License at
 *
 *  http://creativecommons.org/publicdomain/zero/1.0/
 */
package com.tersesystems.logback.postgresjson;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.joran.spi.JoranException;
import com.tersesystems.logback.classic.Utils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class PostgresJsonAppenderTest {

  @Disabled
  @Test
  public void testJson() throws JoranException, InterruptedException {

    Utils utils = Utils.create("/logback-postgres-json.xml");
    Logger logger1 = utils.getLogger("com.example.Test");
    logger1.info("THIS IS A TEST");

    Thread.sleep(1000);

    utils.getStatusList().forEach(System.out::println);
  }
}
