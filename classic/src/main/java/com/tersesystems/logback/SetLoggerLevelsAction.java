package com.tersesystems.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigValue;
import org.xml.sax.Attributes;

import java.util.Map;
import java.util.Set;

/**
 * Sets the logger levels using typesafe config.
 */
public class SetLoggerLevelsAction extends Action {

    @Override
    public void begin(InterpretationContext ic, String name, Attributes attributes) throws ActionException {
        doConfigure();
    }

    @Override
    public void end(InterpretationContext ic, String name) throws ActionException {

    }

    public void doConfigure() {
        LoggerContext context = (LoggerContext) getContext();
        Config rootConfig = (Config) context.getObject(Constants.TYPESAFE_CONFIG);
        Config levelsConfig = rootConfig.getConfig("levels");
        Set<Map.Entry<String, ConfigValue>> levelsEntrySet = levelsConfig.entrySet();
        for (Map.Entry<String, ConfigValue> entry : levelsEntrySet) {
            String name = entry.getKey();
            try {
                String levelFromConfig = entry.getValue().unwrapped().toString();
                Logger logger = context.getLogger(name);
                logger.setLevel(Level.toLevel(levelFromConfig));
                addInfo("Setting level of " + name + " logger to " + levelFromConfig);
            } catch (ConfigException.Missing e) {
                addInfo("No custom setting found for " + name + " in config, ignoring");
            } catch (Exception e) {
                addError("Unexpected exception resolving " + name, e);
            }
        }

    }
}
