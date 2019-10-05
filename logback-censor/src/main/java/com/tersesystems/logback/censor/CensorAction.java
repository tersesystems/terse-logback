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

import static com.tersesystems.logback.censor.CensorConstants.CENSOR_BAG;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.OptionHelper;
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.Attributes;

public class CensorAction extends Action {
  Censor censor;
  private boolean inError = false;

  @SuppressWarnings("unchecked")
  public void begin(InterpretationContext ic, String localName, Attributes attributes)
      throws ActionException {
    // We are just beginning, reset variables
    censor = null;
    inError = false;

    // Ensure idempotency of a CENSOR_BAG
    Map<String, Object> omap = ic.getObjectMap();
    if (!omap.containsKey(CENSOR_BAG)) {
      omap.put(CENSOR_BAG, new HashMap<String, Censor>());
    }

    String className = attributes.getValue(CLASS_ATTRIBUTE);
    if (OptionHelper.isEmpty(className)) {
      addError("Missing class name for censor. Near [" + localName + "] line " + getLineNumber(ic));
      inError = true;
      return;
    }

    try {
      addInfo("About to instantiate censor of type [" + className + "]");
      censor = (Censor) OptionHelper.instantiateByClassName(className, Censor.class, context);

      // XXX we can get the censor here but it still doesn't have the parameters we need.
      // OptionHelper.substVars()

      Context icContext = ic.getContext();
      if (censor != null) {
        censor.setContext(icContext);
      }

      String censorName = ic.subst(attributes.getValue(NAME_ATTRIBUTE));

      if (OptionHelper.isEmpty(censorName)) {
        addWarn("No censor name given for censor of type " + className + "].");
      } else {
        censor.setName(censorName);
        addInfo("Naming censor as [" + censorName + "]");
      }

      // The execution context contains a bag which contains the censors
      // created thus far.
      HashMap<String, Censor> censorBag =
          (HashMap<String, Censor>) ic.getObjectMap().get(CENSOR_BAG);
      getContext().putObject(CENSOR_BAG, censorBag);

      // add the censorText just created to the censorText bag.
      censorBag.put(censorName, censor);

      ic.pushObject(censor);
    } catch (Exception oops) {
      inError = true;
      addError("Could not create a Censor of type [" + className + "].", oops);
      throw new ActionException(oops);
    }
  }

  private void addConverter() {
    // Add a conversion rule automatically
    Map<String, String> ruleRegistry =
        (Map<String, String>) context.getObject(CoreConstants.PATTERN_RULE_REGISTRY);
    if (ruleRegistry == null) {
      ruleRegistry = new HashMap<String, String>();
      context.putObject(CoreConstants.PATTERN_RULE_REGISTRY, ruleRegistry);
    }
    ruleRegistry.putIfAbsent(CensorConstants.CENSOR_RULE_NAME, CensorConverter.class.getName());
  }

  /**
   * Once the children elements are also parsed, now is the time to activate the appender options.
   */
  public void end(InterpretationContext ec, String name) {
    if (inError) {
      return;
    }

    if (censor != null) {
      censor.start();
    }

    Object o = ec.peekObject();

    if (o != censor) {
      addWarn(
          "The object at the end of the stack is not the censor named ["
              + censor.getName()
              + "] pushed earlier.");
    } else {
      ec.popObject();
    }
  }
}
