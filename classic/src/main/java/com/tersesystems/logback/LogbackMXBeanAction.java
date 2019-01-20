package com.tersesystems.logback;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import org.xml.sax.Attributes;

public class LogbackMXBeanAction extends Action {

    @Override
    public void begin(InterpretationContext ec, String name, Attributes attributes) throws ActionException {
        addInfo("begin");

        LoggerContext lc = (LoggerContext) this.context;
        LogbackMXBean possiblyNullBean = LogbackMXBean.register(lc);
        if (possiblyNullBean != null) {
            lc.addListener(possiblyNullBean);
        }
    }

    @Override
    public void end(InterpretationContext ec, String name) throws ActionException {

    }

}
