package com.tersesystems.logback.context;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public interface ISelfLoggerFactory<L extends Logger> extends ILoggerFactory {
    @SuppressWarnings("unchecked")
    default L getLogger(Class<?> clazz) {
        return (L) getLogger(clazz.getName());
    }
}
