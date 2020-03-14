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

import static com.tersesystems.logback.ringbuffer.RingBufferConstants.REF_ATTRIBUTE;
import static com.tersesystems.logback.ringbuffer.RingBufferConstants.RINGBUFFER_BAG;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.OptionHelper;
import java.util.Map;
import org.xml.sax.Attributes;

public class RingBufferRefAction extends Action {
  boolean inError = false;

  @SuppressWarnings("unchecked")
  public void begin(InterpretationContext ec, String tagName, Attributes attributes) {
    // Let us forget about previous errors (in this object)
    inError = false;

    // logger.debug("begin called");

    Object o = ec.peekObject();

    if (!(o instanceof RingBufferAttachable)) {
      String errMsg =
          "Could not find an RingBufferAttachable at the top of execution stack. Near ["
              + tagName
              + "] line "
              + getLineNumber(ec);
      inError = true;
      addInfo(errMsg); // This can trigger in an "if" block from janino, so it may not be serious...
      return;
    }

    RingBufferAttachable ringBufferAttachable = (RingBufferAttachable) o;

    String ringbufferName = ec.subst(attributes.getValue(REF_ATTRIBUTE));

    if (OptionHelper.isEmpty(ringbufferName)) {
      // print a meaningful error message and return
      String errMsg = "Missing ringbuffer ref attribute in <ringbuffer-ref> tag.";
      inError = true;
      addError(errMsg);
      return;
    }

    Map<String, RingBufferContextAware> ringBufferMap =
        (Map<String, RingBufferContextAware>) ec.getObjectMap().get(RINGBUFFER_BAG);
    RingBufferContextAware ringBuffer = ringBufferMap.get(ringbufferName);

    if (ringBuffer == null) {
      String msg =
          "Could not find an ringBuffer named ["
              + ringbufferName
              + "]. Did you define it below instead of above in the configuration file?";
      inError = true;
      addError(msg);
      return;
    }

    addInfo(
        "Attaching ringBuffer named ["
            + ringbufferName
            + "] to "
            + ringBufferAttachable
            + "at "
            + getLineNumber(ec));
    ringBufferAttachable.setRingBuffer(ringBuffer);
  }

  public void end(InterpretationContext ec, String n) {}
}
