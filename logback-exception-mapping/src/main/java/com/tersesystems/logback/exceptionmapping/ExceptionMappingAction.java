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
package com.tersesystems.logback.exceptionmapping;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.OptionHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import org.xml.sax.Attributes;

public class ExceptionMappingAction extends Action {

  Consumer<Exception> handler = e -> addError("Cannot map exception", e);
  boolean inError = false;

  @SuppressWarnings("unchecked")
  public void begin(InterpretationContext ec, String tagName, Attributes attributes) {
    // Let us forget about previous errors (in this object)
    inError = false;

    // logger.debug("begin called");
    Object o = ec.peekObject();

    if (!(o instanceof ExceptionMappingRegistry)) {
      String errMsg =
          "Could not find a registry at the top of execution stack. Near ["
              + tagName
              + "] line "
              + getLineNumber(ec);
      inError = true;
      addInfo(errMsg); // This can trigger in an "if" block from janino, so it may not be serious...
      return;
    }

    ExceptionMappingRegistry registry = (ExceptionMappingRegistry) o;
    String mappingName = ec.subst(attributes.getValue("name"));
    String properties = ec.subst(attributes.getValue("properties"));

    if (OptionHelper.isEmpty(mappingName)) {
      // print a meaningful error message and return
      String errMsg = "Missing name attribute in tag.";
      inError = true;
      addError(errMsg);
      return;
    }

    if (OptionHelper.isEmpty(properties)) {
      // print a meaningful error message and return
      String errMsg = "Missing properties attribute in tag.";
      inError = true;
      addError(errMsg);
      return;
    }

    List<String> mappingPropertyNames = new ArrayList<String>(Arrays.asList(properties.split(",")));
    ExceptionMapping newMapping =
        new BeanExceptionMapping(mappingName, mappingPropertyNames, handler);

    addInfo(
        "Attaching mapping named [" + mappingName + "] to " + registry + "at " + getLineNumber(ec));
    registry.register(newMapping);
  }

  public void end(InterpretationContext ec, String n) {}
}
