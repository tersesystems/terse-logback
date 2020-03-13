package com.tersesystems.logback.classic;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.tersesystems.logback.core.ComponentContainer;

/** A logging event that is a container of components. */
public interface IContainerLoggingEvent extends ILoggingEvent, ComponentContainer {}
