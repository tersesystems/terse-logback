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
package com.tersesystems.logback.ringbuffer;

import static com.tersesystems.logback.ringbuffer.RingBufferConstants.RINGBUFFER_BAG;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.OptionHelper;
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.Attributes;

public class RingBufferAction extends Action {
  RingBuffer ringBuffer;
  private boolean inError = false;

  @SuppressWarnings("unchecked")
  public void begin(InterpretationContext ic, String localName, Attributes attributes)
      throws ActionException {
    // We are just beginning, reset variables
    ringBuffer = null;
    inError = false;

    // Ensure idempotency of a ring buffer bag
    Map<String, Object> omap = ic.getObjectMap();
    if (!omap.containsKey(RINGBUFFER_BAG)) {
      omap.put(RINGBUFFER_BAG, new HashMap<String, RingBuffer>());
    }

    String className = attributes.getValue(CLASS_ATTRIBUTE);
    if (OptionHelper.isEmpty(className)) {
      addInfo(
          "Missing class name for ringbuffer. Near [" + localName + "] line " + getLineNumber(ic));
      addInfo(
          "Defaulting to message passing ring buffer, near ["
              + localName
              + "] line "
              + getLineNumber(ic));
      className = MessagePassingQueueRingBuffer.class.getName();
    }

    try {
      addInfo("About to instantiate ringbuffer of type [" + className + "]");
      ringBuffer =
          (RingBuffer) OptionHelper.instantiateByClassName(className, RingBuffer.class, context);

      Context icContext = ic.getContext();
      if (ringBuffer != null) {
        ringBuffer.setContext(icContext);
      }

      String nameAttribute = ic.subst(attributes.getValue(NAME_ATTRIBUTE));

      if (OptionHelper.isEmpty(nameAttribute)) {
        addWarn("No name given for ringbuffer of type " + className + "].");
      } else {
        ringBuffer.setName(nameAttribute);
        addInfo("Naming ringbuffer as [" + nameAttribute + "]");
      }

      // The execution context contains a bag which contains the ringbuffer
      // created thus far.
      HashMap<String, RingBuffer> bag =
          (HashMap<String, RingBuffer>) ic.getObjectMap().get(RINGBUFFER_BAG);
      getContext().putObject(RINGBUFFER_BAG, bag);

      // add the ringbuffer just created to the bag.
      bag.put(nameAttribute, ringBuffer);

      ic.pushObject(ringBuffer);
    } catch (Exception oops) {
      inError = true;
      addError("Could not create a ringbuffer of type [" + className + "].", oops);
      throw new ActionException(oops);
    }
  }

  /**
   * Once the children elements are also parsed, now is the time to activate the appender options.
   */
  public void end(InterpretationContext ec, String name) {
    if (inError) {
      return;
    }

    if (ringBuffer != null) {
      ringBuffer.start();
    }

    Object o = ec.peekObject();

    if (o != ringBuffer) {
      addWarn(
          "The object at the end of the stack is not the ringbuffer named ["
              + ringBuffer.getName()
              + "] pushed earlier.");
    } else {
      ec.popObject();
    }
  }
}
