package com.tersesystems.logback.core;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.spi.AppenderAttachableImpl;

import java.util.Iterator;

public interface DefaultAppenderAttachable<E> extends AppenderAttachable<E> {

    AppenderAttachableImpl<E> appenderAttachableImpl();

    default void addAppender(Appender<E> newAppender) {
        appenderAttachableImpl().addAppender(newAppender);
    }

    default Iterator<Appender<E>> iteratorForAppenders() {
        return appenderAttachableImpl().iteratorForAppenders();
    }

    default Appender<E> getAppender(String name) {
        return appenderAttachableImpl().getAppender(name);
    }

    default boolean isAttached(Appender<E> eAppender) {
        return appenderAttachableImpl().isAttached(eAppender);
    }

    default void detachAndStopAllAppenders() {
        appenderAttachableImpl().detachAndStopAllAppenders();
    }

    default boolean detachAppender(Appender<E> eAppender) {
        return appenderAttachableImpl().detachAppender(eAppender);
    }

    default boolean detachAppender(String name) {
        return appenderAttachableImpl().detachAppender(name);
    }

}
