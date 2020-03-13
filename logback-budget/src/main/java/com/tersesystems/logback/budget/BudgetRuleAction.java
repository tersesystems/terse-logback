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

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;

public class BudgetRuleAction extends Action {

  private BudgetRule budgetRule;
  private boolean inError = false;

  @Override
  public void begin(InterpretationContext ic, String localName, Attributes attributes)
      throws ActionException {
    Object o = ic.peekObject();

    if (!(o instanceof BudgetRuleAttachable)) {
      String errMsg =
          "Could not find a BudgetRuleAttachable at the top of execution stack. Near ["
              + localName
              + "] line "
              + getLineNumber(ic);
      inError = true;
      addInfo(errMsg); // This can trigger in an "if" block from janino, so it may not be serious...
      return;
    }

    BudgetRuleAttachable budgetRuleAttachable = (BudgetRuleAttachable) o;

    String className = attributes.getValue(CLASS_ATTRIBUTE);
    if (OptionHelper.isEmpty(className)) {
      className = BudgetRule.class.getName();
    }

    String name = attributes.getValue(NAME_ATTRIBUTE);
    long interval = Long.parseLong(attributes.getValue("interval"));
    int threshold = Integer.parseInt(attributes.getValue("threshold"));
    String timeUnit = attributes.getValue("timeUnit");

    try {
      addInfo("About to instantiate budgetRule of type [" + className + "]");
      budgetRule =
          (BudgetRule) OptionHelper.instantiateByClassName(className, BudgetRule.class, context);
      budgetRule.setName(name);
      budgetRule.setInterval(interval);
      budgetRule.setThreshold(threshold);
      budgetRule.setTimeUnit(timeUnit);

      ic.pushObject(budgetRule);
    } catch (Exception oops) {
      inError = true;
      addError("Could not create budgetRule.", oops);
      throw new ActionException(oops);
    }
    budgetRuleAttachable.addBudgetRule(budgetRule);
  }

  @Override
  public void end(InterpretationContext ic, String name) throws ActionException {
    if (inError) {
      return;
    }

    Object o = ic.peekObject();
    if (o != budgetRule) {
      addWarn("The object at the end of the stack is not the budgetRule pushed earlier.");
    } else {
      ic.popObject();
    }
  }
}
