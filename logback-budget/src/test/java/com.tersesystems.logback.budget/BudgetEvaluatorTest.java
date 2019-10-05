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
package com.tersesystems.logback.budget;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import java.net.URL;
import org.junit.Test;

public class BudgetEvaluatorTest {

  @Test
  public void testBudget() throws JoranException, InterruptedException {
    LoggerContext context = new LoggerContext();

    URL resource = getClass().getResource("/logback-budget.xml");
    JoranConfigurator configurator = new JoranConfigurator();
    configurator.setContext(context);
    configurator.doConfigure(resource);

    Logger logger = context.getLogger("some.random.Logger");

    for (int i = 0; i < 10; i++) {
      logger.info("Hello world");
    }
    Thread.sleep(1000);

    logger.info("Hello world");
  }
}
