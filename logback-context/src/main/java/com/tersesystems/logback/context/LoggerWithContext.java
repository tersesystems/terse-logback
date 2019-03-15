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
package com.tersesystems.logback.context;

import org.slf4j.Logger;
import org.slf4j.Marker;

public interface LoggerWithContext<
        MarkerT extends Marker,
        ContextT extends Context<MarkerT, ContextT>,
        SelfT> extends Logger, ContextAware<MarkerT, ContextT> {

     SelfT withContext(ContextT context);
}
