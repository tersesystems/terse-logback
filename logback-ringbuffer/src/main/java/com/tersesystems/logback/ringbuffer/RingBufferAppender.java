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
package com.tersesystems.logback.ringbuffer;

import ch.qos.logback.core.Appender;

/**
 * Indicates that this appender appends to a ring buffer.
 *
 * @param <E>
 */
public interface RingBufferAppender<E> extends Appender<E>, RingBufferAware {}
