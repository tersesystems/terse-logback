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
package com.tersesystems.logback.budget;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.boolex.EventEvaluatorBase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.concurrent.CircuitBreaker;
import org.apache.commons.lang3.concurrent.EventCountCircuitBreaker;

/** Returns true if logging the event is within budget, false otherwise. */
public class BudgetEvaluator extends EventEvaluatorBase<ILoggingEvent>
    implements BudgetRuleAttachable {

  private List<BudgetRule> budgetRules = new ArrayList<>();
  private Map<String, CircuitBreaker<Integer>> levelRules = new HashMap<>();

  @Override
  public void start() {
    for (BudgetRule budgetRule : budgetRules) {
      CircuitBreaker<Integer> breaker = createCircuitBreaker(budgetRule);
      levelRules.put(budgetRule.getName(), breaker);
    }
    super.start();
  }

  private CircuitBreaker<Integer> createCircuitBreaker(BudgetRule budgetRule) {
    addInfo("budgetRule = " + budgetRule);
    final int threshold = budgetRule.getThreshold();
    final long checkInterval = budgetRule.getInterval();
    String timeUnit = budgetRule.getTimeUnit();
    if (timeUnit == null) {
      addError("No time unit found for budget rule");
      throw new IllegalStateException("No time unit found for budget rule " + budgetRule);
    } else {
      TimeUnit checkUnit;
      try {
        checkUnit = TimeUnit.valueOf(timeUnit.toUpperCase());
      } catch (IllegalArgumentException iae) {
        try {
          // Try adding an S on the end
          checkUnit = TimeUnit.valueOf(timeUnit.toUpperCase() + "S");
        } catch (Exception e) {
          addError(
              "Invalid time unit found for budget rule, use java.util.concurrent.TimeUnit enums "
                  + budgetRule,
              e);
          throw e;
        }
      }
      return new EventCountCircuitBreaker(threshold, checkInterval, checkUnit);
    }
  }

  @Override
  public boolean evaluate(ILoggingEvent event) {
    if (levelRules.isEmpty()) {
      return true; // not applicable
    }

    Level level = event.getLevel();
    CircuitBreaker<Integer> breaker = levelRules.get(level.levelStr);
    if (breaker == null) {
      return true; // does not apply to this level
    }

    if (breaker.checkState()) {
      return breaker.incrementAndCheckState(1);
    } else {
      return false;
    }
  }

  @Override
  public void addBudgetRule(BudgetRule budget) {
    budgetRules.add(budget);
  }

  @Override
  public void clearAllBudgetRules() {
    budgetRules.clear();
  }
}
