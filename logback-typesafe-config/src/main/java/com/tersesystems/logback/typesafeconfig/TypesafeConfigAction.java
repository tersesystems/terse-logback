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
package com.tersesystems.logback.typesafeconfig;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.action.ActionUtil;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.joran.spi.RuleStore;
import ch.qos.logback.core.util.OptionHelper;
import com.typesafe.config.*;
import org.xml.sax.Attributes;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tersesystems.logback.typesafeconfig.ConfigConstants.*;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * This class reads in configuration from a series of files using <a href="https://github.com/lightbend/config/blob/master/README.md">Typesafe Config</a>, an easy to use configuration
 * library.
 *
 * A property is resolved in the following resources in order of priority.  If there is no
 * setting found, it will fall back to the next available resource, which is
 *
 * <ul>
 *     <li>System Properties</li>
 *     <li>-Dterse.logback.configurationFile=somefile.conf</li>
 *     <li>logback.conf</li>
 *     <li>logback-test.conf</li>
 *     <li>logback-reference.conf</li>
 * </ul>
 *
 * The configuration will be available in the LoggerContext's object map, so you can use it
 * from your own code.
 *
 * <pre>{@code
 * LoggerContext context = (LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory();
 * com.typesafe.config.Config config = (com.typesafe.config.Config) context.getObject("config");
 * }</pre>
 *
 * This action will also set up the levels for a "setLoggingLevelsAction" used in terse-logback core.
 *
 * <pre>{@code
 * context.putObject(LEVELS_KEY, levelsMap);
 * }</pre>
 *
 * You may want to made subsections of config available to other actions and components.
 * You can use the {@code object} action for this.
 *
 * <pre>{@code
 *   <typesafeConfig>
 *     <object name="contextObjectFoo" path="some.random.path" scope="context"/>
 *   </typesafeConfig>
 * }</pre>
 *
 * which will do a {@code context.putObject("contextObjectFoo", pathValue); }
 */
public class TypesafeConfigAction extends Action {

    protected String scope = LOCAL_SCOPE;

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public void begin(InterpretationContext ic, String name, Attributes attributes) throws ActionException {
        RuleStore ruleStore = ic.getJoranInterpreter().getRuleStore();

        ruleStore.addRule(new ElementSelector("configuration/" + name + "/object"), new ContextObjectAction());

        String scope = attributes.getValue(SCOPE_ATTRIBUTE);
        if (scope != null) {
            setScope(scope);
        }
        String debugAttr = attributes.getValue(DEBUG_ATTRIBUTE);

        Config config = generateConfig(ic.getClass().getClassLoader(), Boolean.valueOf(debugAttr));
        Context context = ic.getContext();

        configureConfig(config);
        configureLevels(config);

        Set<Map.Entry<String, ConfigValue>> properties = config.getConfig(PROPERTIES_KEY).entrySet();
        if (isContextScope()) {
            configureContextScope(config, context, properties);
        } else {
            configureLocalScope(config, ic, properties);
        }
    }

    protected Map<String, String> levelsToMap(Config levelsConfig) {
        Map<String, String> levelsMap = new HashMap<>();
        Set<Map.Entry<String, ConfigValue>> levelsEntrySet = levelsConfig.entrySet();
        for (Map.Entry<String, ConfigValue> entry : levelsEntrySet) {
            String name = entry.getKey();
            try {
                String levelFromConfig = entry.getValue().unwrapped().toString();
                levelsMap.put(name, levelFromConfig);
            } catch (ConfigException.Missing e) {
                addInfo("No custom setting found for " + name + " in config, ignoring");
            } catch (Exception e) {
                addError("Unexpected exception resolving " + name, e);
            }
        }
        return levelsMap;
    }

    protected void configureConfig(Config config) {
        try {
            context.putObject(TYPESAFE_CONFIG_CTX_KEY, config);
        } catch (ConfigException e) {
            addWarn("Cannot set config in context!", e);
        }
    }

    protected void configureLevels(Config config) {
        // Try to set up the levels as they're important...
        try {
            Map<String, String> levelsMap = levelsToMap(config.getConfig(LEVELS_KEY));
            context.putObject(LEVELS_KEY, levelsMap);
        } catch (ConfigException e) {
            addWarn("Cannot set levels in context!", e);
        }
    }

    @Override
    public void end(InterpretationContext ic, String name) throws ActionException {

    }

    protected boolean isContextScope() {
        // ActionUtil.Scope scope = ActionUtil.stringToScope(scopeStr);
       return CONTEXT_SCOPE.equalsIgnoreCase(scope);
    }

    protected void configureContextScope(Config config, Context lc, Set<Map.Entry<String, ConfigValue>> properties) {
        addInfo("Configuring with context scope");
        lc.putObject(TYPESAFE_CONFIG_CTX_KEY, config);
        for (Map.Entry<String, ConfigValue> propertyEntry : properties) {
            String key = propertyEntry.getKey();
            String value = propertyEntry.getValue().unwrapped().toString();
            lc.putProperty(key, value);
        }
    }

    protected void configureLocalScope(Config config, InterpretationContext ic,  Set<Map.Entry<String, ConfigValue>> properties) {
        addInfo("Configuring with local scope");
        ic.getObjectMap().put(TYPESAFE_CONFIG_CTX_KEY, config);

        for (Map.Entry<String, ConfigValue> propertyEntry : properties) {
            String key = propertyEntry.getKey();
            String value = propertyEntry.getValue().unwrapped().toString();
            ic.addSubstitutionProperty(key, value);
        }
    }

    protected Config generateConfig(ClassLoader classLoader, boolean debug) {
        // Look for logback.json, logback.conf, logback.properties
        Config systemProperties = ConfigFactory.systemProperties();
        String fileName = System.getProperty(CONFIG_FILE_PROPERTY);
        Config file = ConfigFactory.empty();
        if (fileName != null) {
            file = ConfigFactory.parseFile(new File(fileName));
        }

        Config testResources = ConfigFactory.parseResourcesAnySyntax(classLoader, LOGBACK_TEST);
        Config resources = ConfigFactory.parseResourcesAnySyntax(classLoader, LOGBACK);
        Config reference = ConfigFactory.parseResources(classLoader, LOGBACK_REFERENCE_CONF);

        Config config = systemProperties        // Look for a property from system properties first...
                .withFallback(file)          // if we don't find it, then look in an explicitly defined file...
                .withFallback(testResources) // if not, then if logback-test.conf exists, look for it there...
                .withFallback(resources)     // then look in logback.conf...
                .withFallback(reference)     // and then finally in logback-reference.conf.
                .resolve();                  // Tell config that we want to use ${?ENV_VAR} type stuff.

        // Add a check to show the config value if nothing is working...
        if (debug) {
            String configString = config.root().render(ConfigRenderOptions.defaults());
            addInfo(configString);
        }
        return config;
    }


    /**
     * Lets you put objects into the context's object map, as the correct type,
     * using typesafe config paths as the source.
     */
    public static class ContextObjectAction extends Action {
        private String nameAttr;
        private String path;
        private ActionUtil.Scope scope;

        static ActionUtil.Scope stringToScope(String scopeStr) {
            if (ActionUtil.Scope.LOCAL.toString().equalsIgnoreCase(scopeStr))
                return ActionUtil.Scope.LOCAL;
            if (ActionUtil.Scope.CONTEXT.toString().equalsIgnoreCase(scopeStr))
                return ActionUtil.Scope.CONTEXT;

            // default to context.
            return ActionUtil.Scope.CONTEXT;
        }

        Config resolveConfig(InterpretationContext ic) {
            Config config = (Config) getContext().getObject(TYPESAFE_CONFIG_CTX_KEY);
            if (config == null) {
                config = (Config) ic.getObjectMap().get(TYPESAFE_CONFIG_CTX_KEY);
            }
            return config;
        }

        /**
         * Set a new property for the execution context by name, value pair, or adds
         * all the properties found in the given file.
         *
         */
        public void begin(InterpretationContext ic, String localName, Attributes attributes) {
            String nameAttr = attributes.getValue(NAME_ATTRIBUTE);
            setNameAttr(nameAttr);
            String path = attributes.getValue(PATH_ATTRIBUTE);
            setPath(path);

            ActionUtil.Scope scope = stringToScope(attributes.getValue(SCOPE_ATTRIBUTE));
            setScope(scope);
        }

        boolean isValid(String name, String value) {
            return ! (OptionHelper.isEmpty(name) || OptionHelper.isEmpty(value));
        }

        public void end(InterpretationContext ic, String name) {
            Config config = resolveConfig(ic);

            if (isValid(nameAttr, path)) {
                if (config == null) {
                    addError("No config object found in context's object map!");
                    return;
                }

                ConfigValue configValue = null;
                Object contextValue = null;
                try {
                    configValue = config.getValue(path);
                    if (configValue != null) {
                        contextValue = configValue.unwrapped();
                    }

                    switch (scope) {
                        case LOCAL:
                            ic.getObjectMap().put(nameAttr, contextValue);
                            break;
                        case CONTEXT:
                            context.putObject(nameAttr, contextValue);
                            break;
                        case SYSTEM:
                            // never used.
                            break;
                    }

                } catch (ConfigException e) {
                    addError(String.format("Cannot set value %s typesafe config path %s to name %s", contextValue, path, nameAttr), e);
                }
            } else {
                addError("Cannot set property, it is invalid!");
            }
        }

        private String getNameAttr() {
            return this.nameAttr;
        }

        void setNameAttr(String name) {
            this.nameAttr = name;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public void setScope(ActionUtil.Scope scope) {
            this.scope = scope;
        }
    }

}
