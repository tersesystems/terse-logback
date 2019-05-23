package com.tersesystems.logback;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.spi.AppenderAttachableImpl;

import java.util.Iterator;

public abstract class EnrichingAppender<E, EE extends E> extends UnsynchronizedAppenderBase<E> implements AppenderAttachable<EE> {

    protected AppenderAttachableImpl<EE> aai = new AppenderAttachableImpl<EE>();

    protected abstract EE enrichEvent(E eventObject);

    @Override
    protected void append(E eventObject) {
        aai.appendLoopOnAppenders(enrichEvent(eventObject));
    }

    public void addAppender(Appender<EE> newAppender) {
        addInfo("Attaching appender named [" + newAppender.getName() + "] to " + this.toString());
        aai.addAppender(newAppender);
    }

    public Iterator<Appender<EE>> iteratorForAppenders() {
        return aai.iteratorForAppenders();
    }

    public Appender<EE> getAppender(String name) {
        return aai.getAppender(name);
    }

    public boolean isAttached(Appender<EE> eAppender) {
        return aai.isAttached(eAppender);
    }

    public void detachAndStopAllAppenders() {
        aai.detachAndStopAllAppenders();
    }

    public boolean detachAppender(Appender<EE> eAppender) {
        return aai.detachAppender(eAppender);
    }

    public boolean detachAppender(String name) {
        return aai.detachAppender(name);
    }
}
