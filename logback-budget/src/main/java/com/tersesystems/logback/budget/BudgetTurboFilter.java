package com.tersesystems.logback.budget;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.concurrent.CircuitBreaker;
import org.apache.commons.lang3.concurrent.EventCountCircuitBreaker;
import org.slf4j.Marker;

public class BudgetTurboFilter extends TurboFilter implements BudgetRuleAttachable {

  private List<BudgetRule> budgetRules = new ArrayList<>();
  private Map<String, CircuitBreaker<Integer>> levelRules = new HashMap<>();

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
  public FilterReply decide(
      Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
    if (levelRules.isEmpty()) {
      return getOnMatch(); // not applicable
    }

    CircuitBreaker<Integer> breaker = levelRules.get(level.levelStr);
    if (breaker == null) {
      return getOnMatch(); // does not apply to this level
    }

    if (breaker.checkState()) {
      return breaker.incrementAndCheckState(1) ? getOnMatch() : getOnMismatch();
    } else {
      return getOnMismatch();
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

  protected FilterReply onMatch = FilterReply.NEUTRAL;
  protected FilterReply onMismatch = FilterReply.DENY;

  public final void setOnMatch(FilterReply reply) {
    this.onMatch = reply;
  }

  public final void setOnMismatch(FilterReply reply) {
    this.onMismatch = reply;
  }

  public final FilterReply getOnMatch() {
    return onMatch;
  }

  public final FilterReply getOnMismatch() {
    return onMismatch;
  }
}
