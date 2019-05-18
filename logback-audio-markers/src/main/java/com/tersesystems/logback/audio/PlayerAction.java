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
package com.tersesystems.logback.audio;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;

public class PlayerAction extends Action {
    Player player;
    private boolean inError = false;

    @Override
    public void begin(InterpretationContext ic, String localName, Attributes attributes) throws ActionException {
        String className = attributes.getValue(CLASS_ATTRIBUTE);
        if (OptionHelper.isEmpty(className)) {
            addError("Missing class name for player. Near [" + localName + "] line " + getLineNumber(ic));
            inError = true;
            return;
        }

        try {
            addInfo("About to instantiate player of type [" + className + "]");
            player = (Player) OptionHelper.instantiateByClassName(className, Player.class, context);

            Context icContext = ic.getContext();
            if (player instanceof ContextAwareBase) {
                ((ContextAwareBase) player).setContext(icContext);
            }

            ic.pushObject(player);
        } catch (Exception oops) {
            inError = true;
            addError("Could not create player.", oops);
            throw new ActionException(oops);
        }
    }

    @Override
    public void end(InterpretationContext ic, String name) throws ActionException {
        if (inError) {
            return;
        }

        if (player instanceof LifeCycle) {
            ((LifeCycle) player).start();
        }

        Object o = ic.peekObject();
        if (o != player) {
            addWarn("The object at the end of the stack is not the player pushed earlier.");
        } else {
            ic.popObject();
        }
    }
}
