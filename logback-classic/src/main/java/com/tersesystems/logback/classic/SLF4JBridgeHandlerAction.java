package com.tersesystems.logback.classic;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import org.xml.sax.Attributes;

import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * Provides SLF4JBridgeHandler installation as an action.  This is useful
 * because it means you don't have to add custom code to your main method,
 * and can completely initialize JUL by adding this.
 *
 * Easiest way to do this is to use a custom rule:
 *
 * "&lt;newRule pattern="configuration/slf4jBridgeHandler"
 *   actionClass="com.tersesystems.logback.classic.SLF4JBridgeHandlerAction"/&gt;"
 *
 * and then call it:
 *
 * "&lt;slf4jBridgeHandler/&gt;"
 *
 * You should use this in conjunction with the "LevelChangePropagator":
 *
 * "&lt;contextListener class="ch.qos.logback.classic.jul.LevelChangePropagator"/&gt;"
 *
 */
public class SLF4JBridgeHandlerAction extends Action {

    @Override
    public void begin(InterpretationContext ic, String name, Attributes attributes) throws ActionException {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    @Override
    public void end(InterpretationContext ic, String name) throws ActionException {

    }
}
