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

package com.tersesystems.logback.uniqueid;

import com.tersesystems.logback.core.Component;

/** This interface returns a unique id identifying the entity. */
public interface UniqueIdProvider extends Component {
  String uniqueId();
}
