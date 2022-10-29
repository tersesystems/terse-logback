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
package com.tersesystems.logback.censor;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.OptionHelper;
import java.util.Map;
import org.xml.sax.Attributes;

public class CensorRefAction extends Action {
  boolean inError = false;

  @SuppressWarnings("unchecked")
  public void begin(InterpretationContext ec, String tagName, Attributes attributes) {
    // Let us forget about previous errors (in this object)
    inError = false;

    // logger.debug("begin called");

    Object o = ec.peekObject();

    if (!(o instanceof CensorAttachable)) {
      String errMsg =
          "Could not find an CensorAttachable at the top of execution stack. Near ["
              + tagName
              + "] line "
              + getLineNumber(ec);
      inError = true;
      addInfo(errMsg); // This can trigger in an "if" block from janino, so it may not be serious...
      return;
    }

    CensorAttachable censorAttachable = (CensorAttachable) o;

    String censorName = ec.subst(attributes.getValue(CensorConstants.REF_ATTRIBUTE));

    if (OptionHelper.isEmpty(censorName)) {
      // print a meaningful error message and return
      String errMsg = "Missing censor ref attribute in <censor-ref> tag.";
      inError = true;
      addError(errMsg);
      return;
    }

    Map<String, CensorContextAware> censorBag =
        (Map<String, CensorContextAware>) ec.getObjectMap().get(CensorConstants.CENSOR_BAG);
    CensorContextAware censor = censorBag.get(censorName);

    if (censor == null) {
      String msg =
          "Could not find an censor named ["
              + censorName
              + "]. Did you define it below instead of above in the configuration file?";
      inError = true;
      addError(msg);
      return;
    }

    addInfo(
        "Attaching censor named ["
            + censorName
            + "] to "
            + censorAttachable
            + "at "
            + getLineNumber(ec));
    censorAttachable.addCensor(censor);
  }

  public void end(InterpretationContext ec, String n) {}
}
