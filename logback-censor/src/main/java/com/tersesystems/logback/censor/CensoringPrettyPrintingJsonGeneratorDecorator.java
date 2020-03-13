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

import com.fasterxml.jackson.core.JsonGenerator;

public class CensoringPrettyPrintingJsonGeneratorDecorator extends CensoringJsonGeneratorDecorator
    implements CensorAttachable {
  @Override
  public JsonGenerator decorate(JsonGenerator generator) {
    return super.decorate(generator.useDefaultPrettyPrinter());
  }
}
