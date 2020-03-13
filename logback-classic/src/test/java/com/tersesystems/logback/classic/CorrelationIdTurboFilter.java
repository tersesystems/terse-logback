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

package com.tersesystems.logback.classic;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import com.tersesystems.logback.core.StreamUtils;
import java.util.Map;
import java.util.stream.Stream;
import org.slf4j.Marker;

/** Tells the tap filter to create an event and append it if a correlation id is found. */
public class CorrelationIdTurboFilter extends TurboFilter {
  private String mdcKey = "correlation_id";
  private Utils utils;

  public String getMdcKey() {
    return mdcKey;
  }

  public void setMdcKey(String mdcKey) {
    this.mdcKey = mdcKey;
  }

  @Override
  public void start() {
    super.start();
    utils = Utils.create((LoggerContext) getContext());
  }

  @Override
  public FilterReply decide(
      Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
    // If there's a correlation id marker somewhere in the hierarchy, then good.
    Stream<Marker> markerStream = StreamUtils.fromMarker(marker);
    if (markerStream.anyMatch(m -> m instanceof CorrelationIdMarker)) {
      return FilterReply.ACCEPT;
    }

    // Look in MDC for a correlation id as well...
    Map<String, String> mdcPropertyMap = utils.getMDCPropertyMap();
    String mdcKey = getMdcKey();
    if (mdcKey != null) {
      if (mdcPropertyMap.containsKey(mdcKey)) {
        return FilterReply.ACCEPT;
      }
    }

    // Otherwise no.
    return FilterReply.DENY;
  }
}
