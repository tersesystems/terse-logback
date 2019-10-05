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
package com.tersesystems.logback.classic;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.turbo.MDCFilter;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.junit.jupiter.api.Test;
import org.slf4j.Marker;

public class UtilsTest {

  static class FancyTurboFilter extends TurboFilter {
    @Override
    public FilterReply decide(
        Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
      return null;
    }
  }

  @Test
  public void testTurboFilterMatchingType() {
    LoggerContext loggerContext = new LoggerContext();
    FancyTurboFilter fancyTurboFilter = new FancyTurboFilter();
    fancyTurboFilter.setName("fancyTurboFilter");
    fancyTurboFilter.setContext(loggerContext);
    loggerContext.addTurboFilter(fancyTurboFilter);

    Utils utils = Utils.create(loggerContext);
    assertThat(utils.getTurboFilter(FancyTurboFilter.class, "fancyTurboFilter")).isNotEmpty();
  }

  @Test
  public void testTurboFilterNonMatchingType() {
    LoggerContext loggerContext = new LoggerContext();
    FancyTurboFilter fancyTurboFilter = new FancyTurboFilter();
    fancyTurboFilter.setName("fancyTurboFilter");
    fancyTurboFilter.setContext(loggerContext);
    loggerContext.addTurboFilter(fancyTurboFilter);

    Utils utils = Utils.create(loggerContext);
    assertThat(utils.getTurboFilter(MDCFilter.class, "fancyTurboFilter")).isEmpty();
  }
}
