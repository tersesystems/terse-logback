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

import static com.tersesystems.logback.censor.CensorConstants.CENSOR_BAG;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.CompositeConverter;
import java.util.List;
import java.util.Map;

/**
 * Censoring message converter for text.
 *
 * <p>Note that this does not filter out marker text or additional information related to the event,
 * i.e. it does not filter out exception text.
 *
 * <pre>{@code
 * <conversionRule conversionWord="censor"
 *   converterClass="com.tersesystems.logback.censor.CensorConverter" />
 * }</pre>
 */
public class CensorConverter extends CompositeConverter<ILoggingEvent> {

  private CensorContextAware censor;

  @Override
  public void start() {
    super.start();
    // There isn't a good way of referring to other objects without going through
    // the context here, as the IC is not available to converters.
    Map<String, CensorContextAware> censorBag =
        (Map<String, CensorContextAware>) getContext().getObject(CENSOR_BAG);
    if (censorBag == null || censorBag.isEmpty()) {
      addError("Null or empty censor bag found in context!");
    }

    // The censor name is given in the pattern encoder in the form "%censor(%msg, censor-name)"
    // See logstash-logback-encoder for a more complex example:
    // https://github.com/logstash/logstash-logback-encoder/blob/master/src/main/java/net/logstash/logback/stacktrace/ShortenedThrowableConverter.java
    List<String> optionList = getOptionList();
    addInfo(String.format("Pulling options %s", optionList));
    String censorName = getFirstOption();
    if (censorName == null) {
      censorName = censorBag.keySet().iterator().next();
      addInfo(
          String.format("Pulling first censor name %s from censor bag converter: ", censorName));
    } else {
      addInfo(String.format("Referencing explicit censor name %s in converter: ", censorName));
    }

    censor = censorBag.get(censorName);
    if (censor == null) {
      addError(String.format("No censor with name %s found in censor bag!", censorName));
    }
  }
  //
  //    @Override
  //    public String convert(ILoggingEvent event) {
  //        return String.valueOf(censor.censorText(in));
  //    }

  @SuppressWarnings("unchecked")
  public String transform(ILoggingEvent event, String in) {
    return String.valueOf(censor.censorText(in));
  }
}
