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
package com.tersesystems.logback.sigar;

import ch.qos.logback.core.spi.ContextAware;
import org.hyperic.sigar.Sigar;

import java.util.Optional;

import static com.tersesystems.logback.sigar.SigarConstants.SIGAR_CTX_KEY;

public interface SigarContextAware extends ContextAware {
    default Optional<Sigar> getSigar() {
        return Optional.ofNullable((Sigar) getContext().getObject(SIGAR_CTX_KEY));
    }
}
