package com.tersesystems.logback.budget;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import java.net.URL;
import org.junit.Test;

public class BudgetTurboFilterTest {

  @Test
  public void testBudget() throws JoranException, InterruptedException {
    LoggerContext context = new LoggerContext();

    URL resource = getClass().getResource("/logback-turbofilter.xml");
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
